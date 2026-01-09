package ch.heigvd.iict.and.rest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: Contact) : Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    /**
     * Récupère tous les contacts non supprimés
     */
    @Query("SELECT * FROM Contact WHERE syncState != 'TO_DELETE'")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id : Long) : Contact?

    /**
     * Récupère un contact par son remoteId
     */
    @Query("SELECT * FROM Contact WHERE remoteId = :remoteId")
    suspend fun getContactByRemoteId(remoteId: Long): Contact?

    @Query("SELECT COUNT(*) FROM Contact")
    fun getCount() : Int

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

    /**
     * Récupère tous les contacts à synchroniser
     */
    @Query("SELECT * FROM Contact WHERE syncState = 'TO_SYNC'")
    suspend fun getContactsToSync(): List<Contact>

    /**
     * Récupère tous les contacts à supprimer sur le serveur
     */
    @Query("SELECT * FROM Contact WHERE syncState = 'TO_DELETE'")
    suspend fun getContactsToDelete(): List<Contact>

    /**
     * Marque un contact comme à supprimer (soft delete)
     */
    @Query("UPDATE Contact SET syncState = 'TO_DELETE' WHERE id = :id")
    suspend fun markAsDeleted(id: Long)

    /**
     * Supprime définitivement un contact
     */
    @Query("DELETE FROM Contact WHERE id = :id")
    suspend fun hardDelete(id: Long)

}