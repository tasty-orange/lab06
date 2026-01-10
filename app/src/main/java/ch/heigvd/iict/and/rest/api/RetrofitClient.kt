package ch.heigvd.iict.and.rest.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton pour la configuration et l'instanciation de Retrofit
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
object RetrofitClient {

    private const val BASE_URL = "https://daa.iict.ch/"

    /**
     * Instance Gson configurée pour parser les dates au format ISO 8601.
     */
    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        .create()

    /**
     * Intercepteur de logging HTTP pour afficher les requêtes/réponses dans Logcat.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Client OkHttp configuré avec timeouts et logging.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    /**
     * Instance Retrofit configurée avec :
     * - Base URL de l'API
     * - Client HTTP personnalisé (avec logging et timeouts)
     * - StringConverterFactory : pour /enroll qui retourne du texte brut
     * - GsonConverterFactory : pour tous les endpoints JSON
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(StringConverterFactory())  // AVANT Gson pour /enroll
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    /**
     * Instance du service API créée par Retrofit.
     */
    val apiService: ContactsApiService = retrofit.create(ContactsApiService::class.java)
}