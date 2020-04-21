package com.example.private_module.Activity.NewVession

import android.graphics.Color
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.NewVession.MyRestoreViewModel
import com.example.private_module.databinding.ActivityMyRestoreBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.PrivateModuleConfig.MY_RESTORE_AC)
class MyRestoreActivity : BaseFragment<ActivityMyRestoreBinding, MyRestoreViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_my_restore
    }

    override fun initVariableId(): Int {
        return BR.my_restore_model
    }
//
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_my_restore
//    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    //    override fun initViewModel(): MyRestoreViewModel? {
//        return ViewModelProviders.of(this)[MyRestoreViewModel::class.java]
//    }
    var swp: PoketSwipeRefreshLayout? = null

    override fun initData() {
        swp = binding!!.root!!.findViewById(R.id.restore_swipe)
        location = arguments!!.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location
        swp!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swp!!.setOnRefreshListener(viewModel!!)
        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ActiveWebGotoApp") {
                _mActivity!!.onBackPressedSupport()
            }
        }
        RxSubscriptions.add(s)
        viewModel?.inject(this)
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
//            override fun onArrival(postcard: Postcard?) {
//                finish()
//            }
//        })
//    }
}