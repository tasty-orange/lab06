package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Contact(@PrimaryKey(autoGenerate = true) var id: Long? = null,
              var remoteId: Long? = null,  // ID du contact sur le serveur
              var name: String,
              var firstname: String?,
              var birthday : Calendar?,
              var email: String?,
              var address: String?,
              var zip: String?,
              var city: String?,
              var type: PhoneType?,
              var phoneNumber: String?,
              var syncState: SyncState = SyncState.SYNCED  ) // État de synchronisation