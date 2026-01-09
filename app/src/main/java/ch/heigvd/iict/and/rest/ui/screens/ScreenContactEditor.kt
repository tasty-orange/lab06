package ch.heigvd.iict.and.rest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType

@Composable
fun ScreenContactEditor(
    contact: Contact?,
    onSave: (Contact) -> Unit,
    onDelete: (Contact) -> Unit,
    onCancel: () -> Unit
) {
    // États pour les champs du formulaire
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var firstname by remember { mutableStateOf(contact?.firstname ?: "") }
    var email by remember { mutableStateOf(contact?.email ?: "") }
    var address by remember { mutableStateOf(contact?.address ?: "") }
    var zip by remember { mutableStateOf(contact?.zip ?: "") }
    var city by remember { mutableStateOf(contact?.city ?: "") }
    var phoneNumber by remember { mutableStateOf(contact?.phoneNumber ?: "") }
    var phoneType by remember { mutableStateOf(contact?.type ?: PhoneType.MOBILE) }

    val isNewContact = contact == null || contact.id == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Titre
        Text(
            text = if (isNewContact)
                stringResource(R.string.screen_detail_title_new)
            else
                stringResource(R.string.screen_detail_title_edit),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Champ Name (obligatoire)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.screen_detail_name_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = name.isBlank()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Firstname
        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text(stringResource(R.string.screen_detail_firstname_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.screen_detail_email_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Birthday (non éditable)
        OutlinedTextField(
            value = contact?.birthday?.let {
                String.format("%02d.%02d.%04d",
                    it.get(java.util.Calendar.DAY_OF_MONTH),
                    it.get(java.util.Calendar.MONTH) + 1,
                    it.get(java.util.Calendar.YEAR)
                )
            } ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.screen_detail_birthday_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.screen_detail_address_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Zip
        OutlinedTextField(
            value = zip,
            onValueChange = { zip = it },
            label = { Text(stringResource(R.string.screen_detail_zip_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ City
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text(stringResource(R.string.screen_detail_city_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sélection du type de téléphone
        Text(
            text = stringResource(R.string.screen_detail_phonetype_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
            PhoneType.entries.forEach { type ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (phoneType == type),
                            onClick = { phoneType = type }
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (phoneType == type),
                        onClick = { phoneType = type }
                    )
                    Text(
                        text = when(type) {
                            PhoneType.HOME -> stringResource(R.string.phonetype_home)
                            PhoneType.OFFICE -> stringResource(R.string.phonetype_office)
                            PhoneType.MOBILE -> stringResource(R.string.phonetype_mobile)
                            PhoneType.FAX -> stringResource(R.string.phonetype_fax)
                        },
                        modifier = Modifier.padding(start = 8.dp, top = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Champ Phone Number
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(R.string.screen_detail_phonenumber_subtitle)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bouton Cancel
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.screen_detail_btn_cancel))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Bouton Delete (seulement pour édition)
            if (!isNewContact) {
                OutlinedButton(
                    onClick = { contact?.let { onDelete(it) } },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.screen_detail_btn_delete))
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // Bouton Save/Create
            Button(
                onClick = {
                    val updatedContact = Contact(
                        id = contact?.id,
                        remoteId = contact?.remoteId,
                        name = name,
                        firstname = firstname.takeIf { it.isNotBlank() },
                        birthday = contact?.birthday,
                        email = email.takeIf { it.isNotBlank() },
                        address = address.takeIf { it.isNotBlank() },
                        zip = zip.takeIf { it.isNotBlank() },
                        city = city.takeIf { it.isNotBlank() },
                        type = phoneType,
                        phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                        syncState = contact?.syncState ?: ch.heigvd.iict.and.rest.models.SyncState.TO_SYNC
                    )
                    onSave(updatedContact)
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank()
            ) {
                Text(
                    if (isNewContact)
                        stringResource(R.string.screen_detail_btn_create)
                    else
                        stringResource(R.string.screen_detail_btn_save)
                )
            }
        }
    }
}