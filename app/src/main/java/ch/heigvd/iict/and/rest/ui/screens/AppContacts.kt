package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel

/**
 * Composable principal de l'application de gestion de contacts
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 *
 * @param contactsViewModel ContactsViewModel - ViewModel partagé (créé automatiquement)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContact(contactsViewModel: ContactsViewModel = viewModel()) {

    val contacts: List<Contact> by contactsViewModel.allContacts.collectAsStateWithLifecycle()
    val isEditing: Boolean by contactsViewModel.isEditing.collectAsStateWithLifecycle()
    val selectedContact: Contact? by contactsViewModel.selectedContact.collectAsStateWithLifecycle()
    val statusMessage: String? by contactsViewModel.statusMessage.collectAsStateWithLifecycle()
    val isLoading: Boolean by contactsViewModel.isLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            contactsViewModel.clearStatusMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    // Bouton Populate (Enroll)
                    IconButton(onClick = {
                        contactsViewModel.enroll()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.populate),
                            contentDescription = "Enroll"
                        )
                    }
                    // Bouton Synchronize
                    IconButton(onClick = {
                        contactsViewModel.refresh()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.synchronize),
                            contentDescription = "Synchronize"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (!isEditing) {
                FloatingActionButton(onClick = {
                    contactsViewModel.createNewContact()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Add contact"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Affichage conditionnel selon le mode (liste ou édition)
            if (isEditing) {
                // Mode édition : affiche le formulaire
                ScreenContactEditor(
                    contact = selectedContact,
                    onSave = { contact ->
                        contactsViewModel.saveContact(contact)
                    },
                    onDelete = { contact ->
                        contactsViewModel.deleteContact(contact)
                    },
                    onCancel = {
                        contactsViewModel.cancelEditing()
                    }
                )
            } else {
                // Mode liste : affiche tous les contacts
                ScreenContactList(contacts) { selectedContact ->
                    contactsViewModel.editContact(selectedContact)
                }
            }

            // Indicateur de chargement par-dessus l'écran actuel
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}