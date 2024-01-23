package com.strawberryCodeCake.capturevoca.data.dictionary

import com.strawberryCodeCake.capturevoca.data.dictionary.model.DictionaryEntry
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL =
    "https://api.dictionaryapi.dev/api/v2/entries/en/"



private val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val client : OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(interceptor)
}.build()


/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

/**
 * Retrofit service object for creating api calls
 */
interface DictionaryApiService {
    @GET("{word}")
    suspend fun getDictionaryEntryList(@Path(value = "word") word: String): List<DictionaryEntry>
}


/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object DictionaryApi {
    val retrofitService: DictionaryApiService by lazy {
        retrofit.create(DictionaryApiService::class.java)
    }
}