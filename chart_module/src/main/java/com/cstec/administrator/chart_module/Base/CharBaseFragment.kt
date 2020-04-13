package com.cstec.administrator.chart_module.Base

import android.databinding.ViewDataBinding
import android.util.Log
import cn.jpush.im.android.api.JMessageClient
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel


abstract class CharBaseFragment<V : ViewDataBinding, VM : BaseViewModel> : BaseFragment<V, VM>() {

    override fun initData() {
        super.initData()
        JMessageClient.registerEventReceiver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        JMessageClient.unRegisterEventReceiver(this)
    }
}