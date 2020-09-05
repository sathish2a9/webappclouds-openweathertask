package com.winterbitegames.myweatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.winterbitegames.myweatherapp.common.RequestCompleteListener
import com.winterbitegames.myweatherapp.model.data_class.WeatherData
import com.winterbitegames.myweatherapp.model.data_class.WeatherInfoResponse
import com.winterbitegames.myweatherapp.network.ApiInterface
import com.winterbitegames.myweatherapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherInfoViewModel : ViewModel() {

    val weatherInfoLiveData = MutableLiveData<WeatherData>()
    val weatherInfoList = MutableLiveData<ArrayList<WeatherData>>()
    val weatherInfoFailureLiveData = MutableLiveData<String>()
    val progressBarLiveData = MutableLiveData<Boolean>()

    fun getWeatherInfo(cityList: List<String>) {

        progressBarLiveData.postValue(true) // PUSH data to LiveData object to show progress bar

        var cityData : ArrayList<WeatherData> = ArrayList()

        for (city in cityList) {
            getWeatherInfo(city, object :
                RequestCompleteListener<WeatherInfoResponse> {
                override fun onRequestSuccess(data: WeatherInfoResponse) {

                    // business logic and data manipulation tasks should be done here
                    val weatherData = WeatherData(
                        temperature = data.main.temp.kelvinToCelsius().toString() + " \u2103",
                        city = data.name
                    )
                    cityData.add(weatherData)

                    if(cityData.size == cityList.size) {
                        progressBarLiveData.postValue(false) // PUSH data to LiveData object to hide progress bar
                        weatherInfoList.postValue(cityData) // PUSH data to LiveData object
                    }
                }

                override fun onRequestFailed(errorMessage: String) {
                    cityData.add(0,WeatherData())
                    progressBarLiveData.postValue(false) // hide progress bar
                    //weatherInfoFailureLiveData.postValue(errorMessage) // PUSH error message to LiveData object
                }
            })
        }
    }

    fun getWeatherInfo(city: String) {

        progressBarLiveData.postValue(true) // PUSH data to LiveData object to show progress bar

        getWeatherInfo(city, object :
            RequestCompleteListener<WeatherInfoResponse> {
            override fun onRequestSuccess(data: WeatherInfoResponse) {

                // business logic and data manipulation tasks should be done here
                val weatherData = WeatherData(
                    temperature = data.main.temp.kelvinToCelsius().toString() + " \u2103",
                    city = data.name
                )
                progressBarLiveData.postValue(false) // PUSH data to LiveData object to hide progress bar
                Thread {
                    weatherInfoLiveData.postValue(weatherData) // PUSH data to LiveData object
                }.start()

            }

            override fun onRequestFailed(errorMessage: String) {
                progressBarLiveData.postValue(false) // hide progress bar
                weatherInfoFailureLiveData.postValue(errorMessage) // PUSH error message to LiveData object
            }
        })
    }

    fun Double.kelvinToCelsius(): Int {

        return (this - 273.15).toInt()
    }

    fun getWeatherInfo(city: String, callback: RequestCompleteListener<WeatherInfoResponse>) {
        val apiInterface: ApiInterface = RetrofitClient.client.create(ApiInterface::class.java)
        val call: Call<WeatherInfoResponse> = apiInterface.callApiForWeatherInfo(city)

        call.enqueue(object : Callback<WeatherInfoResponse> {

            // if retrofit network call success, this method will be triggered
            override fun onResponse(
                call: Call<WeatherInfoResponse>,
                response: Response<WeatherInfoResponse>
            ) {
                if (response.body() != null)
                    callback.onRequestSuccess(response.body()!!) //let presenter know the weather information data
                else
                    callback.onRequestFailed(response.message()) //let presenter know about failure
            }

            // this method will be triggered if network call failed
            override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {
                callback.onRequestFailed(t.localizedMessage!!) //let presenter know about failure
            }
        })
    }
}