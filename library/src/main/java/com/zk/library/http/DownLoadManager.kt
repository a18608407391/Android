package org.cs.tec.library.http

import android.databinding.ObservableBoolean
import android.os.Environment
import android.util.Log
import com.zk.library.Utils.GsonUtil
import com.zk.library.Utils.GsonUtils

import org.cs.tec.library.http.download.DownLoadSubscriber
import org.cs.tec.library.http.download.ProgressCallBack
import org.cs.tec.library.http.interceptor.ProgressInterceptor

import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.cs.tec.library.Utils.ConvertUtils
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.*

/**
 * Created by goldze on 2017/5/11.
 * 文件下载管理，封装一行代码实现下载
 */

class DownLoadManager private constructor() {

    init {
        buildNetWork()
    }

    //下载
    fun load(downUrl: String, callBack: ProgressCallBack<File>) {
        retrofit!!.create(ApiService::class.java)
                .download(downUrl)
                .subscribeOn(Schedulers.io())//请求网络 在调度者的io线程
                .observeOn(Schedulers.io()) //指定线程保存文件
                .map(Function<ResponseBody, File> {
                    return@Function callBack.saveFile(it)
                })
                .observeOn(AndroidSchedulers.mainThread()) //在主线程中更新ui
                .subscribe(DownLoadSubscriber(callBack))
    }


    fun load(downUrl: String, call: Observer<File>) {
        retrofit!!.create(ApiService::class.java)
                .download(downUrl).subscribeOn(Schedulers.io()).map(Function<ResponseBody, File> {
                    return@Function GsonUtil.getFile(it.bytes(), Environment.getExternalStorageDirectory().getPath() + "/Amoski", System.currentTimeMillis().toString() + "img" + ".zip")
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(call)
    }

    fun saveFile(body: ResponseBody, destFileDir: String, destFileName: String): File {
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

    private fun buildNetWork() {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(ProgressInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()

        retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(NetworkUtil.url)
                .build()
    }

    private interface ApiService {
        @Streaming
        @GET
        fun download(@Url url: String): Observable<ResponseBody>
    }

    companion object {
        private var instan: DownLoadManager? = null

        private var retrofit: Retrofit? = null

        /**
         * 单例模式
         *
         * @return DownLoadManager
         */
        fun getInstance(): DownLoadManager {
            if (instan == null) {
                instan = DownLoadManager()
            }
            return instan!!
        }
    }
}
