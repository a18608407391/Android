package com.elder.logrecodemodule.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
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
import com.elder.logrecodemodule.ActivityPartyRecommand
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.Entity.ActivityPartyEntity
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.UI.ActivityFragment
import com.elder.logrecodemodule.adapter.ActivityPartyAdapter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.insertUserInfo
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Inteface.ClockActiveClickListener
import com.elder.zcommonmodule.Inteface.MBCommandClickListener
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Inteface.WonderfulClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.CityPicker.CityPicker
import com.elder.zcommonmodule.Widget.CityPicker.adapter.OnPickListener
import com.elder.zcommonmodule.Widget.CityPicker.model.City
import com.elder.zcommonmodule.Widget.CityPicker.model.LocatedCity
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinder
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinderDialogFragment
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_activity.*
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.*


/**
 * 活动页ViewModel
 * */
class ActivityViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, WeatherSearch.OnWeatherSearchListener, HttpInteface.PartyHome, TitleClickListener, MBCommandClickListener, WonderfulClickListener, ClockActiveClickListener, TelephoneBinderDialogFragment.DismissListener {
    var tvWeather = ObservableField<String>()//天气
    var tvDegree = ObservableField<String>()//度数
    var tvWeek = ObservableField<String>()//星期
    var tvCity = ObservableField<String>("湖南省")//省市
    var vIsField = ObservableField<Boolean>(false)      //搜索隐藏
    lateinit var activityFragment: ActivityFragment
    var curLocation: AMapLocation? = null
    var mweathersearch: WeatherSearch? = null
    var province = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {//选择城市
            tvCity.set(t)
            requestWeatherDataForCity(t)
        }
    })


    /****
     *
     * 下部分内容数据
     * */
    var adapter = ActivityPartyAdapter()
    var activityPartyItems = MergeObservableList<Any>()
    var titlelistener: TitleClickListener = this
    var mb_click: MBCommandClickListener = this
    var wonderfulListener: WonderfulClickListener = this
    var activeListener: ClockActiveClickListener = this
    var itemBinding = OnItemBindClass<Any>()
            .map(String::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.activityPartyTitle, R.layout.activity_party_base_item_title)
                        .bindExtra(BR.activityPartyTitleListener, titlelistener)
                        .bindExtra(BR.activityPartyPosition, position)
            }
            .map(ActivityPartyRecommand::class.java) { itemBinding, position, item ->
                //热门推荐
                itemBinding.set(BR.activityPartyHotRecommend,
                        R.layout.actvity_party_hot_recommend_item_layout)
                        .bindExtra(BR.activityPartyHotViewModel, this)

            }
            .map(ActivityPartyEntity.ClockActive::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.activityPartyClockActivityItemData,
                        R.layout.activity_party_clock_item_layout)
                        .bindExtra(BR.activityPartyListener, activeListener)
            }
            .map(ActivityPartyEntity.HotDistination::class.java) { itemBinding, position, item ->
            }
            .map(ActivityPartyEntity.MBRecommend::class.java) { itemBinding, position, item ->
                //摩旅推荐
                itemBinding.set(BR.activityPartyMbRecommand,
                        R.layout.activity_party_mb_recommand_item_layout)
                        .bindExtra(BR.activityPartyMbListener, mb_click)

            }
            .map(ActivityPartyEntity.WonderfulActive::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.activityPartyWonderful,
                        R.layout.activity_party_wonderful_item_layout)
                        .bindExtra(BR.activityPartyWonderfulListener, wonderfulListener)
            }


    var activityPartyHot = ObservableField<ActivityPartyRecommand>()


    /**********其他数据**************/
    var Staggereditems = ObservableArrayList<HotData>()
    var cityPartyitems = ObservableArrayList<CountryMemberEntity>().apply {
    }

    fun inject(activityFragment: ActivityFragment) {
        this.activityFragment = activityFragment
    }

    fun receiveLocation(it: AMapLocation) {
        if (curLocation == null) {
            Log.e("result", "执行receiveLocation")
            curLocation = it
            mweathersearch = WeatherSearch(context)
            mweathersearch!!.setOnWeatherSearchListener(this)
            requestWeatherDataForCity("湖南省")
        }
    }

    /**
     * 请求选择城市天气数据
     * @param cityName 城市名
     * * */
    fun requestWeatherDataForCity(cityName: String?) {
        var mquery = WeatherSearchQuery(cityName, WeatherSearchQuery.WEATHER_TYPE_LIVE)
        mweathersearch!!.setQuery(mquery);
        mweathersearch!!.searchWeatherAsyn(); //异步搜索
    }

    /**
     * 请求城市活动数据
     * */
    fun requestActivityDataForCity(location: AMapLocation?) {
        HttpRequest.instance.partyHome = this
        var map = HashMap<String, String>()
        map["city"] = tvCity.get()!!
        map["x"] = location!!.longitude.toString()
        map["y"] = location!!.latitude.toString()
        HttpRequest.instance.getPartyHome(map)
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.llSearch -> {
                Log.e("activity", "顶部搜索")
                //顶部搜索
//                startSearchPartyActivity()
            }
            R.id.llToolbarSearch -> {
                Log.e("activity", "toolbar搜索")
                //toolbar搜索
                startSearchPartyActivity()
            }
            R.id.tvSelectLocation -> {
                //城市选择
                DialogUtils.showProviceDialog(activityFragment.activity!!, province, "选择城市")

            }
        }
    }

    fun startSearchPartyActivity() {


        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                Location(curLocation!!.latitude, curLocation!!.longitude))
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
        startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.SEARCH_PARTY, bundle)
