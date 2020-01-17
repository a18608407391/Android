package com.zk.library.Base

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity


class ViewModelFactory : ViewModelProvider.NewInstanceFactory{


    val mApplication : BaseApplication

    companion object {
        val INSTANCE: ViewModelFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ViewModelFactory(BaseApplication.getInstance())
        }
    }

    constructor(application: BaseApplication){
        this.mApplication = application
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BaseViewModel() as T
    }

    fun <T : ViewModel> createViewModel(activity: FragmentActivity, cls: Class<T>): T {
        return ViewModelProviders.of(activity).get(cls)
    }
}