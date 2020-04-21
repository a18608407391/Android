package com.cstec.administrator.social.Fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.ItemViewModel.SocialItemModel
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.SocialViewModel
import com.cstec.administrator.social.databinding.FragmentSocialBinding
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.RELEASE_RESULT
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_social.*


@Route(path = RouterUtils.SocialConfig.SOCIAL_MAIN)
class SocialFragment : BaseFragment<FragmentSocialBinding, SocialViewModel>() {

    override fun initContentView(): Int {
        return R.layout.fragment_social
    }

    override fun initVariableId(): Int {
        return BR.social_model
    }


    var SocialViewPger: ViewPager? = null
    var SocialTabLayout: TabLayout? = null
    var SocialSwipe: SmartRefreshLayout? = null
    override fun initData() {
        super.initData()
        SocialViewPger = binding!!.root!!.findViewById(R.id.social_viewpager)
        SocialTabLayout = binding!!.root!!.findViewById(R.id.mTabLayout)
        SocialSwipe = binding!!.root!!.findViewById(R.id.social_swipe)
        SocialViewPger!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(SocialTabLayout))
        SocialTabLayout!!.setupWithViewPager(SocialViewPger)
        social_viewpager.currentItem = 0
        if (this.isAdded) {
            SocialSwipe!!.setOnRefreshListener(viewModel)
            SocialTabLayout!!.addOnTabSelectedListener(viewModel!!)
        }
        viewModel?.inject(this)
    }

    fun initSecond() {
        if (viewModel?.first!!) {
            social_viewpager.currentItem = 1
            viewModel?.first = false
        }
    }

    fun initResult(data: Intent) {

    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        when (requestCode) {
            RELEASE_RESULT -> {
                if (data != null) {
                    var flag = data!!.getBoolean("ResultReleaseDynamicsSuccess")
                    if (flag) {
                        if (isAdded) {
                            SocialViewPger!!.currentItem = 0
                            var itemmodel = viewModel?.items!![0] as SocialItemModel
                            itemmodel.page = 20
                            itemmodel.pageSize = 1
                            itemmodel.initDatas(0)
                            SocialSwipe?.finishRefresh(10000)
                        }
                    }
                }
            }
            DETAIL_RESULT -> {
                Log.e("result","执行数据更新了！！！！！")
                if (data != null) {
                    var enttiy = data.getSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY) as DynamicsCategoryEntity.Dynamics
                    if (enttiy != null) {
                        Log.e("result","数据更新了！！！！！")


                        var its = viewModel?.items!![viewModel?.curItem!!] as SocialItemModel
                        its.items.forEachIndexed { index, dynamics ->
                            if (dynamics.id == enttiy.id) {
                                its.items[index] = enttiy
                            } else if (dynamics.memberId == enttiy.memberId && dynamics.followed != enttiy.followed) {
                                dynamics.followed = enttiy.followed
                                its.items.set(index, dynamics)
                            }
                        }
                        its.adapter.initDatas(its.items)
                    }
                }
            }
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