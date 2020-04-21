package com.cstec.administrator.social.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.GetLikeViewModel
import com.cstec.administrator.social.databinding.ActivityReceivelikeBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_my_dynamics.*
import kotlinx.android.synthetic.main.activity_receivelike.*


@Route(path = RouterUtils.SocialConfig.SOCIAL_GET_LIKE)
class GetLikeActivity : BaseFragment<ActivityReceivelikeBinding, GetLikeViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_receivelike
    }


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_MEMBER_ID)
    @JvmField
    var id: String? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var type: Int = 0


    override fun initVariableId(): Int {
        return BR.get_like_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_receivelike
//    }

//    override fun initViewModel(): GetLikeViewModel? {
//        return ViewModelProviders.of(this)[GetLikeViewModel::class.java]
//    }


    lateinit var swip :PoketSwipeRefreshLayout
    override fun initData() {
        super.initData()
        swip = binding!!.root!!.findViewById(R.id.getlike_swipe)
        Utils.setStatusTextColor(true, activity)
        id = arguments?.getString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID)
        location = arguments?.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        getlike_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        getlike_swipe.setOnRefreshListener(viewModel!!)
        viewModel?.inject(this)
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        doback()
//    }

    fun doback() {
//        if (type == 1) {
//            if (mViewModel?.destroyList!!.contains("DriverHomeActivity")) {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location)
//                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, id)
//                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation(this, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
//            } else {
//                finish()
//            }
//        } else if (type == 8) {
////            if (mViewModel?.destroyList!!.contains("MsgActivity")) {
////                ARouter.getInstance().build(RouterUtils.Chat_Module.MSG_AC)
////                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this, object : NavCallback() {
////                            override fun onArrival(postcard: Postcard?) {
////                                finish()
////                            }
////                        })
////            } else {
//                finish()
////            }
//        }
    }
}