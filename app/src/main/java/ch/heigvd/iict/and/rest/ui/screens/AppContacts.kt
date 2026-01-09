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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContact(contactsViewModel: ContactsViewModel = viewModel()) {
    // Observer les états du ViewModel
    val contacts: List<Contact> by contactsViewModel.allContacts.collectAsStateWithLifecycle()
    val isEditing: Boolean by contactsViewModel.isEditing.collectAsStateWithLifecycle()
    val selectedContact: Contact? by contactsViewModel.selectedContact.collectAsStateWithLifecycle()
    val statusMessage: String? by contactsViewModel.statusMessage.collectAsStateWithLifecycle()
    val isLoading: Boolean by contactsViewModel.isLoading.collectAsStateWithLifecycle()

    // Snackbar pour les messages de statut
    val snackbarHostState = remember { SnackbarHostState() }

    // Afficher le message de statut dans un Snackbar
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
                    // Bouton Enroll (Populate)
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
            // FloatingActionButton visible uniquement en mode liste
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
            // Afficher soit l'écran d'édition, soit la liste
            if (isEditing) {
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
                ScreenContactList(contacts) { selectedContact ->
                    contactsViewModel.editContact(selectedContact)
                }
            }

            // Indicateur de chargement par-dessus
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