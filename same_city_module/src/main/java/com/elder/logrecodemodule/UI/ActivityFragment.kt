package com.elder.logrecodemodule.UI

import android.databinding.ObservableArrayList
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearSnapHelper
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.ActivityViewModel
import com.elder.logrecodemodule.databinding.FragmentActivityBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.elder.logrecodemodule.BR
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.insertUserInfo
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Entity.HomeEntitiy
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinder
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_activity.toolbar
import kotlinx.android.synthetic.main.fragment_logrecode.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.HashMap

/**
 * 活动页View
 * */
@Route(path = RouterUtils.LogRecodeConfig.ACTIVITY_FRAGMENT)
class ActivityFragment : BaseFragment<FragmentActivityBinding, ActivityViewModel>(), AppBarLayout.OnOffsetChangedListener {

    override fun initContentView(): Int {
        return R.layout.fragment_activity
    }

    override fun initVariableId(): Int {
        return BR.activityViewModel
    }

    var CurrentClickTime = 0L
    override fun initData() {
        super.initData()
        if (arguments!!.get("location") != null) {
            viewModel?.receiveLocation(arguments!!.getParcelable<AMapLocation>("location"))
        }

        viewModel?.inject(this)
        if (this.isAdded) {
            poketSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
            appBarLayout.addOnOffsetChangedListener(this)
            poketSwipeRefreshLayout.setOnRefreshListener(viewModel)
            toolbar.setOnTouchListener { v, event ->
                var g = isTouchPointInView(llSearch, event.x.toInt(), event.y.toInt())
                if (g) {
                    if (System.currentTimeMillis() - CurrentClickTime < 1000) {
                    } else {
                        CurrentClickTime = System.currentTimeMillis()
                        viewModel?.startSearchPartyActivity()
                    }
                }
                return@setOnTouchListener llSearch.dispatchTouchEvent(event)
            }
        }
        RxSubscriptions.add(RxBus.default?.toObservable(DriverDataStatus::class.java)?.subscribe {
            Log.e("activity", "DriverDataStatus")
//            loadDatas(viewModel?.curLocation!!)
        })

    }

//    fun loadDatas(location: AMapLocation) {
//        if (location == null) {
//            return
//        }
//        if (isAdded) {
//            if (!NetworkUtil.isNetworkAvailable(activity!!)) {
//                Toast.makeText(activity, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                return
//            }
//        }
//        var token = PreferenceUtils.getString(context, USER_TOKEN)
//        if (token == null) {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(activity, object : NavCallback() {
//                override fun onArrival(postcard: Postcard?) {
//                    activity?.finish()
//                }
//            })
//            return
//        }
//        if (isAdded) {
//            HttpRequest.instance.homeResult = this
//            var map = HashMap<String, String>()
//            map["yAxis"] = location!!.longitude.toString()
//            map["xAxis"] = location!!.latitude.toString()
//            HttpRequest.instance.getHome(map)
//        }
//    }

    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        val location = IntArray(2)
        view!!.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view!!.getMeasuredWidth()
        val bottom = top + view!!.getMeasuredHeight()
        //view.isClickable() &&
        return (y in top..bottom && x >= left
                && x <= right)
    }

    fun refreshFinish() {
        poketSwipeRefreshLayout.isRefreshing = false
    }

    var curOffset = 0
    var lastColor = false
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        curOffset = p1
        if (p1 >= -ConvertUtils.dp2px(122F)) {
            if (lastColor) {
                Utils.setStatusTextColor(false, activity)
                lastColor = false
            }
            viewModel?.vIsField!!.set(false)

            poketSwipeRefreshLayout.isEnabled = p1 >= 0
        } else {
            if (!lastColor) {
                Utils.setStatusTextColor(true, activity)
                lastColor = true
            }
            poketSwipeRefreshLayout.isEnabled = false
            viewModel?.vIsField!!.set(true)
        }
    }

//
//    override fun ResultHomeSuccess(it: String) {
//        if (it.isNullOrEmpty() || it == "null") {
//            return
//        }
//        if (it.length < 10) {
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//            return
//        }
//        var home = Gson().fromJson<HomeEntitiy.HomeBean>(it, HomeEntitiy.HomeBean::class.java)
//        var base = BaseResponse()
//        base.code = 0
//        base.data = home.findMemberView
//        base.msg = "成功"
//        PreferenceUtils.putString(context, RouterUtils.PrivateModuleConfig.USER_INFO, Gson().toJson(base))
//        PreferenceUtils.putString(context, USER_PHONE, home.findMemberView?.tel)
//        PreferenceUtils.putString(context, REAL_NAME, home.findMemberView?.realName)
//        PreferenceUtils.putString(context, USERID, home.findMemberView?.id)
//        PreferenceUtils.putBoolean(context, RE_LOGIN, false)
//        PreferenceUtils.putString(context, REAL_CODE, home.findMemberView?.identityCard)
//
//        insertUserInfo(home.findMemberView!!)
//        Log.e(this.javaClass.name, "${home.findMemberView}")
//        viewModel?.Staggereditems!!.clear()
//        viewModel?.cityPartyitems!!.clear()
//        viewModel?.cityPartyitems!!.clear()
//        home?.queryGuideList?.forEach {
//            viewModel?.Staggereditems!!.add(it)
//        }
//        poketSwipeRefreshLayout?.isRefreshing = false
//        if (home.findMemberView?.tel.isNullOrEmpty()) {
//            var tele = TelephoneBinder.from(this)
//            var fr = tele.show()
//            fr.functionDismiss = viewModel
//        } else {
//            JMessageClient.login(home.findMemberView?.tel, "0123456789", object : BasicCallback() {
//                override fun gotResult(responseCode: Int, responseMessage: String?) {
//                    if (responseCode == 0) {
//                        var myInfo = JMessageClient.getMyInfo()
//                        Log.e("result", "IM 登录成功" + Gson().toJson(myInfo))
//                    } else {
//                        Log.e("result", "IM 登录失败" + responseMessage)
//                    }
//                }
//            })
//        }
//    }
//
//    override fun ResultHomeError(it: Throwable) {
//    }

    private var WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0

    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity!!.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            Toast.makeText(_mActivity, "再次点击退出App", Toast.LENGTH_SHORT).show()
        }
        return true
    }

}