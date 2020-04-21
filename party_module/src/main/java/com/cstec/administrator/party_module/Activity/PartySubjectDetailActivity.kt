package com.cstec.administrator.party_module.Activity

import android.support.design.widget.TabLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.FixNestedScrollView
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyMoboDetailViewModel
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Utils.ConvertUtils
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import com.cstec.administrator.party_module.CustomNestedScrollView
import com.cstec.administrator.party_module.databinding.ActivityPartyMoboDetailBinding
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.getNavigationBarHeight
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseFragment
import org.cs.tec.library.Base.Utils.*


/**
 * 热门推荐&精彩活动详情页
 * */
@Route(path = RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL)
class PartySubjectDetailActivity : BaseFragment<ActivityPartyMoboDetailBinding, PartyMoboDetailViewModel>() {

    override fun initContentView(): Int {
        return R.layout.activity_party_mobo_detail
    }

    var offset = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var party_id: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.NavigationType)
    @JvmField
    var navigation: Int = 0

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CODE)
    @JvmField
    var code: Int = 0
    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var city: String = ""

    override fun initVariableId(): Int {
        return BR.party_mobo_detail_model
    }
//
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        return R.layout.activity_party_mobo_detail
//    }
//
//    override fun initViewModel(): PartyMoboDetailViewModel? {
//        return ViewModelProviders.of(this)[PartyMoboDetailViewModel::class.java]
//    }

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
    var h = 0
    var baseStatus = 60
    lateinit var mViewPager: ViewPager
    lateinit var mTabLayout: TabLayout
    lateinit var mMagicTabLayout: TabLayout
    lateinit var mToolBar: Toolbar
    lateinit var mRefreshLayout: SmartRefreshLayout
    lateinit var nest: CustomNestedScrollView
    lateinit var ivHeader: ImageView
    lateinit var buttonBarLayout: TextView
    lateinit var ivPartyMoBoTrans :ImageView
    lateinit var iv_back :ImageView
    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(true, activity)
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        city = arguments!!.getString(RouterUtils.PartyConfig.PARTY_CITY)
        code = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_CODE)
        party_id = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_ID)
        mViewPager = binding!!.root!!.findViewById(R.id.mPartyDetailSubjectViewPager)
        mTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailSubjectTabLayout)
        mMagicTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailSubjectMagicTabLayout)
        mToolBar = binding!!.root!!.findViewById(R.id.subject_toolbar)
        mRefreshLayout = binding!!.root!!.findViewById(R.id.refreshLayout)
        nest = binding!!.root!!.findViewById(R.id.nest_subject)
        ivHeader = binding!!.root!!.findViewById(R.id.ivHeader)
        buttonBarLayout = binding!!.root!!.findViewById(R.id.buttonBarLayout)
        iv_back = binding!!.root!!.findViewById(R.id.iv_back)
        ivPartyMoBoTrans = binding!!.root!!.findViewById(R.id.ivPartyMoBoTrans)






        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout.setupWithViewPager(mViewPager)
        AppManager.get()
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mMagicTabLayout))
        mMagicTabLayout.setupWithViewPager(mViewPager)
