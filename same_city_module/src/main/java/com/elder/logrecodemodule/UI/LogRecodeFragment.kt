package com.elder.logrecodemodule.UI

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearSnapHelper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.dinuscxj.progressbar.CircleProgressBar
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.LogRecodeViewModel
import com.elder.logrecodemodule.databinding.FragmentLogrecodeBinding
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.insertUserInfo
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Even.RequestErrorEven
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinder
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.RouterUtils.PrivateModuleConfig.Companion.USER_INFO
import kotlinx.android.synthetic.main.fragment_logrecode.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.HashMap


//此处改为同城模块  之前为日志模块  如果有强迫症  可以将本模块下部分界面搬至


@Route(path = RouterUtils.LogRecodeConfig.LogRecodeFR)
class LogRecodeFragment : BaseFragment<FragmentLogrecodeBinding, LogRecodeViewModel>(), AppBarLayout.OnOffsetChangedListener, HttpInteface.HomeResult, RadioGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.local_rb -> {
                viewModel?.cityPartyitems!!.clear()
                home?.sameCityMember?.forEach {
                    viewModel?.cityPartyitems!!.add(it)
                }
            }
            R.id.nation_wide_rb -> {
                viewModel?.cityPartyitems!!.clear()
                home?.wholeCountryMember?.forEach {
                    viewModel?.cityPartyitems!!.add(it)
                }
            }
        }
    }

    var home: HomeEntitiy.HomeBean? = null
    override fun ResultHomeSuccess(it: String) {
        if (it.isNullOrEmpty() || it == "null") {
            return
        }
        if (dialogs != null) {
            dialogs!!.dismiss()
        }
        if (it.length < 10) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            return
        }
        var home = Gson().fromJson<HomeEntitiy.HomeBean>(it, HomeEntitiy.HomeBean::class.java)
        this.home = home
        var base = BaseResponse()
        base.code = 0
        base.data = home.findMemberView
        base.msg = "成功"
        PreferenceUtils.putString(context, USER_INFO, Gson().toJson(base))
        PreferenceUtils.putString(context, USER_PHONE, home.findMemberView?.tel)
        PreferenceUtils.putString(context, REAL_NAME, home.findMemberView?.realName)
        PreferenceUtils.putString(context, USERID, home.findMemberView?.id)
        PreferenceUtils.putBoolean(context, RE_LOGIN, false)
        PreferenceUtils.putString(context, REAL_CODE, home.findMemberView?.identityCard)
        insertUserInfo(home.findMemberView!!)
        viewModel?.Staggereditems!!.clear()
        viewModel?.cityPartyitems!!.clear()
        viewModel?.cityPartyitems!!.clear()
        home?.queryGuideList?.forEach {
            viewModel?.Staggereditems!!.add(it)
        }
        if (local_rg.checkedRadioButtonId == R.id.local_rb) {
            home?.sameCityMember?.forEach {
                viewModel?.cityPartyitems!!.add(it)
            }
        } else {
            home?.wholeCountryMember?.forEach {
                viewModel?.cityPartyitems!!.add(it)
            }
        }
        log_swipe.isRefreshing = false
        if (home.findMemberView?.tel.isNullOrEmpty()) {
            var tele = TelephoneBinder.from(this@LogRecodeFragment)
            var fr = tele.show()
            fr.functionDismiss = viewModel
        } else {
            JMessageClient.login(home.findMemberView?.tel, "0123456789", object : BasicCallback() {
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

    override fun ResultHomeError(it: Throwable) {
        if (dialogs != null) {
            dialogs!!.dismiss()
        }
    }

    var curOffset = 0
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        curOffset = p1
//        Log.e("result", "offset" + ConvertUtils.px2dp(Math.abs(p1) * 1F))
        if (p1 >= -ConvertUtils.dp2px(30F)) {
            Utils.setStatusTextColor(false, activity)
            viewModel?.VisField!!.set(false)
            log_swipe.isEnabled = p1 >= 0
        } else {
            Utils.setStatusTextColor(true, activity)
            log_swipe.isEnabled = false
            viewModel?.VisField!!.set(true)
        }
    }

    var onCreate = false

    override fun initContentView(): Int {
        onCreate = true
        return R.layout.fragment_logrecode
    }

    override fun initVariableId(): Int {
        return BR.log_model
    }

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

    var CurrentClickTime = 0L
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
        if (this.isAdded) {
            local_rg.setOnCheckedChangeListener(this)
            log_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
            LinearSnapHelper().attachToRecyclerView(bottom_pager)
            log_swipe.setOnRefreshListener(viewModel)
            appbar_layout.addOnOffsetChangedListener(this)
            toolbar.setOnTouchListener { v, event ->


                var g = isTouchPointInView(log_enter, event.x.toInt(), event.y.toInt())
                if (g) {
                    if (System.currentTimeMillis() - CurrentClickTime < 1000) {
                    } else {
                        CurrentClickTime = System.currentTimeMillis()
                        ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SEARCH_MEMBER).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel?.location).navigation()
                    }
                }
                return@setOnTouchListener log_enter.dispatchTouchEvent(event)
            }
        }

        RxSubscriptions.add(RxBus.default?.toObservable(DriverDataStatus::class.java)?.subscribe {
            loadDatas(viewModel?.location!!)
        })
        mile_circle.progress = 0
        mile_circle.max = 100
        time_circle.progress = 0
        time_circle.max = 100
        mile_circle.setProgressFormatter(object : CircleProgressBar.ProgressFormatter {
            override fun format(progress: Int, max: Int): CharSequence {
                return "10333" + "/r/n" + "KM"
            }
        })
        time_circle.setProgressFormatter(object : CircleProgressBar.ProgressFormatter {
            override fun format(progress: Int, max: Int): CharSequence {
                return "10333" + "/r/n" + "KM"
            }
        })
    }

    var dialogs: Dialog? = null

    fun loadDatas(location: Location) {
        if (location == null) {
            return
        }
        if (isAdded) {
            if (!NetworkUtil.isNetworkAvailable(activity!!)) {
                Toast.makeText(activity, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
                return
            }
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        if (token == null) {
            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(activity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    activity?.finish()
                }
            })
            return
        }
        if (isAdded) {
            HttpRequest.instance.homeResult = this
            dialogs = DialogUtils.showProgress(activity!!, getString(R.string.getDriverInfo))
            var map = HashMap<String, String>()
            map["yAxis"] = location!!.longitude.toString()
            map["xAxis"] = location!!.latitude.toString()
            HttpRequest.instance.getHome(map)
        }
    }
}