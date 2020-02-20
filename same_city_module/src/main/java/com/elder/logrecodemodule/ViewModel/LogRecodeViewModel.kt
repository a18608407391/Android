package com.elder.logrecodemodule.ViewModel

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.services.district.DistrictResult
import com.amap.api.services.district.DistrictSearch
import com.amap.api.services.district.DistrictSearchQuery
import com.amap.api.services.geocoder.GeocodeQuery
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.weather.LocalWeatherForecastResult
import com.amap.api.services.weather.LocalWeatherLiveResult
import com.amap.api.services.weather.WeatherSearch
import com.amap.api.services.weather.WeatherSearchQuery
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.Inteface.RankingClickListener
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.CURRENT_WEATHER
import com.elder.zcommonmodule.Entity.*
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.OningData
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Widget.CityPicker.CityPicker
import com.elder.zcommonmodule.Widget.CityPicker.adapter.OnPickListener
import com.elder.zcommonmodule.Widget.CityPicker.model.City
import com.elder.zcommonmodule.Widget.CityPicker.model.LocatedCity
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinder
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinderDialogFragment
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_logrecode.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.*


class LogRecodeViewModel : BaseViewModel(), WeatherSearch.OnWeatherSearchListener, SwipeRefreshLayout.OnRefreshListener, DistrictSearch.OnDistrictSearchListener, RankingClickListener, GeocodeSearch.OnGeocodeSearchListener, TelephoneBinderDialogFragment.DismissListener {
    override fun onDismiss() {
        //弹出窗消失了
        mapFragment.loadDatas(location!!)
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {

        Log.e("result", "这里执行了onRegeocodeSearched")
    }


    var replaceCount = 0
    var replaceName = ""

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        if (p1 == 1000) {
            if (!p0?.geocodeAddressList.isNullOrEmpty()) {
                var city = ""
                if (p0?.geocodeAddressList!![0]!!.district.isNullOrEmpty()) {
                    city = p0?.geocodeAddressList!![0]!!.city
                } else {
                    city = p0?.geocodeAddressList!![0]!!.district
                }
                location = Location(p0?.geocodeAddressList!![0]!!.latLonPoint!!.latitude, p0?.geocodeAddressList!![0]!!.latLonPoint!!.longitude, System.currentTimeMillis().toString(), 0F, 0.0, 0F, p0?.geocodeAddressList!![0]!!.city, p0?.geocodeAddressList!![0]!!.formatAddress)
                init(city, p0?.geocodeAddressList[0].province, p0?.geocodeAddressList[0].adcode)
                replaceCount = 0
                replaceName = ""
            } else {
                var city: City? = null
                if (replaceCount == 0) {
                    replaceName = p0?.geocodeQuery!!.locationName
                    city = City(p0?.geocodeQuery!!.locationName + "市", "", p0?.geocodeQuery!!.city, "")
                } else if (replaceCount == 1) {
                    city = City(replaceName + "县", "", p0?.geocodeQuery!!.city, "")
                } else if (replaceCount == 2) {
                    city = City(replaceName + "区", "", p0?.geocodeQuery!!.city, "")
                } else {
                    replaceCount = 0
                    replaceName = ""
                    return
                }

                search(city!!)
                replaceCount++
            }
        } else {
            Log.e("result", "异常" + p1)
            Toast.makeText(mapFragment.activity, "地理异常" + p1, Toast.LENGTH_SHORT).show()
        }
    }

