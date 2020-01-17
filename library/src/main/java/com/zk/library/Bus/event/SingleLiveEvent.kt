package com.zk.library.Bus.event

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean


open class SingleLiveEvent<T> : MutableLiveData<T>() {


    companion object {
        var TAG: String = javaClass.name
        var mPending: AtomicBoolean = AtomicBoolean(false)
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {

        if (hasActiveObservers()) {
            Log.e(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        super.observe(owner, Observer<T> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }

    @MainThread
    fun call() {
        this.value = null
    }

    @WorkerThread
    fun callNomal() {

    }

}