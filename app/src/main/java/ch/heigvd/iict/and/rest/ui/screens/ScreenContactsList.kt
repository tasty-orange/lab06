package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.SyncState
import ch.heigvd.iict.and.rest.ui.theme.MyComposeApplicationTheme

/**
 * Écran Compose affichant la liste de tous les contacts
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 *
 * @param contacts List<Contact> - Liste des contacts à afficher
 * @param onContactSelected (Contact) -> Unit - Callback appelé lors du clic sur un contact
 */
@Composable
fun ScreenContactList(contacts: List<Contact>, onContactSelected: (Contact) -> Unit) {
    Column {
        // Titre de l'écran
        Text(text = stringResource(R.string.screen_list_title), fontSize = 24.sp)

        // Affichage conditionnel selon si la liste est vide ou non
        if (contacts.isEmpty()) {
            // Message centré si aucun contact
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.screen_list_empty),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Liste scrollable des contacts
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts) { item ->
                    ContactItemView(item) { clickedContact ->
                        onContactSelected(clickedContact)
                    }
                }
            }
        }
    }
}

/**
 * Composable représentant un item de contact dans la liste
 *
 * @param contact Contact - Contact à afficher
 * @param onClick (Contact) -> Unit - Callback appelé lors du clic
 */
@Composable
fun ContactItemView(contact: Contact, onClick: (Contact) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(2.dp)
            .clickable {
                onClick(contact)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône de contact générique
        Image(
            painter = painterResource(id = R.drawable.contact),
            contentDescription = stringResource(id = R.string.screen_list_contacticon_ctndesc)
        )

        // Colonne centrale avec nom et téléphone
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 2.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "${contact.firstname ?: ""} ${contact.name}".trim())
            Text(text = "${contact.phoneNumber ?: ""}")
        }

        // Icône du type de téléphone
        Image(
            painter = painterResource(
                id = when (contact.type) {
                    PhoneType.MOBILE -> R.drawable.cellphone
                    PhoneType.FAX -> R.drawable.fax
                    PhoneType.HOME -> R.drawable.phone
                    PhoneType.OFFICE -> R.drawable.office
                    null -> R.drawable.office
                }
            ),
            contentDescription = stringResource(id = R.string.screen_list_contacttype_ctndesc)
        )
    }
}

/**
 * Données de démonstration pour les previews
 */
val contactsDemo = listOf(
    Contact(null, null, "Dupont", "Roger", null, null, "", "1400", "Yverdon", PhoneType.HOME, "+41 21 944 23 55", SyncState.SYNCED),
    Contact(null, null, "Dupond", "Tatiana", null, null, "", "1000", "Lausanne", PhoneType.OFFICE, "+41 24 763 34 12", SyncState.SYNCED),
    Contact(null, null, "Toto", "Tata", null, null, "", "1400", "Yverdon", PhoneType.MOBILE, "+41 21 456 25 36", SyncState.SYNCED)
)

/**
 * Preview de la liste complète de contacts
 */
@Preview(showBackground = true)
@Composable
fun ContactListPreview() {
    MyComposeApplicationTheme {
        ScreenContactList(contactsDemo) {}
    }
}

/**
 * Preview d'un item de contact individuel
 */
@Preview(showBackground = true)
@Composable
fun ContactItemPreview() {
    MyComposeApplicationTheme {
        ContactItemView(contactsDemo[0]) {}
    }
}