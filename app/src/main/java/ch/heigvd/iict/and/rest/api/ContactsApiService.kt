package ch.heigvd.iict.and.rest.api

import ch.heigvd.iict.and.rest.models.ContactDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit définissant l'API REST pour la gestion des contacts
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
interface ContactsApiService {

    /**
     * Enrollment - Créer un nouvel utilisateur et obtenir un UUID.
     *
     * @return Response<String> - UUID au format texte brut
     */
    @GET("enroll")
    suspend fun enroll(): Response<String>

    /**
     * Récupérer tous les contacts de l'utilisateur.
     *
     * @param uuid String - UUID utilisateur (header X-UUID)
     * @return Response<List<ContactDTO>> - Liste des contacts au format JSON
     */
    @GET("contacts")
    suspend fun getAllContacts(
        @Header("X-UUID") uuid: String
    ): Response<List<ContactDTO>>

    /**
     * Obtenir un contact spécifique par son ID serveur.
     *
     * @param uuid String - UUID utilisateur (header X-UUID)
     * @param id Long - ID du contact sur le serveur
     * @return Response<ContactDTO> - Contact demandé ou erreur 404 si non trouvé
     */
    @GET("contacts/{id}")
    suspend fun getContactById(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long
    ): Response<ContactDTO>

    /**
     * Créer un nouveau contact sur le serveur.
     *
     * @param uuid String - UUID utilisateur (header X-UUID)
     * @param contact ContactDTO - Contact à créer (avec id = null)
     * @return Response<ContactDTO> - Contact créé avec l'ID généré (201 CREATED)
     */
    @POST("contacts")
    suspend fun createContact(
        @Header("X-UUID") uuid: String,
        @Body contact: ContactDTO
    ): Response<ContactDTO>

    /**
     * Modifier un contact existant sur le serveur.
     *
     * @param uuid String - UUID utilisateur (header X-UUID)
     * @param id Long - ID du contact à modifier
     * @param contact ContactDTO - Nouvelles données du contact
     * @return Response<ContactDTO> - Contact modifié (200 OK)
     */
    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long,
        @Body contact: ContactDTO
    ): Response<ContactDTO>

    /**
     * Supprimer un contact du serveur.
     *
     * @param uuid String - UUID utilisateur (header X-UUID)
     * @param id Long - ID du contact à supprimer
     * @return Response<Unit> - Réponse vide (200 OK) ou erreur
     */
    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long
    ): Response<Unit>
}