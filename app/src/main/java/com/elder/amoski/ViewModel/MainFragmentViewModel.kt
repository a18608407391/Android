package com.elder.amoski.ViewModel
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.cstec.administrator.social.Fragment.SocialFragment
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.Fragment.HomeFragment
import com.elder.amoski.R
import com.elder.logrecodemodule.UI.ActivityFragment
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.elder.zcommonmodule.Service.*
import com.elder.zcommonmodule.Utils.FileSystem
import com.zk.library.Bus.event.RxBusEven
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.Fragment.RoadBookSearchActivity
import com.example.drivermodule.Fragment.RoadHomeActivity
import com.example.private_module.UI.UserInfoFragment
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getStatusBarHeight
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.util.ArrayList


class MainFragmentViewModel : BaseViewModel, RadioGroup.OnCheckedChangeListener, MapFragment.MapLoadedCallBack, HttpInteface.IRelogin {
    override fun IReloginSuccess(t: String) {
        var resp = Gson().fromJson<BaseResponse>(t, BaseResponse::class.java)
        PreferenceUtils.putString(context, USER_TOKEN, resp.data.toString())
        PreferenceUtils.putLong(context, TOKEN_LIMIT, System.currentTimeMillis())
        BaseApplication.getInstance().lastTokenTime = System.currentTimeMillis()
        BaseApplication.getInstance().TokenTimeOutCheck = false
    }

    override fun IReloginError() {

    }

    override fun LoadedSuccess() {
//        bottomVisible.set(false)
    }


