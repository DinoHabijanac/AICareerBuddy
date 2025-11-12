package hr.foi.air.giveaway.network

import android.R.attr.level
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // prikazuje sve (URL, headers, JSON)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()


    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost:5096/api/")   // koristi tunel
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }



    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
