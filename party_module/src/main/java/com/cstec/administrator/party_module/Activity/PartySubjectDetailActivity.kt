package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.FixNestedScrollView
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyMoboDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyClockDetailBinding
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
import android.support.v4.content.ContextCompat
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener


@Route(path = RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL)
class PartySubjectDetailActivity : BaseActivity<ActivityPartyClockDetailBinding, PartyMoboDetailViewModel>(), AppBarLayout.OnOffsetChangedListener {


    var offset = 0
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        Log.e("result", "Subjectoffset" + ConvertUtils.px2dp(p1 * 1F))
        offset = ConvertUtils.px2dp(p1 * 1F)
        if (p1 >= -ConvertUtils.dp2px(280F)) {
            mViewModel?.visible!!.set(false)
            nest_subject.setNeedScroll(true)
//            Utils.setStatusTextColor(false, activity)
//            viewModel?.VisField!!.set(false)
//            log_swipe.isEnabled = p1 >= 0
        } else {
            mViewModel?.visible!!.set(true)
            nest_subject.setNeedScroll(false)
//            Utils.setStatusTextColor(true, activity)
//            log_swipe.isEnabled = false
//            viewModel?.VisField!!.set(true)
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
        return BR.party_mobo_detail_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_party_mobo_detail
    }

    override fun initViewModel(): PartyMoboDetailViewModel? {
        return ViewModelProviders.of(this)[PartyMoboDetailViewModel::class.java]
    }

    var fling = false
    var flag = false
    var downx = 0F
    var downy = 0F
    var onScroll = false
    var firstOffset = 0
    var mScrollY = 0;
    var lastScrollY = 0;
    var toolBarPositionY = 0;
    var mOffset = 0
    var h = ConvertUtils.dp2px(300F)
    override fun initData() {
        super.initData()
        mPartyDetailSubjectViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailSubjectTabLayout))
        mPartyDetailSubjectTabLayout.setupWithViewPager(mPartyDetailSubjectViewPager)
        mPartyDetailSubjectViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailSubjectMagicTabLayout))
        mPartyDetailSubjectMagicTabLayout.setupWithViewPager(mPartyDetailSubjectViewPager)
//        party_subject_appbar_layout.addOnOffsetChangedListener(this)
        mPartyDetailSubjectTabLayout.addOnTabSelectedListener(mViewModel!!)
        initTrans(true)
        subject_toolbar.post {
            var height = subject_toolbar.height
            toolBarPositionY = height
            var params = mPartyDetailSubjectViewPager.layoutParams
            params.height = BaseApplication.getInstance().getHightPixels - height - ConvertUtils.dp2px(42F)+1
            mPartyDetailSubjectViewPager.layoutParams = params
        }


        val color = ContextCompat.getColor(applicationContext, R.color.white) and 0x00ffffff
        refreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            fun onHeaderPulling(header: RefreshHeader, percent: Float, offset: Int, bottomHeight: Int, extendHeight: Int) {
                mOffset = offset / 2
                ivHeader.setTranslationY((mOffset - mScrollY).toFloat())
                subject_toolbar.setAlpha(1 - Math.min(percent, 1f))
            }

            fun onHeaderReleasing(header: RefreshHeader, percent: Float, offset: Int, bottomHeight: Int, extendHeight: Int) {
                mOffset = offset / 2
                ivHeader.setTranslationY((mOffset - mScrollY).toFloat())
                subject_toolbar.setAlpha(1 - Math.min(percent, 1f))
            }
        })
        nest_subject.setOnScrollChangeListener(FixNestedScrollView.OnScrollChangeListener { p0, scrollX, scrollY, oldScrollX, oldScrollY ->

            var y = scrollY

            //            Log.e("LocationP", "Location2" + p2)
//            Log.e("LocationP", "Location4" + p4)
//            fling = Math.abs(p4 - p2) > ConvertUtils.dp2px(20F)

            var location = IntArray(2)
            mPartyDetailSubjectTabLayout.getLocationOnScreen(location)
            var yPosition = location[1];
//            Log.e("result", "yPosition" + yPosition)
//            Log.e("result", "toolBarPositionY" + toolBarPositionY)
            if (yPosition < toolBarPositionY) {
                mPartyDetailSubjectMagicTabLayout.visibility = View.VISIBLE
                nest_subject.setNeedScroll(false)
            } else {
                mPartyDetailSubjectMagicTabLayout.visibility = View.GONE
                nest_subject.setNeedScroll(true)
            }

            if (lastScrollY < h) {
                y = Math.min(h, scrollY)
//                y = y > h ? h : y;
                mScrollY = if (y > h) {
                    h
                } else {
                    y
                }

                Log.e("result","mScrollY" + mScrollY)
                Log.e("result","mScrollH" + h)
                buttonBarLayout.setAlpha(1f * mScrollY / h);
                if(mScrollY==h){
                    subject_toolbar.setBackgroundColor(Color.WHITE)
                }

//                subject_toolbar.setBackgroundColor(255 * mScrollY / h shl 24 or color);
                ivHeader.translationY = (mOffset - mScrollY).toFloat();
            }
//            if (scrollY == 0) {
//                ivBack.setImageResource(R.drawable.back_white);
//                ivMenu.setImageResource(R.drawable.icon_menu_white);
//            } else {
//                ivBack.setImageResource(R.drawable.back_black);
//                ivMenu.setImageResource(R.drawable.icon_menu_black);
//            }

            lastScrollY = scrollY
            buttonBarLayout.setAlpha(0F)
            subject_toolbar.setBackgroundColor(0)
            mPartyDetailSubjectViewPager.setOffscreenPageLimit(10);
        })
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                finish()
            }
        }
        RxSubscriptions.add(s)
        mViewModel?.inject(this)
    }

    fun initTrans(falg: Boolean) {
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
    }

    var CurrentClickTime = 0L
    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }

        var m = Math.abs(offset - firstOffset) > ConvertUtils.dp2px(20F)
        return y >= view.y && y <= (view.y + view.height - ConvertUtils.dp2px(20F)) && y - downy < view.height && !m && !fling
    }

    fun returnBack() {
        if (mViewModel?.destroyList!!.contains("HomeActivity")) {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        } else {
            finish()
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        returnBack()
    }
}