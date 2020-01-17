package org.cs.tec.library.http.download

import android.util.Log

import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import okhttp3.ResponseBody

/**
 * Created by goldze on 2017/9/26 0026.
 */

abstract class ProgressCallBack<T>(private val destFileDir: String // 本地文件存放路径
                                   , private val destFileName: String // 文件名
) {
    private var mSubscription: Disposable? = null

    init {
        subscribeLoadProgress()
    }

    abstract fun onSuccess(t: T)

    abstract fun progress(progress: Long, total: Long)

    fun onStart() {}

    fun onCompleted() {}

    abstract fun onError(e: Throwable)

    fun saveFile(body: ResponseBody): File {
        var `is`: InputStream? = null
        val buf = ByteArray(2048)
        var len: Int = 0
        var fos: FileOutputStream? = null
        try {
            `is` = body.byteStream()
            val dir = File(destFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, destFileName)
            fos = FileOutputStream(file)
            `is`.use {
                while (`is`!!.read().also { it -> len = it } != -1) {
                    fos.write(buf, 0, len)
                }
            }
            fos.flush()
            return file
            unsubscribe()
            //onCompleted();
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                fos?.close()
            } catch (e: IOException) {
                Log.e("saveFile", e.message)
            }
        }
        return null!!
    }

    /**
     * 订阅加载的进度条
     */
    fun subscribeLoadProgress() {
        mSubscription = RxBus.default!!.toObservable(DownLoadStateBean::class.java)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe { progressLoadBean -> progress(progressLoadBean.bytesLoaded, progressLoadBean.total) }
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription)
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    fun unsubscribe() {
        RxSubscriptions.remove(mSubscription)
    }
}