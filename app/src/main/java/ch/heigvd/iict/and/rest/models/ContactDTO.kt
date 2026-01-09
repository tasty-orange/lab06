package ch.heigvd.iict.and.rest.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Représentation JSON d'un contact pour l'API
 */
data class ContactDTO(
    val id: Long?,
    val name: String,
    val firstname: String?,
    val birthday: String?,  // Format ISO: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    val email: String?,
    val address: String?,
    val zip: String?,
    val city: String?,
    val type: String?,  // HOME, OFFICE, MOBILE, FAX
    val phoneNumber: String?
) {
    companion object {
        private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        /**
         * Convertit un Contact local en ContactDTO pour l'API
         */
        fun fromContact(contact: Contact): ContactDTO {
            return ContactDTO(
                id = contact.remoteId,
                name = contact.name,
                firstname = contact.firstname,
                birthday = contact.birthday?.let { isoFormat.format(it.time) },
                email = contact.email,
                address = contact.address,
                zip = contact.zip,
                city = contact.city,
                type = contact.type?.name,
                phoneNumber = contact.phoneNumber
            )
        }

        /**
         * Convertit un ContactDTO de l'API en Contact local
         */
        fun toContact(dto: ContactDTO, localId: Long? = null, syncState: SyncState = SyncState.SYNCED): Contact {
            val birthday = dto.birthday?.let { dateStr ->
                Calendar.getInstance().apply {
                    time = isoFormat.parse(dateStr) ?: Date()
                }
            }

            return Contact(
                id = localId,
                remoteId = dto.id,
                name = dto.name,
                firstname = dto.firstname,
                birthday = birthday,
                email = dto.email,
                address = dto.address,
                zip = dto.zip,
                city = dto.city,
                type = dto.type?.let { PhoneType.valueOf(it) },
                phoneNumber = dto.phoneNumber,
                syncState = syncState
            )
        }
    }
}