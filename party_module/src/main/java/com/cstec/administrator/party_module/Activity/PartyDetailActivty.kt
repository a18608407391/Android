package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.FixNestedScrollView
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.R.id.members_click
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_party_detail.*
import kotlinx.android.synthetic.main.activity_party_mobo_detail.*
import kotlinx.android.synthetic.main.party_detail_item_subject_top.*
import kotlinx.android.synthetic.main.party_detail_item_top.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils


@Route(path = RouterUtils.PartyConfig.PARTY_DETAIL)
class PartyDetailActivty : BaseActivity<ActivityPartyDetailBinding, PartyDetailViewModel>(), AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        if (p1 >= -ConvertUtils.dp2px(300F)) {
            mViewModel?.visible!!.set(false)
        } else {
            mViewModel?.visible!!.set(true)
        }
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var party_id: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CODE)
    @JvmField
    var code: Int = 0

    override fun initVariableId(): Int {
        return BR.party_detail_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        Utils.setStatusBar(this, false, false)
        return R.layout.activity_party_detail
    }

    override fun initViewModel(): PartyDetailViewModel? {
        return ViewModelProviders.of(this)[PartyDetailViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        returnBack()
    }


    var flag = false

    var downx = 0F
    var downy = 0F
    var onScroll = false
    override fun initData() {
        super.initData()
        mPartyDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailTabLayout))
        mPartyDetailTabLayout.setupWithViewPager(mPartyDetailViewPager)
        mPartyDetailTabLayout.addOnTabSelectedListener(mViewModel!!)
        party_appbar_layout.addOnOffsetChangedListener(this)
        initTrans(true)
        detail_toolbar.post {
            var height = detail_toolbar.height
            var params = mPartyDetailViewPager.layoutParams
            params.height = BaseApplication.getInstance().getHightPixels - height - ConvertUtils.dp2px(50F)
            mPartyDetailViewPager.layoutParams = params
        }
        nest.setOnScrollChangeListener(FixNestedScrollView.OnScrollChangeListener { p0, p1, p2, p3, p4 ->
            val location = IntArray(2)
            mPartyDetailTabLayout.getLocationOnScreen(location)
            var xPosition = location[0]
            var yPosition = location[1]
            if (ConvertUtils.px2dp(yPosition * 1F) < 300) {
                nest.setNeedScroll(false)
            } else {
                nest.setNeedScroll(true)
            }

        })
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                finish()
            }
        }
        RxSubscriptions.add(s)
        mViewModel?.inject(this)
    }

    fun returnBack() {
        if (mViewModel?.destroyList!!.contains("HomeActivity")) {
            Log.e("result", "当前界面只有一个了")
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        } else {
            finish()
        }
    }

    var CurrentClickTime = 0L
    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        return y >= view.y && y <= (view.y + view.height - ConvertUtils.dp2px(20F)) && y - downy < view.height
    }

    fun initTrans(falg: Boolean) {
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
    }

}