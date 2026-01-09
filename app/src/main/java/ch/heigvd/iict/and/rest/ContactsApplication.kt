package ch.heigvd.iict.and.rest

import android.app.Application
import ch.heigvd.iict.and.rest.api.RetrofitClient
import ch.heigvd.iict.and.rest.database.ContactsDatabase
import ch.heigvd.iict.and.rest.utils.UuidManager

class ContactsApplication : Application() {

    // Database
    private val database by lazy { ContactsDatabase.getDatabase(this) }

    // API Service (Retrofit)
    private val apiService by lazy { RetrofitClient.apiService }

    // UUID Manager
    private val uuidManager by lazy { UuidManager(this) }

    // Repository
    val repository by lazy {
        ContactsRepository(
            database.contactsDao(),
            apiService,
            uuidManager
        )
    }
}