    override fun RankingItemClick(position: Int, item: CountryMemberEntity) {
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, item.id)
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation()
    }

    override fun onDistrictSearched(p0: DistrictResult?) {
//        hotCity.clear()
//        p0!!.district.forEach {
//            Log.e("result", "名称" + it.name)
//            it.subDistrict.forEach {
//                hotCity.add(HotCity(it.name, it.name, it.citycode))
//            }
//        }
//        city?.setHotCities(hotCity)
    }

    override fun onRefresh() {
        mapFragment.loadDatas(location!!)
    }

    var district = ObservableField<String>("出发地：爱摩老司机重机生活馆")

    var navigationStart = ObservableField<String>("未来漫城")

    var navigationEnd = ObservableField<String>("火车南站")

    var weather = ObservableField<String>()
    var driverType = ObservableField<Int>(0)
    var allTotal = ObservableField<String>("0")
    var allTime = ObservableField<String>("0")
    var monthTotal = ObservableField<String>("0")
    var monthTime = ObservableField<String>("0")

    var VisField = ObservableField<Boolean>(false)
    override fun onWeatherLiveSearched(weatherLiveResult: LocalWeatherLiveResult?, rCode: Int) {
        if (rCode == 1000) {
            if (weatherLiveResult?.liveResult != null) {
                var weatherlive = weatherLiveResult.liveResult
                tem.set(weatherlive.temperature + "℃")
                var w = ""
                var mWay = Calendar.getInstance()[Calendar.DAY_OF_WEEK].toString()
                weather.set(weatherlive.weather)
                var action = WeatherAndLocation()
                if (location!!.aoiName.isNullOrEmpty()) {
                    action.location = location!!.poiName
                } else {
                    action.location = location!!.aoiName
                }
                action.weatherResult = weatherLiveResult
                PreferenceUtils.putString(context, CURRENT_WEATHER, Gson().toJson(action))
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
            } else {

            }
        } else {
        }
    }


    var pagerAdapter = BindingRecyclerViewAdapter<HotData>()

    var itemBinding = ItemBinding.of<HotData>(BR.horiDatas, R.layout.log_roadbook_layout).bindExtra(BR.log_road_model, this@LogRecodeViewModel)

    var items = ObservableArrayList<HotData>()


    fun itemClick(data: HotData) {
        if (data != null) {
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data).navigation(mapFragment.activity, REQUEST_LOAD_ROADBOOK)
        }
    }

    var ScrollChangeCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
