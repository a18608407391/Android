package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.ChangeNickNameViewModel
import com.example.private_module.databinding.ActivityChangeNicknameBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.PrivateModuleConfig.CHANGENICKNAME)
class ChangeNickNameActivity : BaseFragment<ActivityChangeNicknameBinding, ChangeNickNameViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_change_nickname
    }

    @Autowired(name = RouterUtils.PrivateModuleConfig.NICKNAME)
    @JvmField
    var nickname: String? = null

    override fun initVariableId(): Int {
        return BR.change_nickname
    }
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_change_nickname
//    }

//    override fun initViewModel(): ChangeNickNameViewModel? {
//        return ViewModelProviders.of(this)[ChangeNickNameViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onBackPressedSupport(): Boolean {
        hideSoftInput()
        return super.onBackPressedSupport()
    }
}