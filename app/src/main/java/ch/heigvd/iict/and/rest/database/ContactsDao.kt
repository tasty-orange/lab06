package ch.heigvd.iict.and.rest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pour les opérations sur la table Contact
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
@Dao
interface ContactsDao {

    /**
     * Insère un nouveau contact dans la base de données.
     *
     * @param contact Contact à insérer
     * @return Long - ID local généré automatiquement par Room
     */
    @Insert
    fun insert(contact: Contact): Long

    /**
     * Met à jour un contact existant dans la base de données.
     *
     * @param contact Contact à mettre à jour
     */
    @Update
    fun update(contact: Contact)

    /**
     * Supprime un contact de la base de données (rarement utilisé).
     *
     * @param contact Contact à supprimer
     */
    @Delete
    fun delete(contact: Contact)

    /**
     * Récupère tous les contacts non supprimés sous forme de Flow.
     *
     * @return Flow<List<Contact>> - Flow observable des contacts actifs
     */
    @Query("SELECT * FROM Contact WHERE syncState != 'TO_DELETE'")
    fun getAllContacts(): Flow<List<Contact>>

    /**
     * Récupère un contact par son ID local.
     *
     * @param id Long - ID local du contact
     * @return Contact? - Contact trouvé ou null
     */
    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id: Long): Contact?

    /**
     * Récupère un contact par son ID serveur (remoteId).
     *
     * @param remoteId Long - ID du contact sur le serveur
     * @return Contact? - Contact trouvé ou null
     */
    @Query("SELECT * FROM Contact WHERE remoteId = :remoteId")
    suspend fun getContactByRemoteId(remoteId: Long): Contact?

    /**
     * Compte le nombre total de contacts (y compris TO_DELETE).
     *
     * @return Int - Nombre de contacts en base
     */
    @Query("SELECT COUNT(*) FROM Contact")
    fun getCount(): Int

    /**
     * Supprime tous les contacts de la base de données.
     */
    @Query("DELETE FROM Contact")
    fun clearAllContacts()

    /**
     * Récupère tous les contacts qui doivent être synchronisés.
     *
     * @return List<Contact> - Liste des contacts avec syncState = TO_SYNC
     */
    @Query("SELECT * FROM Contact WHERE syncState = 'TO_SYNC'")
    suspend fun getContactsToSync(): List<Contact>

    /**
     * Récupère tous les contacts qui doivent être supprimés sur le serveur.
     *
     * @return List<Contact> - Liste des contacts avec syncState = TO_DELETE
     */
    @Query("SELECT * FROM Contact WHERE syncState = 'TO_DELETE'")
    suspend fun getContactsToDelete(): List<Contact>

    /**
     * Marque un contact comme à supprimer (soft delete).
     *
     * @param id Long - ID local du contact à marquer
     */
    @Query("UPDATE Contact SET syncState = 'TO_DELETE' WHERE id = :id")
    suspend fun markAsDeleted(id: Long)

    /**
     * Supprime définitivement un contact de la base de données (hard delete).
     *
     * @param id Long - ID local du contact à supprimer définitivement
     */
    @Query("DELETE FROM Contact WHERE id = :id")
    suspend fun hardDelete(id: Long)
}