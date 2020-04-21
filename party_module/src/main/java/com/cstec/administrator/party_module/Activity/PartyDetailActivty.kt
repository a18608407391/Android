package com.cstec.administrator.party_module.Activity

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
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Utils.Utils.*
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.party_detail_item_top.*
import org.cs.tec.library.Base.Utils.getScreenHeightPx
import org.cs.tec.library.Utils.ConvertUtils

/**
 * 摩旅推荐详情页
 * */
@Route(path = RouterUtils.PartyConfig.PARTY_DETAIL)
class PartyDetailActivty : BaseFragment<ActivityPartyDetailBinding, PartyDetailViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_party_detail
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

    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var city: String = ""

    @Autowired(name = RouterUtils.PartyConfig.NavigationType)
    @JvmField
    var navigation: Int = 0

    override fun initVariableId(): Int {
        return BR.party_detail_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        Utils.setStatusBar(this, false, false)
//        return R.layout.activity_party_detail
//    }
//
//    override fun initViewModel(): PartyDetailViewModel? {
//        return ViewModelProviders.of(this)[PartyDetailViewModel::class.java]
//    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        returnBack()
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


    lateinit var mPartyDetailViewPager: ViewPager
    lateinit var mPartyDetailTabLayout: TabLayout
    lateinit var mPartyDetailMagicTabLayout: TabLayout
    lateinit var detail_toolbar: Toolbar
    lateinit var detail_refreshLayout: SmartRefreshLayout
    lateinit var nest: CustomNestedScrollView
    lateinit var detail_ivHeader: ImageView
    lateinit var detail_buttonBarLayout: TextView
    lateinit var ivPartyDetailTrans: ImageView
    lateinit var detail_iv_back: ImageView
    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(true, activity)
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        city = arguments!!.getString(RouterUtils.PartyConfig.PARTY_CITY)
        code = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_CODE)
        party_id = arguments!!.getInt(RouterUtils.PartyConfig.PARTY_ID)


        mPartyDetailViewPager = binding!!.root!!.findViewById(R.id.mPartyDetailViewPager)
        mPartyDetailTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailTabLayout)
        mPartyDetailMagicTabLayout = binding!!.root!!.findViewById(R.id.mPartyDetailMagicTabLayout)
        detail_toolbar = binding!!.root!!.findViewById(R.id.detail_toolbar)
        detail_refreshLayout = binding!!.root!!.findViewById(R.id.detail_refreshLayout)
        nest = binding!!.root!!.findViewById(R.id.nest)
        detail_ivHeader = binding!!.root!!.findViewById(R.id.detail_ivHeader)
        detail_buttonBarLayout = binding!!.root!!.findViewById(R.id.detail_buttonBarLayout)
        detail_iv_back = binding!!.root!!.findViewById(R.id.detail_iv_back)
        ivPartyDetailTrans = binding!!.root!!.findViewById(R.id.ivPartyDetailTrans)

        mPartyDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailTabLayout))
        mPartyDetailTabLayout.setupWithViewPager(mPartyDetailViewPager)
        mPartyDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailMagicTabLayout))
        mPartyDetailMagicTabLayout.setupWithViewPager(mPartyDetailViewPager)
