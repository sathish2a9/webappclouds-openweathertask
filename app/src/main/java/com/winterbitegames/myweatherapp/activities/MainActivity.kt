package com.winterbitegames.myweatherapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.winterbitegames.myweatherapp.R
import com.winterbitegames.myweatherapp.qrcodegeneratorreader.ScanQrCodeActivity
import com.winterbitegames.myweatherapp.common.ClickListener
import com.winterbitegames.myweatherapp.model.data_class.WeatherData
import com.winterbitegames.myweatherapp.viewmodel.WeatherInfoViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ClickListener {
    private lateinit var viewModel: WeatherInfoViewModel
    private var cityList = ArrayList<WeatherData>()
    private lateinit var customAdapter: CustomAdapter
    private val sharedPrefFile = "mypreference"
    var sharedPreferences: SharedPreferences? = null
    val REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = this.getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
        viewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel::class.java)

        setLiveDataListeners()

        cityList.add(WeatherData())

        customAdapter = CustomAdapter(cityList, this) // need to pass the list
        rv_list.layoutManager = GridLayoutManager(this, 3)
        rv_list.adapter = customAdapter

        getList()
    }

    override fun onClick(data: Any, position: Int) {
        val intent = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun setLiveDataListeners() {

        viewModel.progressBarLiveData.observe(this, Observer { isShowLoader ->
            if (isShowLoader)
                progressBar.visibility = View.VISIBLE
            else
                progressBar.visibility = View.GONE
        })

        viewModel.weatherInfoLiveData.observe(this, Observer { weatherData ->
            setWeatherInfo(weatherData)
        })

        viewModel.weatherInfoList.observe(this, Observer { weatherList ->
            cityList = weatherList
            customAdapter.updateList(weatherList)
        })

        viewModel.weatherInfoFailureLiveData.observe(this, Observer { errorMessage ->
            Toast.makeText(this, "Could not get weather. Please scan valid city name and try again.", Toast.LENGTH_SHORT)
                .show()
        })
    }

    private fun setWeatherInfo(weatherData: WeatherData) {
        // Pass this data to adapter
        cityList.add(weatherData);
        //cityList.add(WeatherData())
        customAdapter.updateList(cityList)
    }

    private fun getList() {

        if (sharedPreferences != null) {
            sharedPreferences = this.getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
            val listString = sharedPreferences!!.getString("cityList", "")
            if (!listString.equals("") || listString?.length != 0) {
                val cityNames: List<String> = listString?.split(",")!!.map { it.trim() }
                viewModel.getWeatherInfo(cityNames)
            }
        }
    }

    private fun updateListToDb(list: ArrayList<WeatherData>) {
        if (sharedPreferences != null) {
            sharedPreferences = this.getSharedPreferences(sharedPrefFile, MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
            var nameList = mutableListOf<String>()
            for (i in list.indices){
                nameList.add(list[i].city)
            }
            editor.putString("cityList", nameList.joinToString())
            editor.apply()
        }
    }

    override fun onStop() {
        super.onStop()
        updateListToDb(cityList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val returnString:String? = data.getStringExtra("scannedName")
                    if (!returnString.equals("")) {
                        viewModel.getWeatherInfo(returnString.toString())
                    }
                }
            }
        }
    }

}