    var returnCheckId = 0
    var bottomVisible = ObservableField<Boolean>(true)
    lateinit var homeActivity: HomeFragment
    var mFragments = ArrayList<Fragment>()
    var type = 2
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var bottomBg = ObservableField<Drawable>(context.getDrawable(R.drawable.home_bottom_bg))
    var logSelected = ObservableField<Boolean>()
    var driverSelected = ObservableField<Drawable>()
    var privateSelected = ObservableField<Boolean>()
    var curPosition = 0
    var myself: UserInfoFragment? = null
    var social: SocialFragment? = null
    var activityFragment: ActivityFragment? = null
    var messageFragment: MessageFragment? = null
    var mapFr: MapFragment? = null
    var tans: FragmentTransaction? = null
    var lastCheckediD = R.id.same_city
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.same_city -> {
                home.mViewModel?.statusHeight!!.set(0)
                changerFragment(0)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.same_city
            }
            R.id.main_left -> {
                home.mViewModel?.statusHeight!!.set(getStatusBarHeight())
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(1)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_left
            }
            R.id.driver_middle -> {
                home.mViewModel?.statusHeight!!.set(getStatusBarHeight())
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(2)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.driver_middle
            }
            R.id.dynamics -> {
                home.mViewModel?.statusHeight!!.set(getStatusBarHeight())
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(3)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.dynamics
            }
            R.id.main_right -> {
                home.mViewModel?.statusHeight!!.set(getStatusBarHeight())
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(4)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_right
            }
        }
    }


    fun checkRelogin() {
        if (!BaseApplication.getInstance().TokenTimeOutCheck) {
            if (System.currentTimeMillis() - BaseApplication.getInstance().lastTokenTime >= BaseApplication.getInstance().TokenTimeOutLimit) {
                Log.e("result", "登录过期")
                BaseApplication.getInstance().TokenTimeOutCheck = true
                var phone: String? = PreferenceUtils.getString(context, USER_PHONE) ?: return
                var map = HashMap<String, String>()
                map["phoneNumber"] = FileSystem.getPhoneBase64(phone!!)
                map["type"] = "app"
                HttpRequest.instance.ReLoginImpl = this
                HttpRequest.instance.relogin(map)
            }
        }
    }

    var amapLocation: AMapLocation? = null
    fun setLocation(it: AMapLocation) {
        amapLocation = it
        if (activityFragment != null) {
            activityFragment!!.viewModel?.receiveLocation(amapLocation!!)
        }
        if (mapFr != null && mapFr!!.isAdded) {
            mapFr?.viewModel?.receiveLocation(amapLocation!!)
        }
        if (myself != null && myself!!.isAdded) {
            myself!!.viewModel?.receiveLocation(amapLocation!!)
        }
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it?.type) {
            RxBusEven.RxLocation -> {
                checkRelogin()
                setLocation(it.value as AMapLocation)
            }
            RxBusEven.DriverReturnRequest -> {
                CoroutineScope(uiContext).launch {
                    if (lastCheckediD == returnCheckId) {
                        mRadioGroup!!.check(R.id.same_city)
                    } else {
                        mRadioGroup!!.check(lastCheckediD)
                    }
                    bottomVisible.set(true)
                }
            }
            RxBusEven.PartyWebViewReturn -> {
                bottomVisible.set(it.value as Boolean)
                if (it.secondValue as Int == 1) {
                    mRadioGroup!!.check(lastCheckediD)
                }
            }
            RxBusEven.ENTER_TO_SEARCH -> {
//                    homeActivity.start(SearchCategoryFragment())
                homeActivity.start((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation() as RoadBookSearchActivity))
            }
            RxBusEven.ENTER_TO_ROAD_HOME -> {
                var loc = it.value as Location
                var type = it.value2 as Int
                homeActivity.start((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).navigation() as RoadHomeActivity).setLocation(loc).setType(type))
            }
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                returnPrivate = true
            }
            RxBusEven.NET_WORK_SUCCESS -> {
                if (mapFr != null && mapFr!!.viewModel?.status!!.startDriver.get() != DriverCancle) {
                    var pos = ServiceEven()
                    pos.type = SERVICE_DRIVER
                    RxBus.default?.post(pos)
                } else {
                    var pos = ServiceEven()
                    pos.type = SERVICE_START
                    RxBus.default?.post(pos)
                }
            }
            RxBusEven.NET_WORK_ERROR -> {
                var pos = ServiceEven()
                pos.type = SERVICE_STOP
                RxBus.default?.post(pos)
            }
            RxBusEven.BrowserSendTeamCode -> {
                //组队Code 问题  非重启app，进入此处
                mRadioGroup!!.check(R.id.driver_middle)
                if (!BaseApplication.MinaConnected) {
                    home.code = it.value as String
                    mapFr!!.teamCode = it.value as String
                    mapFr!!.viewModel?.selectTab(1)
                }
            }
        }
    }


    var mRadioGroup: RadioGroup? = null
    lateinit var home: HomeActivity
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(homeActivity: HomeFragment) {
        this.homeActivity = homeActivity
        this.home = homeActivity.activity as HomeActivity
        mRadioGroup = homeActivity.binding!!.root.findViewById(R.id.main_bottom_bg)
        returnCheckId = R.id.same_city
        initStatus()
    }


    //h5返回，HomeActivity Resume 检查，判断是否切换到用户模块
    var returnPrivate = false

    private fun initStatus() {
        //处理登录状态，默认状态或者骑行
        if (home.resume == "nomal" || home.resume.isNullOrEmpty()) {
            changerFragment(0)
        } else {
            mRadioGroup!!.check(R.id.driver_middle)
            lastCheckediD = R.id.same_city
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun changerFragment(position: Int) {
        if (!homeActivity.isAdded) {
            return
        }
        tans = homeActivity.childFragmentManager.beginTransaction()
        if (position == 0) {
            if (activityFragment == null) {
                var bundle = Bundle()
                activityFragment = ARouter.getInstance().build(RouterUtils.LogRecodeConfig.ACTIVITY_FRAGMENT).navigation() as ActivityFragment
                mFragments.add(activityFragment!!)
                tans?.add(R.id.main_rootlayout, activityFragment!!)
                if (home.location != null) {
                    setLocation(home.location!!)
                    bundle.putParcelable("location", home.location)
                    activityFragment!!.arguments = bundle
                } else {
                    var pos = ServiceEven()
                    pos.type = SERVICE_START
                    RxBus.default?.post(pos)
                }
                if (activityFragment!!.curOffset > -ConvertUtils.dp2px(122F)) {
                    Utils.setStatusTextColor(false, homeActivity.activity as HomeActivity)
                } else {
                    Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                }
                if (home.resume == "nomal" || home.resume.isNullOrEmpty()) {
                    if (mapFr == null) {
                        //普通进入方式，地图懒加载
                        mapFr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_FR).navigation() as MapFragment?
                        mFragments.add(mapFr!!)
                        mapFr?.setDriverStatus(home.resume)
                        tans!!.add(R.id.main_rootlayout, mapFr!!)
                    }
                }
            } else {
                if (activityFragment!!.viewModel?.activityPartyItems.isNullOrEmpty()) {
                    activityFragment!!.viewModel?.requestActivityDataForCity(amapLocation)
                }
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            logSelected.set(true)
            privateSelected.set(false)
        } else if (position == 1) {
            if (social == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", activityFragment!!.viewModel?.curLocation!!)
                social = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_MAIN).navigation() as SocialFragment
                social!!.arguments = bundle
                mFragments.add(social!!)
                tans!!.add(R.id.main_rootlayout, social!!)
            }
        } else if (position == 2) {
            if (mapFr == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", home.location)
                mapFr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_FR).navigation() as MapFragment?
                mapFr!!.arguments = bundle
                mFragments.add(mapFr!!)
                mapFr!!.loaded = this
                mapFr?.setDriverStatus(home.resume)
                if (home.resume == "road") {
                    mapFr!!.hotData = homeActivity.hot
                }
                tans!!.add(R.id.main_rootlayout, mapFr!!)
            }
            if (mapFr!!.isAdded && !mapFr!!.initStatus) {
                mapFr!!.initMap()
            }
            mapFr!!.setCode(home.code)
            bottomVisible.set(false)
        } else if (position == 3) {
            if (messageFragment == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", activityFragment!!.viewModel?.curLocation!!)
                messageFragment = ARouter.getInstance().build(RouterUtils.Chat_Module.MESSAGE_FRAGMENT).navigation() as MessageFragment
                messageFragment!!.arguments = bundle
                mFragments.add(messageFragment!!)
                tans?.add(R.id.main_rootlayout, messageFragment!!)
            }
        } else if (position == 4) {
            if (myself == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", activityFragment!!.viewModel?.curLocation!!)
                myself = ARouter.getInstance().build(RouterUtils.FragmentPath.MYSELFPAGE).navigation() as UserInfoFragment
                myself!!.arguments = bundle
                mFragments.add(myself!!)
                tans?.add(R.id.main_rootlayout, myself!!)
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            var fr = myself as UserInfoFragment
            fr.getUserInfo(true)
            logSelected.set(false)
            privateSelected.set(true)
        }
        curPosition = position
        mFragments!!.forEach {
            tans!!.hide(it)
        }
        if (position == 1) {
            tans!!.show(social!!)
        } else if (position == 2) {
            tans!!.show(mapFr!!)
        } else if (position == 3) {
            tans!!.show(messageFragment!!)
        } else if (position == 4) {
            tans!!.show(myself!!)
        } else {
            tans!!.show(mFragments!![position])
        }
        tans!!.commitAllowingStateLoss()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.main_left -> {
                if (curPosition == 0) {
                    return
                }
                changerFragment(0)
            }
            R.id.main_right -> {
                if (curPosition == 1) {
                    return
                }
                changerFragment(1)
            }
        }
    }

    constructor() {
        var m = RxBus.default?.toObservable(ActivityResultEven::class.java)?.subscribe {
            when (it.name) {
                CALL_BACK_STATUS -> {
                    type = it.data as Int
                    if (type == 2) {
                        driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                    } else {
                        driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
                    }
                }
            }
        }

        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ShareFinish") {
                mRadioGroup!!.check(R.id.same_city)
                var log = mFragments[0] as LogRecodeFragment
                log.loadDatas(log.viewModel?.location!!)
                type = DriverCancle
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                AppManager.get()?.finishOtherActivity(homeActivity.javaClass)
            } else if (it == "ToUser") {
              home.runOnUiThread {
                    mRadioGroup!!.check(R.id.main_right)
                }
            }
        }
        RxSubscriptions.add(m)
        RxSubscriptions.add(s)
    }
}