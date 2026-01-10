package ch.heigvd.iict.and.rest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.heigvd.iict.and.rest.ui.screens.AppContact
import ch.heigvd.iict.and.rest.ui.theme.MyComposeApplicationTheme

/**
 * Activité principale de l'application de gestion de contacts
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
class MainActivity : ComponentActivity() {

    /**
     * Initialise l'activité et configure l'interface Compose.
     *
     * @param savedInstanceState Bundle? - État sauvegardé de l'activité (peut être null)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeApplicationTheme {
                AppContact()
            }
        }
    }
}