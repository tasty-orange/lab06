package ch.heigvd.iict.and.rest.models

import java.text.SimpleDateFormat
import java.util.*

/**
 * Objet de transfert de données (DTO) pour les échanges JSON avec l'API REST
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 *
 * @property id Long? - ID du contact sur le serveur (null lors de création)
 * @property name String - Nom du contact (obligatoire)
 * @property firstname String? - Prénom du contact
 * @property birthday String? - Date de naissance au format ISO 8601
 * @property email String? - Adresse email
 * @property address String? - Adresse postale
 * @property zip String? - Code postal
 * @property city String? - Ville
 * @property type String? - Type de téléphone (HOME, OFFICE, MOBILE, FAX)
 * @property phoneNumber String? - Numéro de téléphone
 */
data class ContactDTO(
    val id: Long?,
    val name: String,
    val firstname: String?,
    val birthday: String?,
    val email: String?,
    val address: String?,
    val zip: String?,
    val city: String?,
    val type: String?,
    val phoneNumber: String?
) {
    companion object {
        private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

        /**
         * Convertit un Contact local (entity Room) en ContactDTO pour l'API.
         *
         * @param contact Contact - Contact local à convertir
         * @return ContactDTO - Objet prêt pour sérialisation JSON
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
         * Convertit un ContactDTO de l'API en Contact local (entity Room).
         *
         * @param dto ContactDTO - DTO reçu du serveur
         * @param localId Long? - ID local existant (null pour nouveau contact)
         * @param syncState SyncState - État de synchronisation à attribuer (SYNCED par défaut)
         * @return Contact - Entity Room prête pour insertion/mise à jour
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