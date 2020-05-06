package com.cstec.administrator.party_module.Activity

import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.CustomNestedScrollView
import com.cstec.administrator.party_module.FixNestedScrollView
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyClockDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyClockDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.getNavigationBarHeight
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.getScreenHeightPx
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
/**
 * 打开活动详情
 * */
@Route(path = RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL)
class PartyClockDetailActivity : BaseFragment<ActivityPartyClockDetailBinding, PartyClockDetailViewModel>(), AppBarLayout.OnOffsetChangedListener {
    override fun initContentView(): Int {
        return R.layout.activity_party_clock_detail
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


    @Autowired(name = RouterUtils.PartyConfig.NavigationType)
    @JvmField
    var navigation: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var city: String = ""

    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {


        if (p1 >= -ConvertUtils.dp2px(240F)) {
            viewModel?.visible!!.set(false)
        } else {
            viewModel?.visible!!.set(true)
        }
    }

    override fun initVariableId(): Int {
        return BR.party_clock_detail_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        Utils.setStatusBar(this, false, false)
//        return R.layout.activity_party_clock_detail
//    }
//
//    override fun initViewModel(): PartyClockDetailViewModel? {
//        return ViewModelProviders.of(this)[PartyClockDetailViewModel::class.java]
//    }

//    override fun doPressBack() {
//        super.doPressBack()
//        returnBack()
//    }

//    fun returnBack() {
//        if (navigation == 0) {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this@PartyClockDetailActivity, object : NavCallback() {
//                override fun onArrival(postcard: Postcard?) {
//                    finish()
//                }
//            })
//        } else if (navigation == 1) {
//            if (mViewModel?.destroyList!!.contains("SubjectPartyActivity")) {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.SUBJECT_PARTY).withInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, 2)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(location!!.latitude, location!!.longitude)).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyClockDetailActivity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
//            } else {
//                finish()
//            }
//        } else if (navigation == 2) {
//            if (mViewModel?.destroyList!!.contains("MyRestoreActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this@PartyClockDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        } else if (navigation == 3) {
//            if (mViewModel?.destroyList!!.contains("SearchPartyActivity")) {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.SEARCH_PARTY).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                        location).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyClockDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        }
//    }

    var flag = false

    var downx = 0F
    var downy = 0F
    var onScroll = false

    var mScrollY = 0;
    var lastScrollY = 0;
    var toolBarPositionY = 0;
    var mOffset = 0
    var h = 0


    lateinit var mPartyDetailClockViewPager: ViewPager
    lateinit var mPartyDetailClockTabLayout: TabLayout
    lateinit var mPartyDetailSubjectMagicTabLayout: TabLayout
    lateinit var clock_toolbar: Toolbar
    lateinit var clock_refreshLayout: SmartRefreshLayout
    lateinit var nest_clock: CustomNestedScrollView
    lateinit var clock_ivHeader: ImageView
    lateinit var clock_buttonBarLayout: TextView
    lateinit var ivClockDetailTrans : ImageView
    lateinit var clock_iv_back : ImageView

    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(true, activity)
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        city = arguments!!.getString(RouterUtils.PartyConfig.PARTY_CITY)
        code = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_CODE)
        party_id = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_ID)
        mPartyDetailClockViewPager = binding!!.root!!.findViewById(R.id.mPartyDetailClockViewPager)
        mPartyDetailClockTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailClockTabLayout)
        mPartyDetailSubjectMagicTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailSubjectMagicTabLayout)
        clock_toolbar = binding!!.root!!.findViewById(R.id.clock_toolbar)
        clock_refreshLayout = binding!!.root!!.findViewById(R.id.clock_refreshLayout)
        nest_clock = binding!!.root!!.findViewById(R.id.nest_clock)
        clock_ivHeader = binding!!.root!!.findViewById(R.id.clock_ivHeader)
        clock_buttonBarLayout = binding!!.root!!.findViewById(R.id.clock_buttonBarLayout)
        clock_iv_back = binding!!.root!!.findViewById(R.id.clock_iv_back)
        ivClockDetailTrans = binding!!.root!!.findViewById(R.id.ivPartyMoBoTrans)


        mPartyDetailClockViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailClockTabLayout))
        mPartyDetailClockTabLayout.setupWithViewPager(mPartyDetailClockViewPager)
//        party_subject_appbar_layout.addOnOffsetChangedListener(this)
        mPartyDetailClockTabLayout.addOnTabSelectedListener(viewModel!!)
//        initTrans(true)
        clock_toolbar.post {
            var height = clock_toolbar.height
            toolBarPositionY = height
            var flag = Utils.isNavigationBarExist(activity!!)
            var params = mPartyDetailClockViewPager.layoutParams
            var values = getScreenHeightPx() - BaseApplication.getInstance().getScreenHights()
            if (flag && values != 0) {
                if (values == org.cs.tec.library.Base.Utils.getStatusBarHeight()) {
                    params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1
                } else {
                    if (values > getNavigationBarHeight(this.activity!!) && values < org.cs.tec.library.Base.Utils.getStatusBarHeight() + getNavigationBarHeight(context!!)) {
                        params.height = BaseApplication.getInstance().getHightPixels - ConvertUtils.dp2pxValue(125F, activity!!)
                    } else {
                        if (Utils.getNavigationBarHeight(activity!!) > values) {
                            params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1 - values    //适配全面屏
                        } else {
                            params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1 - Utils.getNavigationBarHeight(activity!!)     //适配全面屏
                        }
                    }
                }
            } else {
                params.height = getScreenHeightPx() - height - mPartyDetailClockTabLayout.height + 1
            }
            mPartyDetailClockViewPager.layoutParams = params
        }
        clock_refreshLayout.setOnRefreshListener {
            viewModel?.initData()
            it.finishRefresh(10000)
        }

        val color = ContextCompat.getColor(activity!!, R.color.white) and 0x00ffffff
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
                ivClockDetailTrans.setImageResource(R.drawable.more_white)
            } else {
                clock_iv_back.setImageResource(R.drawable.arrow_black)
                ivClockDetailTrans.setImageResource(R.drawable.more_black)
            }
            lastScrollY = y
        })

        clock_buttonBarLayout.setAlpha(0F)
        clock_toolbar.setBackgroundColor(0)
        viewModel?.inject(this)
    }

    var CurrentClickTime = 0L
    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        return y >= view.y && y <= (view.y + view.height - ConvertUtils.dp2px(20F)) && y - downy < view.height
    }

//    fun initTrans(falg: Boolean) {
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
//    }
}