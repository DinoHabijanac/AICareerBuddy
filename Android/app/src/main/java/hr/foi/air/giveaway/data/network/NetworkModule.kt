package hr.foi.air.giveaway.data.network
import ApiService

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.util.concurrent.TimeUnit

object NetworkModule {
    // Postavke za razvoj (DEBUG=true dopušta nepouzdane SSL certifikate za lokalni server)
    private const val DEBUG = true
    private const val BASE_URL = "https://10.0.2.2:7058/"  // bazni URL backend-a (primjer)

    // Interceptor za jednostavno logiranje osnovnih informacija o zahtjevu/odgovoru
    private val simpleLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        try {
            Log.d("NetworkModule", "--> ${'$'}{request.method} ${'$'}{request.url}")
            val response = chain.proceed(request)
            Log.d("NetworkModule", "<-- ${'$'}{response.code} ${'$'}{response.request.url}")
            response
        } catch (e: Exception) {
            Log.d("NetworkModule", "<-- HTTP FAILED: ${'$'}{e.message}")
            throw e
        }
    }

    // Konfiguracija OkHttp klijenta
    private fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .addInterceptor(simpleLoggingInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        // Ako je DEBUG, postaviti "trust-all" SSL context (samo za razvojne svrhe - nesigurno)
        if (DEBUG) {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (t: Throwable) {
                Log.w("NetworkModule", "Greška pri postavljanju SSL trust-all: ${'$'}{t.message}")
            }
        }

        return builder.build()
    }

    // Lazy inicijalizacija Retrofit instance i API servisa
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