//            if (lastMarker == null) {
//                return
//            }
//
//            if (t == 0) {
//                var layout = roadBookFragment.bottom_pager.layoutManager as LinearLayoutManager
//                if (layout.findLastCompletelyVisibleItemPosition() >= 0) {
//                    markerChange(makerList[layout.findLastCompletelyVisibleItemPosition()])
//                }
//                activity.mAmap.moveCamera(CameraUpdateFactory.newLatLng(lastMarker!!.position))
//            }
        }
    })


    var Teamicon = ObservableField<Boolean>(false)

    var teamStatus = ObservableField<String>(getString(R.string.not_team))

    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {

    }

    lateinit var mapFragment: LogRecodeFragment
    lateinit var search: DistrictSearch
    lateinit var query: DistrictSearchQuery
    var location: Location? = null
    fun inject(mapFragment: LogRecodeFragment) {
        search = DistrictSearch(mapFragment.activity)
        query = DistrictSearchQuery()
        search.setOnDistrictSearchListener(this)
        this.mapFragment = mapFragment
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            invoke(it)
        })
    }


    var locLocition: Location? = null
    var curLocation: AMapLocation? = null
    fun invoke(loc: AMapLocation) {
        curLocation = loc
        if (this.location == null) {
            if (loc.city.isEmpty()) {
                return
            }
            location = Location(loc!!.latitude, loc!!.longitude, System.currentTimeMillis().toString(), loc!!.speed, loc!!.altitude, loc!!.bearing, loc!!.city, loc!!.aoiName)
//            mapFragment.loadDatas(location!!)
            locLocition = location
            init(loc.city, loc.province, loc.cityCode)
        }
    }


    fun init(loc: String, provice: String, cityCode: String) {
        query.keywords = loc;//传入关键字
        query.isShowBoundary = false;//是否返回边界值
        search.query = query
        search.searchDistrictAsyn()
        loacation.set(loc)
        var mquery = WeatherSearchQuery(loc, WeatherSearchQuery.WEATHER_TYPE_LIVE)
        var mweathersearch = WeatherSearch(context)
        mweathersearch.setOnWeatherSearchListener(this)
        mweathersearch.query = mquery
        mweathersearch.searchWeatherAsyn()
        if (city == null) {
            initCity()
        }
        mapFragment.loadDatas(location!!)
        city?.setLocatedCity(LocatedCity(loc, provice, cityCode))
    }

    var week = ObservableField<String>()
    var tem = ObservableField<String>()
    var loacation = ObservableField<String>()
    var city: CityPicker? = null


    fun search(data: City) {
        var geocodeSearch = GeocodeSearch(mapFragment.context)
        geocodeSearch.setOnGeocodeSearchListener(this@LogRecodeViewModel)
        var geocodeQuery = GeocodeQuery(data!!.name, data.pinyin)
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery)
    }


    fun initCity() {
        city = CityPicker.from(mapFragment).setLocatedCity(null).setOnPickListener(object : OnPickListener {
            override fun onPick(position: Int, data: City?) {
                search(data!!)
            }

            override fun onLocate() {
//                CityPicker.from(mapFragment).locateComplete(LocatedCity(location!!.city, location!!.province, location!!.cityCode), LocateState.SUCCESS)
//                                .locateComplete(new LocatedCity ("深圳", "广东", "101280601"), LocateState.SUCCESS);
            }

            override fun onCancel() {
            }

        })
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.log_et -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SEARCH_MEMBER).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation()
            }
            R.id.log_enter -> {

//                mapFragment.showSoftInputFromWindow(mapFragment.log_et1)
            }
            R.id.gotoroadhome -> {
                if (location == null) {
                    Toast.makeText(context, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
                    return
                }
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE, 0).withSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, location).navigation(mapFragment.activity, REQUEST_LOAD_ROADBOOK)
//                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).navigation()
            }
            R.id.navigation_log_list -> {
                RxBus.default?.post("ToUser")
            }
            R.id.navigation_user -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).navigation()
            }
            R.id.go_web -> {
                var pos = ServiceEven()
                pos.type = "HomeDriver"
                RxBus.default?.post(pos)
//                context!!.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
                RxBus.default?.post("fastTeam")
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "fastTeam").navigation()
//                ARouter.getInstance().build(RouterUtils.LoginModuleKey.WEB_VIEW).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 1).navigation()
            }
            R.id.active -> {
                ARouter.getInstance().build(RouterUtils.LoginModuleKey.WEB_VIEW).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 2).navigation()
            }
            R.id.hot_active -> {

                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 3).navigation()

            }
            R.id.toRank -> {
                if (mapFragment.local_rg.checkedRadioButtonId == R.id.local_rb) {
                    ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SAME_CITY_RANKING).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, locLocition).withSerializable(RouterUtils.LogRecodeConfig.LOCATION_SIDE, "local").navigation()
                } else {
                    ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SAME_CITY_RANKING).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(location!!.latitude, location!!.longitude, System.currentTimeMillis().toString(), location!!.speed, location!!.latitude, location!!.bearing, location!!.aoiName, location!!.poiName)).withSerializable(RouterUtils.LogRecodeConfig.LOCATION_SIDE, "whole").navigation()
                }
            }
            R.id.toLocation -> {


//                var tele = TelephoneBinder.from(this@LogRecodeFragment)
//                var fr = tele.show()

//                fr.onDismiss(object : DialogInterface {
//                    override fun dismiss() {
////                        loadDatas(viewModel?.location!!)
//                    }
//                    override fun cancel() {
//                    }
//                })

                if (city == null) {
                    initCity()
                }

//                CityPicker.from(mapFragment) //activity或者fragment
//                        .enableAnimation(true)	//启用动画效果，默认无
//                        .setLocatedCity(LocatedCity("杭州", "浙江", "101210101")))  //APP自身已定位的城市，传null会自动定位（默认）
//                        .hotCities


                city!!.show()

//                .setHotCities(hotCities)	//指定热门城市
//                        .setOnPickListener(new OnPickListener() {
//                            @Override
//                            public void onPick(int position, City data) {
//                                Toast.makeText(getApplicationContext(), data.getName(), Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onCancel(){
//                                Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onLocate() {
//                                //定位接口，需要APP自身实现，这里模拟一下定位
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //定位完成之后更新数据
//                                        CityPicker.getInstance()
//                                                .locateComplete(new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
//                                    }
//                                }, 3000);
//                            }
//                        })
//                        .show();

//                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SAME_CITY_LOCATION_AC).navigation()
            }

        }

    }

