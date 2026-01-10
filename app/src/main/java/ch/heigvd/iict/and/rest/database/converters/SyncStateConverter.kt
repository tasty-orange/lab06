package ch.heigvd.iict.and.rest.database.converters

import androidx.room.TypeConverter
import ch.heigvd.iict.and.rest.models.SyncState

/**
 * Convertisseur de type pour Room permettant de stocker SyncState en base de données
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
class SyncStateConverter {

    /**
     * Convertit un enum SyncState en String pour le stockage en base.
     *
     * @param state SyncState - Enum à convertir
     * @return String - Nom de l'enum (ex: "SYNCED", "TO_SYNC", "TO_DELETE")
     */
    @TypeConverter
    fun fromSyncState(state: SyncState): String {
        return state.name
    }

    /**
     * Convertit un String de la base en enum SyncState.
     *
     * @param value String - Nom de l'enum stocké en base
     * @return SyncState - Enum correspondant
     * @throws IllegalArgumentException si la valeur ne correspond à aucun état
     */
    @TypeConverter
    fun toSyncState(value: String): SyncState {
        return SyncState.valueOf(value)
    }
}