package ch.heigvd.iict.and.rest.database.converters

import androidx.room.TypeConverter
import ch.heigvd.iict.and.rest.models.SyncState

class SyncStateConverter {

    @TypeConverter
    fun fromSyncState(state: SyncState): String {
        return state.name
    }

    @TypeConverter
    fun toSyncState(value: String): SyncState {
        return SyncState.valueOf(value)
    }
}