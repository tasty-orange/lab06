package ch.heigvd.iict.and.rest.api

import ch.heigvd.iict.and.rest.models.ContactDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface Retrofit pour l'API REST
 */
interface ContactsApiService {

    /**
     * Enrollment - Créer un nouvel utilisateur et obtenir un UUID
     * GET /enroll
     */
    @GET("enroll")
    suspend fun enroll(): Response<String>

    /**
     * Récupérer tous les contacts
     * GET /contacts
     */
    @GET("contacts")
    suspend fun getAllContacts(
        @Header("X-UUID") uuid: String
    ): Response<List<ContactDTO>>

    /**
     * Obtenir un contact spécifique
     * GET /contacts/{id}
     */
    @GET("contacts/{id}")
    suspend fun getContactById(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long
    ): Response<ContactDTO>

    /**
     * Créer un nouveau contact
     * POST /contacts
     */
    @POST("contacts")
    suspend fun createContact(
        @Header("X-UUID") uuid: String,
        @Body contact: ContactDTO
    ): Response<ContactDTO>

    /**
     * Modifier un contact existant
     * PUT /contacts/{id}
     */
    @PUT("contacts/{id}")
    suspend fun updateContact(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long,
        @Body contact: ContactDTO
    ): Response<ContactDTO>

    /**
     * Supprimer un contact
     * DELETE /contacts/{id}
     */
    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Header("X-UUID") uuid: String,
        @Path("id") id: Long
    ): Response<Unit>
}