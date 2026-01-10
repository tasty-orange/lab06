package ch.heigvd.iict.and.rest

import android.app.Application
import ch.heigvd.iict.and.rest.api.RetrofitClient
import ch.heigvd.iict.and.rest.database.ContactsDatabase
import ch.heigvd.iict.and.rest.utils.UuidManager

/**
 * Classe Application pour l'application de gestion de contacts
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
class ContactsApplication : Application() {

    /**
     * Instance de la base de données Room.
     */
    private val database by lazy { ContactsDatabase.getDatabase(this) }

    /**
     * Service API Retrofit pour les communications REST.
     */
    private val apiService by lazy { RetrofitClient.apiService }

    /**
     * Gestionnaire d'UUID utilisateur.
     */
    private val uuidManager by lazy { UuidManager(this) }

    /**
     * Repository unique pour toute l'application.
     */
    val repository by lazy {
        ContactsRepository(
            database.contactsDao(),
            apiService,
            uuidManager
        )
    }
}