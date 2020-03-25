package com.cstec.administrator.social.Fragment

import android.content.Intent
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearSnapHelper
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.SocialViewModel
import com.cstec.administrator.social.databinding.FragmentSocialBinding
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


    override fun initData() {
        super.initData()
        social_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout.setupWithViewPager(social_viewpager)
        social_viewpager.currentItem = 0
        if (this.isAdded) {
            social_swipe.setOnRefreshListener(viewModel)
//            social_swipe.setColorSchemeColors(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT)
//            social_swipe.setOnRefreshListener(viewModel!!)
            mTabLayout.addOnTabSelectedListener(viewModel!!)
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
}