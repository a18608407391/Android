package com.elder.zcommonmodule.Http

import android.app.Activity
import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


open class BaseObserver<T> : Observer<T> {
    var activty: Activity?

    constructor(activity: Activity?) {
        this.activty = activity
    }

    override fun onComplete() {
        Log.e("result","onComplete")
    }

    override fun onSubscribe(d: Disposable) {
        Log.e("result","onSubscribe")
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
        Log.e("result","onError"+e.message)
    }
}