//        party_subject_appbar_layout.addOnOffsetChangedListener(this)
        mPartyDetailTabLayout.addOnTabSelectedListener(viewModel!!)
        detail_toolbar.post {
            var height = detail_toolbar.height
            toolBarPositionY = height
            var params = mPartyDetailViewPager.layoutParams
            var flag = Utils.isNavigationBarExist(activity!!)
            var values = getScreenHeightPx() - BaseApplication.getInstance().getScreenHights()

//            params.height = detail_top_layout.height + detail_ivHeader.height - toolBarPositionY
            Log.e("result", "Values" + values + getNavigationBarHeight(activity!!))
            if (flag && values != 0) {
                if (values == org.cs.tec.library.Base.Utils.getStatusBarHeight()) {
                    params.height = getScreenHeightPx() - height - mPartyDetailTabLayout.height + 1
                    Log.e("result", "执行这里" + flag)
                } else {
                    if (values > getNavigationBarHeight(activity!!) && values < org.cs.tec.library.Base.Utils.getStatusBarHeight() + getNavigationBarHeight(context)) {
                        params.height = BaseApplication.getInstance().getHightPixels - ConvertUtils.dp2pxValue(125F, activity!!)
                        Log.e("result", "执行这里1" + flag)
                    } else {
                        Log.e("result", "执行这里2" + flag)
                        if (getNavigationBarHeight(activity!!) > values) {
                            params.height = getScreenHeightPx() - height - mPartyDetailTabLayout.height + 1 - values    //适配全面屏
                        } else {
                            params.height = getScreenHeightPx() - height - mPartyDetailTabLayout.height + 1 - getNavigationBarHeight(activity!!)     //适配全面屏
                        }

                    }
                }
            } else {
                Log.e("result", "执行这里3")
                params.height = getScreenHeightPx() - height - mPartyDetailTabLayout.height + 1
            }
            mPartyDetailViewPager.layoutParams = params
        }
        detail_refreshLayout.setOnRefreshListener {
            viewModel?.initData()
            it.finishRefresh(10000)
        }

        val color = ContextCompat.getColor(activity!!, R.color.white) and 0x00ffffff
        detail_refreshLayout.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight)
                mOffset = offset / 2
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

            Log.e("result", "当前H值" + h + "最小高度" + ConvertUtils.px2dp(912F))
            var location = IntArray(2)
            mPartyDetailTabLayout.getLocationOnScreen(location)
            var yPosition = location[1];
            if (h == 0) {
                h = detail_ivHeader.height - toolBarPositionY
            }
//            Log.e("result", "yPosition" + yPosition)  //2314
//            Log.e("result", "toolBarPositionY" + toolBarPositionY)
            if (yPosition < toolBarPositionY) {
//                mPartyDetailMagicTabLayout.visibility = View.VISIBLE
//                mPartyDetailSubjectTabLayout.visibility = View.INVISIBLE
                nest.setNeedScroll(false)
            } else {
//                mPartyDetailMagicTabLayout.visibility = View.GONE
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
                detail_buttonBarLayout.setAlpha(1f * mScrollY / h);
                detail_toolbar.setBackgroundColor(255 * mScrollY / h shl 24 or color);
                detail_ivHeader.translationY = (mOffset - mScrollY).toFloat()
            }

            if (scrollY == 0) {
                detail_iv_back.setImageResource(R.drawable.arrow_white);
                ivPartyDetailTrans.setImageResource(R.drawable.more_white)
            } else {
                ivPartyDetailTrans.setImageResource(R.drawable.more_black)
                detail_iv_back.setImageResource(R.drawable.arrow_black);
            }
            lastScrollY = y
        })
        detail_buttonBarLayout.setAlpha(0F)
        detail_toolbar.setBackgroundColor(0)
        viewModel?.inject(this)
    }

//    fun returnBack() {
//        Log.e("party", "$navigation")
//        if (navigation == 0) {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME)
//                    .navigation(this@PartyDetailActivty, object : NavCallback() {
//                        override fun onArrival(postcard: Postcard?) {
//                            finish()
//                        }
//                    })
//        } else if (navigation == 1) {
//            if (mViewModel?.destroyList!!.contains("SubjectPartyActivity")) {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.SUBJECT_PARTY).withInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, 1)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(location!!.latitude, location!!.longitude)).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyDetailActivty, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
//            } else {
//                finish()
//            }
//        } else if (navigation == 2) {
//            if (mViewModel?.destroyList!!.contains("MyRestoreActivity")) {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withString(RouterUtils.PartyConfig.PARTY_CITY, city).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this@PartyDetailActivty, object : NavCallback() {
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
//                        location).withString(RouterUtils.PartyConfig.PARTY_CITY, city).navigation(this@PartyDetailActivty, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                finish()
//            }
//        }
//    }

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