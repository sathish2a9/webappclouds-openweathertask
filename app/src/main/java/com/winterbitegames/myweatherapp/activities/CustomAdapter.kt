package com.winterbitegames.myweatherapp.activities

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.winterbitegames.myweatherapp.R
import com.winterbitegames.myweatherapp.common.ClickListener
import com.winterbitegames.myweatherapp.model.data_class.WeatherData

class CustomAdapter(var cityList: ArrayList<WeatherData>, var clickListener: ClickListener) : RecyclerView.Adapter<CustomAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View = layoutInflater.inflate(R.layout.item_layout, parent, false)
        return ContactHolder(view)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        val city: WeatherData = cityList[holder.adapterPosition]

        if (TextUtils.isEmpty(city.city)){
            holder.add_image.visibility =View.VISIBLE
            holder.cityName.visibility = View.GONE
            holder.cityTemp.visibility = View.GONE
        }else {
            holder.add_image.visibility =View.GONE
            holder.cityName.visibility = View.VISIBLE
            holder.cityTemp.visibility = View.VISIBLE

            // Set the data to the views here
            holder.cityName.text = city.city
            holder.cityTemp.text = city.temperature
        }

        holder.add_image.setOnClickListener { clickListener.onClick(city, holder.adapterPosition) }

    }

    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.tv_cityName)
        val cityTemp: TextView = itemView.findViewById(R.id.tv_temp)
        val add_image: ImageView = itemView.findViewById(R.id.iv_add)
    }

    fun updateList(cityList: ArrayList<WeatherData>){
        this.cityList = cityList
        notifyDataSetChanged()
    }
}