package com.zk.library.Base

import android.app.Activity
import android.app.ProgressDialog
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.afollestad.materialdialogs.util.DialogUtils
import com.trello.rxlifecycle2.LifecycleProvider
import com.zk.library.Bus.event.SingleLiveEvent
import com.zk.library.Base.ContainerActivity.Companion.BUNDLE
import com.zk.library.Bus.event.ActivityDestroyEven
import com.zk.library.R
import com.zk.library.Utils.CANONICAL_NAME
import com.zk.library.Utils.CLASS
import io.reactivex.disposables.Disposable
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions


open class BaseViewModel : ViewModel(), IBaseViewModel {
    override fun onDestroy() {

    }

    lateinit var lifecycle: LifecycleProvider<*>
    var uc: UIChangeLiveData? = null

    fun injectLifecycleProvider(lifecycleProvider: LifecycleProvider<*>) {
        this.lifecycle = lifecycleProvider
    }

    fun getLifecycleProvide(): LifecycleProvider<*> {
        return lifecycle
    }

    fun getUC(): UIChangeLiveData {
        if (uc == null) {
            uc = UIChangeLiveData()
        }
        return uc!!
    }


    fun showDialig() {
        showDialog("请稍后")
    }


    fun showDialog(title: String) {
        uc!!.showDialogEvent!!.postValue(title)
    }

    fun dismissDialog() {
        uc!!.dismissDialogEvent!!.call()
    }

    fun startActivity(clz: Class<*>) {
        startActivity(clz, null)
    }

    fun startContainerActivity(canonicalName: String) {
        startContainerActivity(canonicalName, null)
    }

    fun finish() {
        uc!!.finishEvent!!.call()
    }

    open fun onBackPressed() {
        uc!!.onBackPressedEvent!!.call()
    }


    fun startContainerActivity(canonicalName: String, bundle: Bundle?) {
        val params = HashMap<String, Any>()
        params.put(CANONICAL_NAME, canonicalName)
        if (bundle != null) {
            params.put(BUNDLE, bundle)
        }
        uc!!.startContainerActivityEvent!!.postValue(params)
    }

    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        var params = HashMap<String, Any>()
        params[CLASS] = clz
        if (bundle != null) {
            params[BUNDLE] = bundle
        }
        uc!!.startActivityEvent!!.postValue(params)
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    var destroyList = ArrayList<String>()
    var destroyEven: Disposable? = null
    override fun registerRxBus() {
        destroyEven = RxBus.default?.toObservable(ActivityDestroyEven::class.java)?.subscribe {
            destroyList.add(it.name!!)
        }
        RxSubscriptions.add(destroyEven)
    }

    override fun removeRxBus() {
        RxSubscriptions.remove(destroyEven)
    }


    class UIChangeLiveData : SingleLiveEvent<Any>() {
        var showDialogEvent: SingleLiveEvent<String>? = null
        var dismissDialogEvent: SingleLiveEvent<Void>? = null
        var startActivityEvent: SingleLiveEvent<Map<String, Any>>? = null
        var startContainerActivityEvent: SingleLiveEvent<Map<String, Any>>? = null
        var finishEvent: SingleLiveEvent<Void>? = null
        var onBackPressedEvent: SingleLiveEvent<Void>? = null


        fun getShowDialogEven(): SingleLiveEvent<String> {
            if (showDialogEvent == null) {
                showDialogEvent = SingleLiveEvent()
            }
            return showDialogEvent!!
        }

        fun getDismisDialogEven(): SingleLiveEvent<Void> {
            if (dismissDialogEvent == null) {
                dismissDialogEvent = SingleLiveEvent()
            }
            return dismissDialogEvent!!
        }

        fun getStartActivityEven(): SingleLiveEvent<Map<String, Any>> {
            if (startActivityEvent == null) {
                startActivityEvent = SingleLiveEvent()
            }
            return startActivityEvent!!
        }

        fun getStartContainerEvent(): SingleLiveEvent<Map<String, Any>> {
            if (startContainerActivityEvent == null) {
                startContainerActivityEvent = SingleLiveEvent()
            }
            return startContainerActivityEvent!!
        }

        fun getFinishEven(): SingleLiveEvent<Void> {
            if (finishEvent == null) {
                finishEvent = SingleLiveEvent()
            }
            return finishEvent!!
        }

        fun getonBackPressEvent(): SingleLiveEvent<Void> {
            if (onBackPressedEvent == null) {
                onBackPressedEvent = SingleLiveEvent()
            }
            return onBackPressedEvent!!
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<Any>) {
            super.observe(owner, observer)
        }
    }

}