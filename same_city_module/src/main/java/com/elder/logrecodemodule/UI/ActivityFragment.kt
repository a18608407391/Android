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
                //解决CoordinatorLayout 点击事件冲突
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
    fun refreshFinish() {
        poketSwipeRefreshLayout.isRefreshing = false
    }
    var curOffset = 0
    var lastColor = false
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        curOffset = p1
        //顶部状态切换处理
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