//    var entity = entity as DynamicsCategoryEntity.Dynamics
//    ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//    .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.memberId)
//    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
//    .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID,4).navigation()


    var active = ObservableArrayList<ActiveData>()
    var Spec = ObservableArrayList<ActiveData>()

    fun activeClick(data: ActiveData) {
        ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 2).withString(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_ID, data.id.toString()).navigation()
    }

    fun addLinearData(linear: LinearLayout, list: ObservableArrayList<ActiveData>) {

        linear.removeAllViews()
        if (!list.isNullOrEmpty()) {
            list.forEachIndexed { index, specActiveData ->
                var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.log_recode_item_layout, linear, false)
                binding.setVariable(BR.spec, specActiveData)
                binding.setVariable(BR.model, this@LogRecodeViewModel)
                linear.addView(binding.root)
            }
        }
        linear.invalidate()
    }


    fun onIngItemClick(data: OningData) {
        if (!data.visibleType.get()!!) {
            return
        }
        when (data.type) {
            1 -> {
                var pos = ServiceEven()
                pos.type = "HomeDriver"
                RxBus.default?.post(pos)
//                context!!.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
                RxBus.default?.post("fastTeam")
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "fastTeam").navigation()
            }
            2 -> {
                var ho = PreferenceUtils.getString(context, PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USERID) + "hot")
                var hot = Gson().fromJson<HotData>(ho, HotData::class.java)
                RxBus.default?.post(hot)
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad").withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, hot).navigation()
            }
            3 -> {
            }
            4 -> {
            }
        }
    }


    fun onIngAddItemClick(data: OningData) {
        Log.e("result", "onIngAddItemClick")
        when (data.type) {
            1 -> {
                var pos = ServiceEven()
                pos.type = "HomeDriver"
                RxBus.default?.post(pos)
//                context!!.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
                RxBus.default?.post("fastTeam")
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "fastTeam").navigation()
            }
            2 -> {
                if (location == null) {
                    Toast.makeText(context, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
                    return
                }
                var loc = Location(location!!.latitude, location!!.longitude, System.currentTimeMillis().toString(), location!!.speed, location!!.height, location!!.bearing, location!!.aoiName, location!!.poiName)
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE, 0).withSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, loc).navigation(mapFragment.activity, REQUEST_LOAD_ROADBOOK)
            }
            3 -> {
            }
            4 -> {
            }
        }
    }

    fun StraggerItem(data: HotData) {
        if (data != null) {
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data).navigation(mapFragment.activity, REQUEST_LOAD_ROADBOOK)
        }
    }

    var onIngAdapter = BindingRecyclerViewAdapter<OningData>()

    var onIngitemBinding = ItemBinding.of<OningData>(BR.oningData, R.layout.log_roadbook_oning_layout).bindExtra(BR.log_road_model, this@LogRecodeViewModel)

    var onIngitems = ObservableArrayList<OningData>()


    var StaggeredAdapter = BindingRecyclerViewAdapter<HotData>()

    var StaggereditemBinding = ItemBinding.of<HotData>(BR.straggerdata, R.layout.stragger_layout).bindExtra(BR.log_road_model, this@LogRecodeViewModel)

    var Staggereditems = ObservableArrayList<HotData>()


    var PartyAdapter = BindingRecyclerViewAdapter<PartyEntity>()

    var PartyitemBinding = ItemBinding.of<PartyEntity>(BR.partyData, R.layout.same_city_partyitem_layout).bindExtra(BR.log_road_model, this@LogRecodeViewModel)

    var Partyitems = ObservableArrayList<PartyEntity>().apply {

    }

    var cityPartyAdapter = BindingRecyclerViewAdapter<CountryMemberEntity>()

    var cityPartyitems = ObservableArrayList<CountryMemberEntity>().apply {
    }
    var listener: RankingClickListener = this
    var cityPartyitemBinding = ItemBinding.of<CountryMemberEntity> { itemBinding, position, item ->
        itemBinding.set(BR.city_party_model, R.layout.city_party_recy_item).bindExtra(BR.position, position).bindExtra(BR.listener, listener)
    }


}