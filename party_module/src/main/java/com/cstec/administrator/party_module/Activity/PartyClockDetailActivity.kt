package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyClockDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyClockDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_party_clock_detail.*
import kotlinx.android.synthetic.main.activity_party_detail.*
import org.cs.tec.library.Utils.ConvertUtils

@Route(path = RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL)
class PartyClockDetailActivity : BaseActivity<ActivityPartyClockDetailBinding, PartyClockDetailViewModel>(), AppBarLayout.OnOffsetChangedListener {


    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var party_id: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CODE)
    @JvmField
    var code: Int = 0
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
//        Log.e("result", "offset" + ConvertUtils.px2dp(Math.abs(p1) * 1F))
        if (p1 >= -ConvertUtils.dp2px(300F)) {
            mViewModel?.visible!!.set(false)
//            Utils.setStatusTextColor(false, activity)
//            viewModel?.VisField!!.set(false)
//            log_swipe.isEnabled = p1 >= 0
        } else {
            mViewModel?.visible!!.set(true)
//            Utils.setStatusTextColor(true, activity)
//            log_swipe.isEnabled = false
//            viewModel?.VisField!!.set(true)
        }
    }

    override fun initVariableId(): Int {
        return BR.party_clock_detail_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        Utils.setStatusBar(this, false, false)
        return R.layout.activity_party_clock_detail
    }

    override fun initViewModel(): PartyClockDetailViewModel? {
        return ViewModelProviders.of(this)[PartyClockDetailViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initData() {
        super.initData()
        mPartyClockDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyClockDetailTabLayout))
        mPartyClockDetailTabLayout.setupWithViewPager(mPartyClockDetailViewPager)
        party_clock_appbar_layout.addOnOffsetChangedListener(this)
        mPartyClockDetailTabLayout.addOnTabSelectedListener(mViewModel!!)
        initTrans(true)
        nest_clock.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { p0, p1, p2, p3, p4 ->
            val location = IntArray(2)
            mPartyClockDetailTabLayout.getLocationOnScreen(location)
            var xPosition = location[0]
            var yPosition = location[1]
            Log.e("result", "offset" + ConvertUtils.px2dp(yPosition * 1F))
            if (ConvertUtils.px2dp(yPosition * 1F) < 55) {
                nest_clock.setNeedScroll(false)
            } else {
                nest_clock.setNeedScroll(true)
            }
        })
        mViewModel?.inject(this)
    }

    fun initTrans(falg: Boolean) {
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
    }
}