package ch.heigvd.iict.and.rest.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (application as ContactsApplication).repository

    // Liste de tous les contacts (Flow automatiquement mis à jour)
    val allContacts: StateFlow<List<Contact>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Contact sélectionné pour édition (null = création d'un nouveau)
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    // Mode édition (true = formulaire, false = liste)
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // Messages de statut à afficher
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Indicateur de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * ENROLLMENT : Créer un nouvel utilisateur
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

            // Effacer le message après 3 secondes
            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * SYNCHRONISATION complète
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

            // Effacer le message après 3 secondes
            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * Ouvrir l'écran de CRÉATION d'un nouveau contact
     */
    fun createNewContact() {
        _selectedContact.value = null
        _isEditing.value = true
    }

    /**
     * Ouvrir l'écran d'ÉDITION d'un contact existant
     */
    fun editContact(contact: Contact) {
        _selectedContact.value = contact
        _isEditing.value = true
    }

    /**
     * SAUVEGARDER un contact (création ou mise à jour)
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

            // Effacer le message après 3 secondes
            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * SUPPRIMER un contact
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

            // Effacer le message après 3 secondes
            kotlinx.coroutines.delay(3000)
            _statusMessage.value = null
        }
    }

    /**
     * ANNULER l'édition et retourner à la liste
     */
    fun cancelEditing() {
        _isEditing.value = false
        _selectedContact.value = null
    }

    /**
     * Effacer le message de statut
     */
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}