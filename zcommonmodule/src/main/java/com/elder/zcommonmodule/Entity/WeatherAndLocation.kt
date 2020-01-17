package com.elder.zcommonmodule.Entity

import com.amap.api.location.AMapLocation
import com.amap.api.services.weather.LocalWeatherLiveResult
import java.io.Serializable


class WeatherAndLocation : Serializable {

    var weatherResult: LocalWeatherLiveResult? = null

    var location: String? = null


}