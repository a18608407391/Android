package org.cs.tec.library.http

import android.content.Context
import android.widget.Toast

import org.cs.tec.library.Utils.KLog
import org.cs.tec.library.Utils.ToastUtils

import io.reactivex.observers.DisposableObserver

/**
 * Created by goldze on 2017/5/10.
 * 该类仅供参考，实际业务Code, 根据需求来定义，
 */
abstract class BaseSubscriber<T>(private val context: Context) : DisposableObserver<T>() {
    private val isNeedCahe: Boolean = false
    abstract fun onResult(t: T)

    override fun onError(e: Throwable) {
        KLog.e(e.message!!)
        // todo error somthing

        if (e is ResponseThrowable) {
            onError(e)
        } else {
            onError(ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN))
        }
    }


    public override fun onStart() {
        super.onStart()

        Toast.makeText(context, "http is start", Toast.LENGTH_SHORT).show()
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "无网络，读取缓存数据", Toast.LENGTH_SHORT).show()
            onComplete()
        }

    }

    override fun onComplete() {

        Toast.makeText(context, "http is Complete", Toast.LENGTH_SHORT).show()
        // todo some common as  dismiss loadding
    }


    abstract fun onError(e: ResponseThrowable)

    override fun onNext(t: T) {
        val baseResponse = t as BaseResponse<*>
        if (baseResponse.code == 200) {
            onResult(baseResponse.result as T)
        } else if (baseResponse.code == 330) {
            ToastUtils.showShort(baseResponse.message!!)
        } else if (baseResponse.code == 503) {
            KLog.e(baseResponse.message!!)
        } else {
            ToastUtils.showShort("操作失败！错误代码:" + baseResponse.code)
        }
    }
}
