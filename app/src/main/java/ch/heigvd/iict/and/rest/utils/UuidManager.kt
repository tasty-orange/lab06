package ch.heigvd.iict.and.rest.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestionnaire pour stocker et récupérer l'UUID utilisateur
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 *
 * @param context Context - Contexte Android pour accéder aux SharedPreferences
 */
class UuidManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ContactsPrefs"
        private const val KEY_UUID = "user_uuid"
    }

    /**
     * Sauvegarde l'UUID utilisateur dans SharedPreferences.
     *
     * @param uuid String - UUID attribué par le serveur lors de l'enrollment
     */
    fun saveUuid(uuid: String) {
        prefs.edit().putString(KEY_UUID, uuid).apply()
    }

    /**
     * Récupère l'UUID utilisateur depuis SharedPreferences.
     *
     * @return String? - UUID stocké ou null si aucun UUID n'est enregistré
     */
    fun getUuid(): String? {
        return prefs.getString(KEY_UUID, null)
    }

    /**
     * Supprime l'UUID des SharedPreferences.
     */
    fun clearUuid() {
        prefs.edit().remove(KEY_UUID).apply()
    }

    /**
     * Vérifie si un UUID est actuellement enregistré.
     *
     * @return Boolean - true si un UUID existe, false sinon
     */
    fun hasUuid(): Boolean {
        return getUuid() != null
    }
}