package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
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
import com.cstec.administrator.party_module.ViewModel.PartyClockDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyClockDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.getNavigationBarHeight
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_party_clock_detail.*
import kotlinx.android.synthetic.main.activity_party_detail.*
import kotlinx.android.synthetic.main.activity_party_mobo_detail.*
import kotlinx.android.synthetic.main.party_detail_item_subject_top.*
import kotlinx.android.synthetic.main.party_detail_item_top.*
import kotlinx.android.synthetic.main.party_detail_item_top_clock.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getScreenHeightPx
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
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


    @Autowired(name = RouterUtils.PartyConfig.NavigationType)
    @JvmField
    var navigation: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var city: String = ""

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {


        if (p1 >= -ConvertUtils.dp2px(240F)) {
            mViewModel?.visible!!.set(false)
        } else {
            mViewModel?.visible!!.set(true)
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
        returnBack()
    }

    fun returnBack() {
        if (navigation == 0) {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this@PartyClockDetailActivity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        } else if (navigation == 1) {
            if (mViewModel?.destroyList!!.contains("SubjectPartyActivity")) {
                ARouter.getInstance().build(RouterUtils.PartyConfig.SUBJECT_PARTY).withInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, 2)
                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                                Location(location!!.latitude, location!!.longitude)).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyClockDetailActivity, object : NavCallback() {
                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
            } else {
                finish()
            }
        } else if (navigation == 2) {
            if (mViewModel?.destroyList!!.contains("MyRestoreActivity")) {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this@PartyClockDetailActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            } else {
                finish()
            }
        } else if (navigation == 3) {
            if (mViewModel?.destroyList!!.contains("SearchPartyActivity")) {
                ARouter.getInstance().build(RouterUtils.PartyConfig.SEARCH_PARTY).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        location).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyClockDetailActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            } else {
                finish()
            }
        }
    }

    var flag = false

    var downx = 0F
    var downy = 0F
    var onScroll = false


    var mScrollY = 0;
    var lastScrollY = 0;
    var toolBarPositionY = 0;
    var mOffset = 0
    var h = 0
    override fun initData() {
        super.initData()
        mPartyDetailClockViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailClockTabLayout))
        mPartyDetailClockTabLayout.setupWithViewPager(mPartyDetailClockViewPager)
//        party_subject_appbar_layout.addOnOffsetChangedListener(this)
        mPartyDetailClockTabLayout.addOnTabSelectedListener(mViewModel!!)
        initTrans(true)
        clock_toolbar.post {
            var height = clock_toolbar.height
            toolBarPositionY = height
            var params = mPartyDetailClockViewPager.layoutParams
            var values = getScreenHeightPx() - BaseApplication.getInstance().getScreenHights() - Utils.getStatusBarHeight(context)
            if (flag && values != 0) {
                if (values == org.cs.tec.library.Base.Utils.getStatusBarHeight()) {
                    params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1
                } else {
                    if (values > getNavigationBarHeight(this@PartyClockDetailActivity) && values < org.cs.tec.library.Base.Utils.getStatusBarHeight() + getNavigationBarHeight(context)) {
                        params.height = BaseApplication.getInstance().getHightPixels - ConvertUtils.dp2pxValue(125F, this@PartyClockDetailActivity)
                    } else {
                        params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1 - values     //适配全面屏
                    }
                }
            } else {
                params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1
            }
            mPartyDetailClockViewPager.layoutParams = params
        }
        clock_refreshLayout.setOnRefreshListener {
            mViewModel?.initData()
            it.finishRefresh(10000)
        }

        val color = ContextCompat.getColor(applicationContext, R.color.white) and 0x00ffffff
        clock_refreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
//                mOffset = offset / 2
//                ivHeader.setTranslationY((mOffset - mScrollY).toFloat())
//                subject_toolbar.setAlpha(1 - Math.min(percent, 1f))
            }

            override fun onHeaderReleased(header: RefreshHeader?, headerHeight: Int, maxDragHeight: Int) {
                super.onHeaderReleased(header, headerHeight, maxDragHeight)
//                mOffset = offset / 2
//                ivHeader.setTranslationY((mOffset - mScrollY).toFloat())
//                subject_toolbar.setAlpha(1 - Math.min(percent, 1f))
            }
        })


        nest_clock.setOnScrollChangeListener(FixNestedScrollView.OnScrollChangeListener { p0, scrollX, scrollY, oldScrollX, oldScrollY ->
            var y = scrollY
            //            Log.e("LocationP", "Location2" + p2)
//            Log.e("LocationP", "Location4" + p4)
//            fling = Math.abs(p4 - p2) > ConvertUtils.dp2px(20F)

            Log.e("result", "当前H值" + h + "最小高度" + ConvertUtils.px2dp(912F))
            var location = IntArray(2)
            mPartyDetailClockTabLayout.getLocationOnScreen(location)
            var yPosition = location[1];
            if (h == 0) {
                h = clock_ivHeader.height - toolBarPositionY
            }
//            Log.e("result", "yPosition" + yPosition)  //2314
//            Log.e("result", "toolBarPositionY" + toolBarPositionY)
            if (yPosition < toolBarPositionY) {
//                mPartyDetailSubjectMagicTabLayout.visibility = View.VISIBLE
//                mPartyDetailSubjectTabLayout.visibility = View.INVISIBLE
                nest_clock.setNeedScroll(false)
            } else {
//                mPartyDetailSubjectMagicTabLayout.visibility = View.GONE
//                mPartyDetailSubjectTabLayout.visibility = View.VISIBLE
                nest_clock.setNeedScroll(true)
            }
            if (lastScrollY < h) {
                y = Math.min(h, y)
//                y = y > h ? h : y;
                mScrollY = if (y > h) {
                    h
                } else {
                    y
                }
                Log.e("result", "h=" + h)
                Log.e("result", "y=" + y)
                Log.e("result", "mScrollY= " + mScrollY)
//                Log.e("result", "mScrollY" + mScrollY)
//                Log.e("result", "mScrollH" + h)
//                if (mScrollY == 900) {
//                    Log.e("result","这里执行了吗")
//                    subject_toolbar.setBackgroundColor(Color.WHITE)
//                }
                clock_buttonBarLayout.setAlpha(1f * mScrollY / h);
                clock_toolbar.setBackgroundColor(255 * mScrollY / h shl 24 or color);
                clock_ivHeader.translationY = (mOffset - mScrollY).toFloat();
            }

            if (scrollY == 0) {
                clock_iv_back.setImageResource(R.drawable.arrow_white);
            } else {
                clock_iv_back.setImageResource(R.drawable.arrow_black);
            }
            lastScrollY = y
        })

        clock_buttonBarLayout.setAlpha(0F)
        clock_toolbar.setBackgroundColor(0)
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                finish()
            }
        }
        RxSubscriptions.add(s)
        mViewModel?.inject(this)
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