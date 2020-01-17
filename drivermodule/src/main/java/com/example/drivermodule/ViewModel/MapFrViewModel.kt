package com.example.drivermodule.ViewModel

import android.databinding.ObservableField
import android.view.View
import com.amap.api.location.AMapLocation
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.elder.zcommonmodule.Entity.UserInfo
import com.example.drivermodule.R
import com.zk.library.Base.BaseViewModel
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearchQuery
import com.example.drivermodule.Ui.MapFragment
import org.cs.tec.library.Base.Utils.context
import java.util.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


class MapFrViewModel : BaseViewModel(), WeatherSearch.OnWeatherSearchListener {
    fun invoke(location: AMapLocation) {
        var mquery = WeatherSearchQuery(location.city, WeatherSearchQuery.WEATHER_TYPE_LIVE)
        var mweathersearch = WeatherSearch(context)
        loacation.set(location.city)
        mweathersearch.setOnWeatherSearchListener(this)
        mweathersearch.query = mquery
        mweathersearch.searchWeatherAsyn()
    }

    var week = ObservableField<String>()
    var tem = ObservableField<String>()
    var loacation = ObservableField<String>()
    override fun onWeatherLiveSearched(weatherLiveResult: LocalWeatherLiveResult?, rCode: Int) {
        if (rCode == 1000) {
            if (weatherLiveResult?.liveResult != null) {
                var weatherlive = weatherLiveResult.liveResult
                tem.set(weatherlive.temperature + "℃")
                var w = ""
                var mWay = Calendar.getInstance()[Calendar.DAY_OF_WEEK].toString()
                if ("1" == mWay) {
                    mWay = "天";
                } else if ("2" == mWay) {
                    mWay = "一";
                } else if ("3" == mWay) {
                    mWay = "二";
                } else if ("4" == mWay) {
                    mWay = "三";
                } else if ("5" == mWay) {
                    mWay = "四";
                } else if ("6" == mWay) {
                    mWay = "五";
                } else if ("7" == mWay) {
                    mWay = "六";
                }
                week.set("星期$mWay")
//                reporttime1.setText(weatherlive.getReportTime()+"发布");
//                weather.setText(weatherlive.getWeather());
//                Temperature.setText(weatherlive.getTemperature()+"°");
//                wind.setText(weatherlive.getWindDirection()+"风     "+weatherlive.getWindPower()+"级");
//                humidity.setText("湿度         "+weatherlive.getHumidity()+"%");
            } else {
//                ToastUtil.show(WeatherSearchActivity.this, R.string.no_result);
            }
        } else {
//            ToastUtil.showerror(WeatherSearchActivity.this, rCode);
        }
    }
    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {
    }
    var flag = false
    var info: UserInfo? = null
    fun onClick(view: View) {
        when (view.id) {
            R.id.map_click -> {

            }
        }
    }
    lateinit var mapFragment: MapFragment
    fun inject(mapFragment: MapFragment) {
        this.mapFragment = mapFragment
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            invoke(it)
        })
    }
}