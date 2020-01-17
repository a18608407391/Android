package org.cs.tec.library.Utils

import android.content.Context
import android.support.v4.app.Fragment

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer

import org.cs.tec.library.http.BaseResponse
import org.cs.tec.library.http.ExceptionHandle

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * Created by goldze on 2017/6/19.
 * 有关Rx的工具类
 */
class RxUtils {
    /**
     * 生命周期绑定
     *
     * @param lifecycle Activity
     */

companion object {
        fun <T> bindToLifecycle(@NonNull lifecycle: Context): LifecycleTransformer<T> {
            return if (lifecycle is LifecycleProvider<*>) {
                (lifecycle as LifecycleProvider<*>).bindToLifecycle()
            } else {
                throw IllegalArgumentException("context not the LifecycleProvider type")
            }
        }

        /**
         * 生命周期绑定
         *
         * @param lifecycle Fragment
         */
        fun bindToLifecycle(@NonNull lifecycle: Fragment): LifecycleTransformer<*> {
            return if (lifecycle is LifecycleProvider<*>) {
                return (lifecycle as LifecycleProvider<*>).bindToLifecycle<Any>()
            } else {
                throw IllegalArgumentException("fragment not the LifecycleProvider type")
            }
        }

        /**
         * 生命周期绑定
         *
         * @param lifecycle Fragment
         */
        fun bindToLifecycle(@NonNull lifecycle: LifecycleProvider<*>): LifecycleTransformer<*> {
            return lifecycle.bindToLifecycle<Any>()
        }

        /**
         * 线程调度器
         */
        fun schedulersTransformer(): ObservableTransformer<*, *> {
            return ObservableTransformer<Any, Any> { upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
            }
        }

        fun exceptionTransformer(): ObservableTransformer<*, *> {
            return ObservableTransformer<Any, Any> {
                it.onErrorResumeNext(HttpResponseFunc())
            }
        }


        private class HttpResponseFunc<T> : Function<Throwable, Observable<T>> {
            override fun apply(t: Throwable): Observable<T> {
                return Observable.error(ExceptionHandle.handleException(t))
            }
        }

        private class HandleFuc<T> : Function<BaseResponse<T>, T> {
            override fun apply(response: BaseResponse<T>): T {
                if (!response.isOk)
                    throw RuntimeException(if ("" != response.code.toString() + "" + response.message) response.message else "")
                return response.result!!
            }
        }
}


}
