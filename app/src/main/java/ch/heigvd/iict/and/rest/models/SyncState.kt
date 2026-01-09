package ch.heigvd.iict.and.rest.models

/**
 * États de synchronisation d'un contact
 */
enum class SyncState {
    /**
     * Contact synchronisé avec le serveur
     */
    SYNCED,

    /**
     * Contact créé/modifié localement, pas encore synchronisé
     */
    TO_SYNC,

    /**
     * Contact supprimé localement, à supprimer sur le serveur
     */
    TO_DELETE
}