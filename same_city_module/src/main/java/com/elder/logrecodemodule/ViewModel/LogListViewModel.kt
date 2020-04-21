package com.elder.logrecodemodule.ViewModel

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.logrecodemodule.Activity.LogListActivity
import com.elder.logrecodemodule.Activity.LogShareActivity
import com.elder.logrecodemodule.BR
import com.elder.zcommonmodule.Utils.Douglas
import com.elder.logrecodemodule.Entity.LogData
import com.elder.logrecodemodule.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Entity.DriverInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.LogInfo
import com.elder.zcommonmodule.Entity.UIdeviceInfo
import com.elder.zcommonmodule.Entity.UpDataDriverEntitiy
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.FileUtils
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.getDriverImageUrl
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loglist.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import org.cs.tec.library.http.NetworkUtil
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class LogListViewModel : BaseViewModel() {
    var totalDistance = ObservableField<String>("0")

    var totalTime = ObservableField<String>("00:00")

    var totalCount = ObservableField<String>("0")

    var calendar = Calendar.getInstance()

    var isEmpty = ObservableField<Boolean>(false)

    var years = ObservableField<String>()

    var dataFormat = ObservableArrayList<LogData>().apply {
        for (i in 0..11) {
            var m = ""
            when (i) {
                0 -> {
                    m = "JAN"
                }
                1 -> {
                    m = "FEB"
                }
                2 -> {
                    m = "MAR"
                }
                3 -> {
                    m = "APR"
                }
                4 -> {
                    m = "MAY"
                }
                5 -> {
                    m = "JUN"
                }
                6 -> {
                    m = "JUL"
                }
                7 -> {
                    m = "AUG"
                }
                8 -> {
                    m = "SEPT"
                }
                9 -> {
                    m = "OCT"
                }
                10 -> {
                    m = "NOV"
                }
                11 -> {
                    m = "DEC"
                }
            }
            var data = LogData()
            data.month.set((i + 1).toString())
            data.BigMoth.set(m)
            this.add(data)
        }
    }

    fun addChildView(layout: LinearLayout?) {
        dataFormat.forEachIndexed { i, entity ->
            var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.hori_item, layout, false)
            if (entity.isChecked.get()!!) {
                lastView = entity
            }
            binding.setVariable(BR.hori_item_data, entity)
            binding.setVariable(BR.log_hor_model, this@LogListViewModel)
            layout?.addView(binding.root)
            layout?.invalidate()
        }
        CoroutineScope(uiContext).launch {
            delay(500)
//            logRecodeFragment.hori_scroll.smoothScrollTo(500, 0)
            logRecodeFragment.autoScroll(dataFormat.indexOf(lastView))
        }
    }

    var currentMonth = 0
    var lastView: LogData? = null
    fun onClick(view: LogData) {
        if (!view.isChecked.get()!!) {
            lastView?.isChecked?.set(false)
            view.isChecked.set(true)
            lastView = view
            currentMonth = Integer.valueOf(view.month.get())
            initDatas(Integer.valueOf(view.month.get()))
        }
    }

    lateinit var logRecodeFragment: LogListActivity
    fun inject(logRecodeFragment: LogListActivity) {
        this.logRecodeFragment = logRecodeFragment
        var month = calendar.get(Calendar.MONTH)
        var m = 0
        if (month - logRecodeFragment.type < 0) {
            m = month - logRecodeFragment.type + 12
            years.set((calendar.get(Calendar.YEAR) - 1).toString())
        } else {
            years.set(calendar.get(Calendar.YEAR).toString())
            m = month - logRecodeFragment.type
        }
        var data = dataFormat[m]
        data.isChecked.set(true)
        logRecodeFragment.hori_scroll
        dataFormat[m] = data
        addChildView(logRecodeFragment.log_hori)

//        initDatas(calendar.get(Calendar.MONTH) + 1 - logRecodeFragment.type)
        initDatas(m + 1)
    }

    fun initDatas(month: Int) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }
        items.clear()
        logRecodeFragment!!.showProgressDialog(getString(R.string.loading_droverlog))
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        var mtv = if (month < 10) "0" + month else month.toString()
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["queryDate"] = "${years.get()}-$mtv"
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiRiding/ridingManage/querySingRidingDetailInfo").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.e("result", it)
            var s = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
            if (s.code == 0 && s.data != null) {
                var info = Gson().fromJson<LogInfo>(Gson().toJson(s.data), LogInfo::class.java)
                if (info.result != null && info.result?.size != 0) {
                    totalDistance.set(DecimalFormat("0.0").format(info.resultDto?.allTotalDis?.toDouble()!! / 1000))
                    totalTime.set(ConvertUtils.formatTimeS(info.resultDto?.allTotalTime!!))
                    totalCount.set(info.result?.size.toString())
                    info.result?.forEach {
                        items.add(converToUiDriver(it))
                    }
                    isEmpty.set(false)
                    if (info.resultDto?.allTotalDis == 0.0) {
                        totalDistance.set("0")
                        totalTime.set("00:00")
                        totalCount.set("0")
                    }
                } else {
                    isEmpty.set(true)
                    totalDistance.set("0")
                    totalTime.set("00:00")
                    totalCount.set("0")
                }
            } else {
                Toast.makeText(context, s.msg, Toast.LENGTH_SHORT).show()
                totalDistance.set("0")
                totalTime.set("00:00")
                totalCount.set("0")
            }
            logRecodeFragment.dismissProgressDialog()
        }
    }

    fun ItemClick(item: UIdeviceInfo) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
            return
        }
        var localFile = FileUtils.getStorageFile(item.id.get()!!)
        if (localFile == null || localFile.isEmpty()) {
            logRecodeFragment!!.showProgressDialog(getString(R.string.loading_droverlog))
            var token = PreferenceUtils.getString(context, USER_TOKEN)
            Observable.create(ObservableOnSubscribe<Response> {
                var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                var map = HashMap<String, String>()
                var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiRiding/ridingManage/ridingFileDown?fileUrl=" + item.fileUrl!!.get()).build()
                var call = client.newCall(request)
                var response = call.execute()
                it.onNext(response)
            }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                return@Function it.body()?.string()
            }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                Log.e("result", it)
                var body = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                if (body?.msg == null) {
                    if (it != null && !it.isEmpty()) {
                        var da = Gson().fromJson<UpDataDriverEntitiy>(it, UpDataDriverEntitiy::class.java)
                        Observable.just(da).map(Function<UpDataDriverEntitiy, UpDataDriverEntitiy> {
                            if (it.locationArray?.size!! > 3000) {
                                var da = Douglas(it.locationArray, 10.0)
                                var mes = da.compress()
                                it.locationArray!!.clear()
                                it.locationArray!!.addAll(mes)
                            }
                            FileUtils.writeTxtToFile(Gson().toJson(it), Environment.getExternalStorageDirectory().getPath() + "/Amoski", "/UpLoad_location_File" + item.id.get() + ".txt")
                            return@Function it
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
                            logRecodeFragment.dismissProgressDialog()
                            if (it != null) {
                                logRecodeFragment.start((ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SHARE).navigation() as LogShareActivity).setValue(item))
//                                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SHARE).withSerializable(RouterUtils.LogRecodeConfig.SHARE_ENTITY, item).navigation()
                            } else {
                                Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    logRecodeFragment.dismissProgressDialog()
                    Toast.makeText(context, body.msg, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            logRecodeFragment.start((ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SHARE).navigation() as LogShareActivity).setValue(item))
//            ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SHARE).withSerializable(RouterUtils.LogRecodeConfig.SHARE_ENTITY, item).navigation()
        }
    }

    var adapter = BindingRecyclerViewAdapter<UIdeviceInfo>()
    var items = ObservableArrayList<UIdeviceInfo>()
    var itembinding = ItemBinding.of<UIdeviceInfo>(BR.log_driver_item, R.layout.log_list_item).bindExtra(BR.log_activity_model, this@LogListViewModel)
    val viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> LogViewHolder(binding.root) }

    private class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun converToUiDriver(info: DriverInfo): UIdeviceInfo {
        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var d = Date(info.createTime)
        var u = ""
        if (info.ridingEndBackgroudImg != null) {
            u = info.ridingEndBackgroudImg!!
        }
        return UIdeviceInfo(ObservableField(simple.format(d)), ObservableField(getDriverImageUrl(info.trackImgUrl, info.baseUrl)), ObservableField(DecimalFormat("0.0").format(info?.totalDistance?.toDouble()!! / 1000)), ObservableField(ConvertUtils.formatTimeS(info?.totalTime!!)), ObservableField(DecimalFormat("0.0").format((info?.totalDistance!! * 3.6 / info!!.totalTime!!))), ObservableField(info.ridingFileUrl!!), ObservableField(u), ObservableField(info.id.toString()), ObservableField(info.baseUrl!!))
    }

    fun onImgClick(view: View) {

        logRecodeFragment._mActivity!!.onBackPressedSupport()
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
//        finish()
    }


    fun onClick(view: View) {
        var t = DialogUtils.showYearDialog(logRecodeFragment.activity!!, command)
        t!!.setSelectedYear(Integer.valueOf(years.get()), true)
    }

    var command = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            initDatas(currentMonth)
            years.set(t)
        }
    })
}