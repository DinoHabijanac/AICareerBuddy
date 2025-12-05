package com.example.myapplication.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.util.concurrent.TimeUnit

object NetworkModule {
    // promjeniti postavke - ovo je nesigurno
    // promjeniti na false
    private const val DEBUG = true
    private const val BASE_URL = "https://10.0.2.2:7058/"
    private val simpleLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        try {
            Log.d("NetworkModule", "--> ${request.method} ${request.url}")
            val response = chain.proceed(request)
            Log.d("NetworkModule", "<-- ${response.code} ${response.request.url}")
            response
        } catch (e: Exception) {
            Log.d("NetworkModule", "<-- HTTP FAILED: ${e.message}")
            throw e
        }
    }

    private val localDateTimeDeserializer = JsonDeserializer<LocalDateTime> { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
        val str = try { json.asString } catch (_: Exception) { "" }
        if (str.isBlank()) return@JsonDeserializer LocalDateTime.now()
        try {
            val instant = Instant.parse(str)
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        } catch (_: Exception) {
            try {
                LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME)
            } catch (_: Exception) {
                LocalDateTime.now()
            }
        }
    }

    private fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
            .create()
    }

    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .addInterceptor(simpleLoggingInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        //promjeniti da ne vjeruje svim certovima
        if (DEBUG) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                })

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (t: Throwable) {
                Log.w("NetworkModule", "Failed to set up dev-trust-all SSL: ${t.message}")
            }
        }

        return builder.build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
    }
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
