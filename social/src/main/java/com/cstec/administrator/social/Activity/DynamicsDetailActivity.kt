package com.cstec.administrator.social.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewAdapter.CommentExpandableListView
import com.cstec.administrator.social.ViewModel.DynamicsDetailViewModel
import com.cstec.administrator.social.databinding.ActivityDynamicsdetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.SocialConfig.SOCIAL_DETAIL)
class DynamicsDetailActivity : BaseFragment<ActivityDynamicsdetailBinding, DynamicsDetailViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_dynamicsdetail
    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY)
    @JvmField
    var detail: DynamicsCategoryEntity.Dynamics? = null

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var navigationType: Int = 0

    //1 为获得的赞

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


//    override fun doPressBack() {
//        super.doPressBack()
//        mViewModel?.toNav()
//    }

    override fun initVariableId(): Int {
        return BR.dynamics_detail_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        return R.layout.activity_dynamicsdetail
//    }


//    override fun initViewModel(): DynamicsDetailViewModel? {
//        return ViewModelProviders.of(this)[DynamicsDetailViewModel::class.java]
//    }


    var mCommentList : CommentExpandableListView ? = null
    var mLinear :LinearLayout ? = null
    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(true, this.activity)
        mCommentList =  binding!!.root.findViewById(R.id.commonlist)
        mLinear =  binding!!.root.findViewById(R.id.social_linear)
        detail = arguments!!.getSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY) as DynamicsCategoryEntity.Dynamics?
        location = arguments!!.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location
        viewModel?.inject(this)
    }


}