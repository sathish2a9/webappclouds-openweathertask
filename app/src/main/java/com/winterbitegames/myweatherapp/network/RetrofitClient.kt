package com.winterbitegames.myweatherapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null


    val client: Retrofit
        get() {
            if (retrofit == null) {
                synchronized(Retrofit::class.java) {
                    if (retrofit == null) {

                        val client = OkHttpClient.Builder().build()

                        retrofit = Retrofit.Builder()
                                .baseUrl("https://api.openweathermap.org/data/2.5/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build()
                    }
                }

            }
            return retrofit!!
        }
}