//        party_subject_appbar_layout.addOnOffsetChangedListener(this)
        mTabLayout.addOnTabSelectedListener(viewModel!!)
        mToolBar.post {
            var height = mToolBar.height
            toolBarPositionY = height
            var params = mViewPager.layoutParams
            var flag = Utils.isNavigationBarExist(activity!!)
            var values = getScreenHeightPx() - BaseApplication.getInstance().getScreenHights()
            Log.e("result", "values" + values)
            Log.e("result", "navigation" + getNavigationBarHeight(activity!!) + flag)
            Log.e("result", "statusbar" + getStatusBarHeight())
            Log.e("result", "realHeight" + getScreenHeightPx())
            Log.e("result", "hight" + BaseApplication.getInstance().getScreenHights())
            Log.e("result", "toolbar" + height + "yPosition" + mToolBar.y)
            Log.e("result", "TabLayout" + mTabLayout.height)
            Log.e("result", "70DP=" + ConvertUtils.dp2px(70F))
            Log.e("result", "mPartyDetailSubjectTabLayout" + mTabLayout.y)
            getScreenRelatedInformation()
            getRealScreenRelatedInformation(activity!!)
            if (flag && values != 0) {
                if (values == getStatusBarHeight()) {
                    Log.e("result", "执行这里3")
                    params.height = getScreenHeightPx() - height - mTabLayout.height + 1
                } else {
                    if (values > getNavigationBarHeight(activity!!) && values < getStatusBarHeight() + getNavigationBarHeight(activity!!)) {
                        Log.e("result", "执行这里2")
                        params.height = BaseApplication.getInstance().getHightPixels - ConvertUtils.dp2pxValue(125F, activity!!)
                    } else {
                        Log.e("result", "执行这里1")
                        if (Utils.getNavigationBarHeight(activity!!) > values) {
                            params.height = getScreenHeightPx() - height - mTabLayout.height + 1 - values    //适配全面屏
                        } else {
                            params.height = getScreenHeightPx() - height - mTabLayout.height + 1 - Utils.getNavigationBarHeight(activity!!)     //适配全面屏
                        }
                    }
                }
            } else {

                Log.e("result", "执行这里")
                params.height = getScreenHeightPx() - height - mTabLayout.height + 1
            }
            mViewPager.layoutParams = params
        }
        mRefreshLayout.setOnRefreshListener {
            viewModel?.initData()
            it.finishRefresh(10000)
        }

        val color = ContextCompat.getColor(activity!!, R.color.white) and 0x00ffffff
        mRefreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
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




        nest.setOnScrollChangeListener(FixNestedScrollView.OnScrollChangeListener { p0, scrollX, scrollY, oldScrollX, oldScrollY ->
            var y = scrollY
            var location = IntArray(2)
            mTabLayout.getLocationOnScreen(location)
            var yPosition = location[1];
            if (h == 0) {
                h = ivHeader.height - toolBarPositionY
            }
//
//
//            Log.e("result", "ivHeader" + ivHeader.height + "width" + ivHeader.width)
//            Log.e("result", "yPosition" + yPosition)  //2314
//            Log.e("result", "toolBarPositionY" + toolBarPositionY)
//            //873
//            //1441
//            //2314

//            Log.e("result", "mCurScrollY= " + y)
            if (yPosition < toolBarPositionY) {
//                mPartyDetailSubjectMagicTabLayout.visibility = View.VISIBLE
//                mPartyDetailSubjectTabLayout.visibility = View.INVISIBLE
                nest.setNeedScroll(false)
            } else {
//                mPartyDetailSubjectMagicTabLayout.visibility = View.GONE
//                mPartyDetailSubjectTabLayout.visibility = View.VISIBLE
                nest.setNeedScroll(true)
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
                buttonBarLayout.setAlpha(1f * mScrollY / h);
                mToolBar.setBackgroundColor(255 * mScrollY / h shl 24 or color);
                ivHeader.translationY = (mOffset - mScrollY).toFloat();
            }

            if (scrollY == 0) {
                iv_back.setImageResource(R.drawable.arrow_white);
                ivPartyMoBoTrans.setImageResource(R.drawable.more_white)
            } else {
                iv_back.setImageResource(R.drawable.arrow_black);
                ivPartyMoBoTrans.setImageResource(R.drawable.more_black);
            }
            lastScrollY = y
        })

        buttonBarLayout.setAlpha(0F)
        mToolBar.setBackgroundColor(0)
        viewModel?.inject(this)
    }

//    fun initTrans(falg: Boolean) {
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
//    }

    var CurrentClickTime = 0L
    private fun isTouchPointInView(view: View?, x: Int, y: Int): Boolean {
        if (view == null) {
            return false
        }
        var m = Math.abs(offset - firstOffset) > ConvertUtils.dp2px(20F)
        return y >= view.y && y <= (view.y + view.height - ConvertUtils.dp2px(20F)) && y - downy < view.height && !m && !fling
    }
//
//    fun returnBack() {
//        var flag = AppManager.get()!!.findActivityIsDestroy("HomeActivity")
//        if (navigation == 0) {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this@PartySubjectDetailActivity, object : NavCallback() {
//                override fun onArrival(postcard: Postcard?) {
//                    finish()
//                }
//            })
//        } else if (navigation == 1) {
//            if (mViewModel?.destroyList!!.contains("SubjectPartyActivity")) {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.SUBJECT_PARTY).withInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, 0)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(location!!.latitude, location!!.longitude)).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartySubjectDetailActivity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
//            } else {
//                finish()
//            }
//        } else if (navigation == 2) {
//            if (mViewModel?.destroyList!!.contains("MyRestoreActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withString(RouterUtils.PartyConfig.PARTY_CITY, city).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this@PartySubjectDetailActivity, object : NavCallback() {
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
//                        location).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartySubjectDetailActivity, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        }
//    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        returnBack()
//    }


}