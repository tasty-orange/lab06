package ch.heigvd.iict.and.rest.models

/**
 * États de synchronisation d'un contact avec le serveur REST
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
enum class SyncState {
    /**
     * Contact synchronisé avec le serveur.
     */
    SYNCED,

    /**
     * Contact créé ou modifié localement, pas encore synchronisé.
     */
    TO_SYNC,

    /**
     * Contact supprimé localement (soft delete), à supprimer sur le serveur.
     */
    TO_DELETE
}