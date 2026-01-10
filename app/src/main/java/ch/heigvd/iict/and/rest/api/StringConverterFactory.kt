package ch.heigvd.iict.and.rest.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Convertisseur Retrofit personnalisé pour les réponses en texte brut
 *
 * @author Piemontesi Gwendal
 * @author Trueb Guillaume
 * @author Kunzli Christophe
 */
class StringConverterFactory : Converter.Factory() {

    /**
     * Crée un converter pour les réponses HTTP si le type attendu est String.
     *
     * @param type Type - Type de retour attendu par la méthode Retrofit
     * @param annotations Array<out Annotation> - Annotations de la méthode
     * @param retrofit Retrofit - Instance Retrofit (non utilisée ici)
     * @return Converter<ResponseBody, *>? - Converter si type = String, null sinon
     */
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == String::class.java) {
            Converter<ResponseBody, String> { value -> value.string() }
        } else {
            null
        }
    }
}