package ch.heigvd.iict.and.rest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel pour la gestion des contacts et de l'état de l'interface
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 *
 * @param app Application - Instance de l'application pour accéder au Repository
 */
class ContactsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (application as ContactsApplication).repository

    /**
     * Liste de tous les contacts, mise à jour automatiquement.
     */
    val allContacts: StateFlow<List<Contact>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    /**
     * Contact actuellement sélectionné pour édition.
     */
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    /**
     * Mode édition de l'interface.
     */
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    /**
     * Message de statut à afficher temporairement.
     */
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    /**
     * Indicateur de chargement pour les opérations asynchrones.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Lance l'enrollment utilisateur.
     */
    fun enroll() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Enrollment en cours..."

            val success = repository.enroll()

            _isLoading.value = false
            _statusMessage.value = if (success) {
                "Enrollment réussi !"
            } else {
                "Erreur lors de l'enrollment"
            }

            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * Lance la synchronisation complète de tous les contacts "dirty".
     */
    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = "Synchronisation en cours..."

            val success = repository.syncAll()

            _isLoading.value = false
            _statusMessage.value = if (success) {
                "Synchronisation réussie !"
            } else {
                "Erreur lors de la synchronisation"
            }

            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * Prépare l'interface pour créer un nouveau contact.
     */
    fun createNewContact() {
        _selectedContact.value = null
        _isEditing.value = true
    }

    /**
     * Prépare l'interface pour éditer un contact existant.
     *
     * @param contact Contact à éditer
     */
    fun editContact(contact: Contact) {
        _selectedContact.value = contact
        _isEditing.value = true
    }

    /**
     * Sauvegarde un contact (création ou modification).
     *
     * @param contact Contact à sauvegarder
     */
    fun saveContact(contact: Contact) {
        viewModelScope.launch {
            _isLoading.value = true

            val success = if (contact.id == null) {
                // Nouveau contact
                repository.createContact(contact)
            } else {
                // Mise à jour
                repository.updateContact(contact)
            }

            _isLoading.value = false

            if (success) {
                _statusMessage.value = "Contact sauvegardé !"
                _isEditing.value = false
                _selectedContact.value = null
            } else {
                _statusMessage.value = "Erreur lors de la sauvegarde"
            }

            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * Supprime un contact.
     *
     * @param contact Contact à supprimer
     */
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            _isLoading.value = true

            val success = repository.deleteContact(contact)

            _isLoading.value = false

            if (success) {
                _statusMessage.value = "Contact supprimé !"
                _isEditing.value = false
                _selectedContact.value = null
            } else {
                _statusMessage.value = "Erreur lors de la suppression"
            }

            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * Annule l'édition en cours et retourne à la liste.
     */
    fun cancelEditing() {
        _isEditing.value = false
        _selectedContact.value = null
    }

    /**
     * Efface manuellement le message de statut.
     */
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}