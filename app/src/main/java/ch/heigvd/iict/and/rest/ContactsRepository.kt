package ch.heigvd.iict.and.rest

import android.util.Log
import ch.heigvd.iict.and.rest.api.ContactsApiService
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactDTO
import ch.heigvd.iict.and.rest.models.SyncState
import ch.heigvd.iict.and.rest.utils.UuidManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ContactsRepository(
    private val contactsDao: ContactsDao,
    private val apiService: ContactsApiService,
    private val uuidManager: UuidManager
) {

    companion object {
        private const val TAG = "ContactsRepository"
    }

    val allContacts: Flow<List<Contact>> = contactsDao.getAllContacts()

    /**
     * ENROLLMENT : Créer un nouvel utilisateur, obtenir UUID et synchroniser
     */
    suspend fun enroll(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting enrollment...")

            // 1. Supprimer toutes les données locales
            contactsDao.clearAllContacts()
            uuidManager.clearUuid()

            // 2. Obtenir un nouvel UUID du serveur
            val response = apiService.enroll()
            if (!response.isSuccessful || response.body() == null) {
                Log.e(TAG, "Failed to get UUID from server: ${response.code()}")
                return@withContext false
            }

            val uuid = response.body()!!

            // 3. Sauvegarder l'UUID
            uuidManager.saveUuid(uuid)
            Log.d(TAG, "UUID saved: $uuid")

            // 4. Récupérer les contacts par défaut du serveur
            val contactsResponse = apiService.getAllContacts(uuid)
            if (!contactsResponse.isSuccessful || contactsResponse.body() == null) {
                Log.e(TAG, "Failed to get contacts from server: ${contactsResponse.code()}")
                return@withContext false
            }

            val contacts = contactsResponse.body()!!

            // 5. Stocker les contacts en local
            contacts.forEach { dto ->
                val contact = ContactDTO.toContact(dto, syncState = SyncState.SYNCED)
                contactsDao.insert(contact)
            }

            Log.d(TAG, "Enrollment successful - ${contacts.size} contacts imported")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Enrollment error", e)
            false
        }
    }

    /**
     * CRÉER un nouveau contact
     */
    suspend fun createContact(contact: Contact): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Insérer en local avec état TO_SYNC
            contact.syncState = SyncState.TO_SYNC
            val localId = contactsDao.insert(contact)
            Log.d(TAG, "Contact created locally with ID: $localId")

            // 2. Essayer de créer sur le serveur
            val uuid = uuidManager.getUuid()
            if (uuid != null) {
                try {
                    val dto = ContactDTO.fromContact(contact)
                    val response = apiService.createContact(uuid, dto)

                    if (response.isSuccessful && response.body() != null) {
                        val createdDto = response.body()!!
                        val updatedContact = contact.copy(
                            id = localId,
                            remoteId = createdDto.id,
                            syncState = SyncState.SYNCED
                        )
                        contactsDao.update(updatedContact)
                        Log.d(TAG, "Contact created successfully on server with remoteId: ${createdDto.id}")
                    } else {
                        Log.w(TAG, "Contact created locally but server returned: ${response.code()}")
                    }
                } catch (networkError: Exception) {
                    // Erreur réseau (mode hors-ligne) - ce n'est PAS une erreur critique
                    Log.w(TAG, "Contact created locally, will sync later: ${networkError.message}")
                }
            } else {
                Log.w(TAG, "No UUID - contact created locally only")
            }

            // ✅ Toujours retourner true si la création locale a réussi
            true

        } catch (e: Exception) {
            // Seules les erreurs de base de données sont critiques
            Log.e(TAG, "Failed to create contact locally", e)
            false
        }
    }

    /**
     * METTRE À JOUR un contact existant
     */
    suspend fun updateContact(contact: Contact): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Marquer comme TO_SYNC et mettre à jour en local
            contact.syncState = SyncState.TO_SYNC
            contactsDao.update(contact)
            Log.d(TAG, "Contact updated locally")

            // 2. Essayer de mettre à jour sur le serveur
            val uuid = uuidManager.getUuid()
            if (uuid != null && contact.remoteId != null) {
                try {
                    val dto = ContactDTO.fromContact(contact)
                    val response = apiService.updateContact(uuid, contact.remoteId!!, dto)

                    if (response.isSuccessful) {
                        contact.syncState = SyncState.SYNCED
                        contactsDao.update(contact)
                        Log.d(TAG, "Contact updated successfully on server")
                    } else {
                        Log.w(TAG, "Contact updated locally but server returned: ${response.code()}")
                    }
                } catch (networkError: Exception) {
                    Log.w(TAG, "Contact updated locally, will sync later: ${networkError.message}")
                }
            } else {
                Log.w(TAG, "No UUID or remoteId - contact updated locally only")
            }

            // ✅ Toujours retourner true si la mise à jour locale a réussi
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to update contact locally", e)
            false
        }
    }

    /**
     * SUPPRIMER un contact
     */
    suspend fun deleteContact(contact: Contact): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Marquer comme TO_DELETE en local (soft delete)
            contactsDao.markAsDeleted(contact.id!!)
            Log.d(TAG, "Contact marked as deleted locally")

            // 2. Essayer de supprimer sur le serveur
            val uuid = uuidManager.getUuid()
            if (uuid != null && contact.remoteId != null) {
                try {
                    val response = apiService.deleteContact(uuid, contact.remoteId!!)

                    if (response.isSuccessful) {
                        // Suppression réussie, supprimer définitivement en local
                        contactsDao.hardDelete(contact.id!!)
                        Log.d(TAG, "Contact deleted successfully on server")
                    } else {
                        Log.w(TAG, "Contact marked as deleted locally but server returned: ${response.code()}")
                    }
                } catch (networkError: Exception) {
                    Log.w(TAG, "Contact marked as deleted locally, will sync later: ${networkError.message}")
                }
            } else {
                // Pas d'ID serveur, supprimer directement
                contactsDao.hardDelete(contact.id!!)
                Log.d(TAG, "Contact deleted locally (no server sync needed)")
            }

            // ✅ Toujours retourner true si l'opération locale a réussi
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete contact locally", e)
            false
        }
    }

    /**
     * SYNCHRONISATION COMPLÈTE de tous les contacts
     */
    suspend fun syncAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            val uuid = uuidManager.getUuid()
            if (uuid == null) {
                Log.e(TAG, "Cannot sync - no UUID")
                return@withContext false
            }

            Log.d(TAG, "Starting full synchronization...")
            var successCount = 0
            var failCount = 0

            // 1. Synchroniser les contacts à supprimer
            val contactsToDelete = contactsDao.getContactsToDelete()
            Log.d(TAG, "Syncing ${contactsToDelete.size} contacts to delete")

            contactsToDelete.forEach { contact ->
                if (contact.remoteId != null) {
                    try {
                        val response = apiService.deleteContact(uuid, contact.remoteId!!)
                        if (response.isSuccessful) {
                            contactsDao.hardDelete(contact.id!!)
                            successCount++
                            Log.d(TAG, "Deleted contact ${contact.remoteId} on server")
                        } else {
                            failCount++
                            Log.w(TAG, "Failed to delete contact ${contact.remoteId}: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        failCount++
                        Log.w(TAG, "Failed to delete contact ${contact.remoteId}: ${e.message}")
                    }
                } else {
                    // Pas d'ID serveur, juste supprimer localement
                    contactsDao.hardDelete(contact.id!!)
                    successCount++
                }
            }

            // 2. Synchroniser les contacts modifiés/créés
            val contactsToSync = contactsDao.getContactsToSync()
            Log.d(TAG, "Syncing ${contactsToSync.size} contacts to sync")

            contactsToSync.forEach { contact ->
                try {
                    val dto = ContactDTO.fromContact(contact)

                    if (contact.remoteId == null) {
                        // Nouveau contact à créer
                        val response = apiService.createContact(uuid, dto)
                        if (response.isSuccessful && response.body() != null) {
                            val createdDto = response.body()!!
                            val updatedContact = contact.copy(
                                remoteId = createdDto.id,
                                syncState = SyncState.SYNCED
                            )
                            contactsDao.update(updatedContact)
                            successCount++
                            Log.d(TAG, "Created contact ${createdDto.id} on server")
                        } else {
                            failCount++
                            Log.w(TAG, "Failed to create contact ${contact.name}: ${response.code()}")
                        }
                    } else {
                        // Contact existant à mettre à jour
                        val response = apiService.updateContact(uuid, contact.remoteId!!, dto)
                        if (response.isSuccessful) {
                            contact.syncState = SyncState.SYNCED
                            contactsDao.update(contact)
                            successCount++
                            Log.d(TAG, "Updated contact ${contact.remoteId} on server")
                        } else {
                            failCount++
                            Log.w(TAG, "Failed to update contact ${contact.remoteId}: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    failCount++
                    Log.w(TAG, "Failed to sync contact ${contact.name}: ${e.message}")
                }
            }

            Log.d(TAG, "Synchronization complete - Success: $successCount, Failed: $failCount")
            failCount == 0

        } catch (e: Exception) {
            Log.e(TAG, "Synchronization error", e)
            false
        }
    }
}