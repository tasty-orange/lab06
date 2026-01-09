package ch.heigvd.iict.and.rest.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestionnaire pour stocker et récupérer l'UUID utilisateur
 * Utilise SharedPreferences pour la persistance
 */
class UuidManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ContactsPrefs"
        private const val KEY_UUID = "user_uuid"
    }

    /**
     * Sauvegarde l'UUID utilisateur
     */
    fun saveUuid(uuid: String) {
        prefs.edit().putString(KEY_UUID, uuid).apply()
    }

    /**
     * Récupère l'UUID utilisateur
     * @return UUID ou null si non défini
     */
    fun getUuid(): String? {
        return prefs.getString(KEY_UUID, null)
    }

    /**
     * Supprime l'UUID (pour un nouvel enrollment)
     */
    fun clearUuid() {
        prefs.edit().remove(KEY_UUID).apply()
    }

    /**
     * Vérifie si un UUID est enregistré
     */
    fun hasUuid(): Boolean {
        return getUuid() != null
    }
}