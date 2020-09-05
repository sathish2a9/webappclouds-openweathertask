package com.winterbitegames.myweatherapp.network

import com.winterbitegames.myweatherapp.model.data_class.WeatherInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather?appid=da7b24afc414d0fa1b4f5afc4d7befa0&")
    fun callApiForWeatherInfo(@Query("q") city: String): Call<WeatherInfoResponse>
}