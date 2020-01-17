package com.zk.library.Base

import android.support.annotation.NonNull


open class ItemViewModel<VM : BaseViewModel> {
    lateinit var viewModel: VM

    fun ItemViewModel(@NonNull viewModel: VM) {
        this.viewModel = viewModel
    }

    open fun clearData() {

    }

  open  fun initDatas(t:Int) {
    }
}