//        ARouter.getInstance().build(RouterUtils.PartyConfig.SEARCH_PARTY)
//                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, Location(curLocation!!.latitude, curLocation!!.longitude))
//                .withString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get()).navigation()
    }

    override fun onRefresh() {//下拉刷新
        requestWeatherDataForCity(tvCity.get())
    }

    /**
     * 实时天气查询回调
     * */
    override fun onWeatherLiveSearched(weatherLiveResult: LocalWeatherLiveResult?, rCode: Int) {
        if (rCode == 1000) {
            if (weatherLiveResult?.liveResult != null) {
                var weatherlive = weatherLiveResult.liveResult
                Log.e("activity", "weatherData${weatherlive.temperature}")
                tvDegree.set(weatherlive.temperature + "℃")//温度
                var mWay = Calendar.getInstance()[Calendar.DAY_OF_WEEK].toString()
                tvWeather.set(weatherlive.weather)//天气
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
                tvWeek.set("星期$mWay")//星期
                activityFragment.showProgressDialog("正在获取活动数据，请稍后......")
                requestActivityDataForCity(curLocation)
            }
        }
    }

    override fun onWeatherForecastSearched(p0: LocalWeatherForecastResult?, p1: Int) {

    }


    override fun getPartyHomeSuccess(it: String) {
        activityFragment.dismissProgressDialog()
        activityFragment.refreshFinish()
        if (it.isNullOrEmpty() || it == "系统繁忙") {
            return
        }

        activityPartyItems.removeAll()
        var activityPartyEntity = Gson().fromJson<ActivityPartyEntity>(it, ActivityPartyEntity::class.java)

        var base = BaseResponse()
        base.code = 0
        base.data = activityPartyEntity.memberView
        base.msg = "成功"
        PreferenceUtils.putString(context, RouterUtils.PrivateModuleConfig.USER_INFO, Gson().toJson(base))
        PreferenceUtils.putString(context, USER_PHONE, activityPartyEntity.memberView?.tel)
        PreferenceUtils.putString(context, REAL_NAME, activityPartyEntity.memberView?.realName)
        PreferenceUtils.putString(context, USERID, activityPartyEntity.memberView?.id)
        PreferenceUtils.putBoolean(context, RE_LOGIN, false)
        PreferenceUtils.putString(context, REAL_CODE, activityPartyEntity.memberView?.identityCard)

        insertUserInfo(activityPartyEntity.memberView!!)

        Log.e("result", Gson().toJson(activityPartyEntity.memberView))


        var activityPartyRecommand = ActivityPartyRecommand()
        activityPartyEntity.topActivityList!!.forEach {
            var item = it
            var start = item.ACTIVITY_START!!.split(" ")[0]
            var stop = item.ACTIVITY_STOP!!.split(" ")[0]
            item.ACTIVITY_START = start + "至" + stop + "  距离" + item.SQRTVALUE + "km"
            if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                item.TICKET_PRICE = "免费"
            } else {
                item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
            }
            activityPartyRecommand.list.add(item)
        }


        var clock = ObservableArrayList<ActivityPartyEntity.ClockActive>()
        var mobo = ObservableArrayList<ActivityPartyEntity.MBRecommend>()
        var select = ObservableArrayList<ActivityPartyEntity.WonderfulActive>()
        if (!activityPartyEntity.clockActivityList.isNullOrEmpty()) {
            activityPartyEntity.clockActivityList!!.forEach {
                var item = it
                Log.e("activity", "${item.DISTANCE}")
                item.DISTANCE = "全长${if (item.DISTANCE.isNullOrEmpty()) 0 else item.DISTANCE}km 距离${it.SQRTVALUE}km"
                var start = if (item.ACTIVITY_START.isNullOrEmpty()) {
                    ""
                } else {
                    item.ACTIVITY_START!!.split(" ")[0]
                }
                var stop = if (item.ACTIVITY_STOP.isNullOrEmpty()) {
                    ""
                } else {
                    item.ACTIVITY_STOP!!.split(" ")[0]
                }
                item.ACTIVITY_START = start + "至" + stop
                clock.add(item)
            }
        }
        if (!activityPartyEntity.motoActivityList.isNullOrEmpty()) {
            activityPartyEntity.motoActivityList!!.forEach {
                var item = it
                if (it.DISTANCE.isNullOrEmpty()) {
                    item.DISTANCE = "时长" + item.DAY + "天" + " 里程" + "0" + "km"
                } else {
                    item.DISTANCE = "时长" + item.DAY + "天" + " 里程" + it.DISTANCE + "km"
                }
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }
                mobo.add(item)
            }
        }
        if (!activityPartyEntity.selectedActivityList.isNullOrEmpty()) {
            activityPartyEntity.selectedActivityList!!.forEach {
                var item = it
//                item.DISTANCE = "全长" + item.DISTANCE + "km" + " 距离" + it.SQRTVALUE / 1000 + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop + " 距离" + item.SQRTVALUE + "km"
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }

                if (!item.TYPE.isNullOrEmpty()) {
                    item.TYPE!!.split(",").forEachIndexed { index, s ->
                        when (index) {
                            0 -> {
                                item.type1 = s
                            }
                            1 -> {
                                item.type2 = s
                            }
                            2 -> {
                                item.type3 = s
                            }
                        }
                    }
                }
                select.add(item)
            }

            activityPartyHot.set(activityPartyRecommand)

            activityPartyItems.insertItem(ObservableField("热门推荐").get())
            activityPartyItems.insertItem(activityPartyHot.get())

            if (!clock.isEmpty()) {
                activityPartyItems.insertItem(ObservableField("打卡活动").get())
                activityPartyItems.insertList(clock)
            }
            activityPartyItems.insertItem(ObservableField("摩旅推荐").get())
            activityPartyItems.insertList(mobo)

            activityPartyItems.insertItem(ObservableField("精彩活动").get())
            activityPartyItems.insertList(select)
        }
        activityFragment.poketSwipeRefreshLayout?.isRefreshing = false
        if (activityPartyEntity.memberView?.tel.isNullOrEmpty()) {
            var tele = TelephoneBinder.from(activityFragment)
            var fr = tele.show()
            fr.functionDismiss = this
        } else {
            JMessageClient.login(activityPartyEntity.memberView?.tel, "0123456789", object : BasicCallback() {
                override fun gotResult(responseCode: Int, responseMessage: String?) {
                    if (responseCode == 0) {
                        var myInfo = JMessageClient.getMyInfo()
                        Log.e("result", "IM 登录成功" + Gson().toJson(myInfo))
                    } else {
                        Log.e("result", "IM 登录失败" + responseMessage)
                    }
                }
            })
        }
    }

    override fun getPartyHomeError(it: Throwable) {

    }

    override fun onTitleArrowClick(entity: Any) {
        //处理Arrow跳转
        var title = entity as String
        var type = 0
        if (title == "打卡活动") {
            type = 2
        } else if (title == "摩旅推荐") {
            type = 1
        } else if (title == "精彩活动") {
            type = 0
        }
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                Location(curLocation!!.latitude, curLocation!!.longitude))
        bundle.putInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, type)
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
        startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.SUBJECT_PARTY, bundle)
    }

    var activityPartyHotRecommendCommand = BindingCommand(object : BindingConsumer<ActivityPartyEntity.HotRecommend> {
        //热门推荐点击
        override fun call(t: ActivityPartyEntity.HotRecommend) {
            //处理点击事件
            if (t.ID == 0) {
                return
            }
            var bundle = Bundle()
            bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                    Location(curLocation!!.latitude, curLocation!!.longitude))
            bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
            bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, t.ID)
            bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, t.CODE)
            startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL, bundle)
        }
    })

    override fun onMBcommandClick(entity: Any) {//摩旅推荐点击
        var en = entity as ActivityPartyEntity.MBRecommend

        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                Location(curLocation!!.latitude, curLocation!!.longitude))
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
        bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, en.ID)
        bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, en.CODE)
        startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.PARTY_DETAIL, bundle)
    }

    override fun onWonderfulClick(entity: Any) {//精彩活动点击
        var ac = entity as ActivityPartyEntity.WonderfulActive
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                Location(curLocation!!.latitude, curLocation!!.longitude))
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
        bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, ac.ID)
        bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, ac.CODE)
        startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL, bundle)
    }

    override fun onClockActiveClick(entity: Any) {
        var clock = entity as ActivityPartyEntity.ClockActive
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                Location(curLocation!!.latitude, curLocation!!.longitude))
        bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, tvCity.get())
        bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, clock.ID)
        bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, clock.CODE)
        startFragment(activityFragment.parentFragment!!, RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL, bundle)
    }

    override fun onDismiss() {
        //弹出窗消失了
        requestWeatherDataForCity(tvCity.get())
    }

    override fun onBack() {

    }
}