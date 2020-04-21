package com.elder.zcommonmodule.Component

import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import com.elder.zcommonmodule.Widget.LoginUtils.BaseDialogFragment
import com.elder.zcommonmodule.Widget.LoginUtils.FragmentDialogController
import com.elder.zcommonmodule.Widget.LoginUtils.LoginDialogFragment
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Bus.event.RxBusEven
import io.reactivex.disposables.Disposable
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


open class ItemViewModel<VM : BaseViewModel>() : BaseDialogFragment.DismissListener {
    lateinit var viewModel: VM
    var disposable: Disposable? = null
    lateinit var dialogFragment: LoginDialogFragment

    open fun ItemViewModel(@NonNull viewModel: VM): ItemViewModel<VM> {
        this.viewModel = viewModel
        disposable = RxBus.default?.toObservable(RxBusEven::class.java)?.subscribe {
            doRxEven(it)
        }
        RxSubscriptions.add(disposable)
        return this
    }

    open fun doRxEven(it: RxBusEven?) {

    }

    open fun clearData() {

    }

    open fun initDatas(t: Int) {
    }


    fun showLoginDialogFragment(fr: Fragment) {
        dialogFragment = FragmentDialogController(fr).show(LoginDialogFragment()) as LoginDialogFragment
        dialogFragment!!.functionDismiss = this
    }


    fun destroy() {
        RxSubscriptions.remove(disposable)
    }

    override fun onDismiss(fr: BaseDialogFragment,value:Any) {

    }

    fun startMinaService() {
        var pos = ServiceEven()
        pos.type = "splashContinue"
        RxBus.default?.post(pos)
    }

    fun closeMina() {
        BaseApplication.isClose = true
        var pos = ServiceEven()
        pos.type = "MinaServiceCancle"
        RxBus.default?.post(pos)
    }
}