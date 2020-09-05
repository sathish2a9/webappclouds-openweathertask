package com.winterbitegames.myweatherapp.model.data_class

import com.google.gson.annotations.SerializedName

data class WeatherInfoResponse(
    @SerializedName("main")
        val main: Main = Main(),
    @SerializedName("name")
    val name: String = ""
)