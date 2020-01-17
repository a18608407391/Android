package com.elder.logrecodemodule.ViewModel

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.service.autofill.Dataset
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.elder.logrecodemodule.Activity.LogShareActivity
import com.elder.logrecodemodule.Entity.ShareIoData
import com.elder.logrecodemodule.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.Even.NomalPostStickyEven
import com.elder.zcommonmodule.Utils.FileUtils
import com.elder.zcommonmodule.Widget.Chart.SuitLines
import com.elder.zcommonmodule.Widget.Chart.Unit
import com.elder.zcommonmodule.getDriverImageUrl
import com.elder.zcommonmodule.getImageUrl
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_log_share.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.USER_ENTITY
import org.cs.tec.library.Utils.ConvertUtils
import org.jetbrains.anko.custom.async
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LogShareViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    lateinit var user: UserInfo
    var bottomVisible = ObservableField<Boolean>(true)

    var component = TitleComponent()
    lateinit var activity: LogShareActivity
    fun inject(logShareActivity: LogShareActivity) {
        this.activity = logShareActivity
        component.title.set(getString(R.string.log_detail))
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.callback = this
        if (logShareActivity.imgs == null) {
            Log.e("result", "值没传过来")
        }
        var id = PreferenceUtils.getString(context, USERID)
        user = queryUserInfo(id)[0]
        Observable.just("").subscribeOn(Schedulers.io()).map(Function<String, UpDataDriverEntitiy> {
            var localFile = FileUtils.getStorageFile(logShareActivity.imgs?.id!!.get()!!)
            var Dataset = Gson().fromJson<UpDataDriverEntitiy>(localFile, UpDataDriverEntitiy::class.java)
            var datas = ArrayList<Location>()
            Dataset.locationArray?.forEach {
                hight.add(it.height)
                datas.add(it)
            }
            return@Function Dataset
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Observable.just(it).subscribeOn(Schedulers.io()).map(Function<UpDataDriverEntitiy, ShareIoData> {
                Log.e("result", Gson().toJson(it))
                var data = ShareIoData()
                data.distance = DecimalFormat("0.0").format(it.mileage!! / 1000)
                data.time = ConvertUtils.formatTimeS(it.totalTime?.toInt()?.toLong()!!)
                data.c = DecimalFormat("0.0").format(it.topspeed)
                data.d = DecimalFormat("0.0").format(it.highestPoint)
                data.e = DecimalFormat("0.0").format((it.mileage!! * 3.6 / it.totalTime))
                Log.e("result", data.distance + "距离" + data.time + "时间" + data.e + "时速")
                data.f = it.climbing
                data.g = "72°"
                data.h = DecimalFormat("0.0").format(it.metre100Sprint)
                var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                if (it.locationArray!![0].time.contains(".")) {
                    var m = it.locationArray!![0].time.split(".")
                    var d = Date(m[0].toLong())
                    var sss = simple.format(d)
                    data.i = sss
                } else {
                    var d = Date(it.locationArray!![0].time.toLong())
                    var sss = simple.format(d)
                    data.i = sss
                }

                data.j = user.data?.name
                data.k = getImageUrl(user.data?.headImgFile)
                data.l = getDriverImageUrl(logShareActivity.imgs?.ridingEndBackgroudImg?.get(), logShareActivity.imgs?.baseUrl?.get())
                addChart(logShareActivity.basesult, hight, it.totalTime)
                return@Function data
            }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                Totaldistance.set(it.distance)
                Totaltime.set(it.time)
                maxSpeed.set(it.c)
                maxHight.set(it.d)
                averageRate.set(it.e)
                maxUp.set(it.f)
                direct.set(it.g)
                onehunSpeed.set(it.h)
                finishTime.set(it.i)
                userName.set(it.j)
                driverAvatar.set(it.k)
                bigImg.set(it.l)
            }
        }

        RxSubscriptions.add(RxBus.default?.toObservableSticky(NomalPostStickyEven::class.java)?.subscribe {
            if (it.type == 105) {

            }
        })
    }

    var bigImg = ObservableField<String>()
    var hight = ArrayList<Double>()
    var driverAvatar = ObservableField<String>()
    var userName = ObservableField<String>()
    var finishTime = ObservableField<String>()
    var Totaldistance = ObservableField<String>()
    var averageRate = ObservableField<String>()
    var Totaltime = ObservableField<String>()
    var maxUp = ObservableField<String>()
    var maxSpeed = ObservableField<String>()
    var direct = ObservableField<String>()
    var onehunSpeed = ObservableField<String>()
    var maxHight = ObservableField<String>()

    fun onClick(view: View) {
        when (view.id) {
            R.id.share_show -> {
                activity.behaviors.state = BottomSheetBehavior.STATE_EXPANDED
                bottomVisible.set(true)
            }
            R.id.toRoadLook -> {
                var even = NomalPostStickyEven(106, activity.imgs!!)
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.PLAYER).navigation()
                RxBus.default?.postSticky(even)
            }
            R.id.share_btn -> {
                var share = ShareEntity()
                share.Totaldistance.set(Totaldistance.get())
                share.Totaltime.set(Totaltime.get())
                share.averageRate.set(averageRate.get())
                share.maxUp.set(maxUp.get())
                for (d in hight) {
                    share.hight.add(d)
                }
                share.maxHight.set(maxHight.get())
                share.maxSpeed.set(maxSpeed.get())
                share.onehunSpeed.set(onehunSpeed.get())
                share.userInfo.set(user)

                CoroutineScope(ioContext).async(ioContext) {
                    var file = Glide.with(activity)
                            .load(bigImg.get())
                            .downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    var files = file.get()
                    var bit =  BitmapFactory.decodeFile(files.path)
                    launch {
                        share.shareIcon = bit
                        RxBus.default?.postSticky(share)
                        ARouter.getInstance().build(RouterUtils.MapModuleConfig.SHARE_ACTIVITY).withString(RouterUtils.MapModuleConfig.SHARE_TYPE, "log").navigation()

                    }
//                    share.shareIcon = BitmapFactory.decodeFile(files.path)
                }

//                CoroutineScope(ioContext).async {
//
//                }


//                share.secondBitmap
            }
        }
    }


    fun doas(){

    }

    fun addChart(chart: SuitLines?, list: ArrayList<Double>, time: Long) {
        if (!list.isNotEmpty() && list.size < 6) {
            return
        }
        var k = 0
        var size = list.size
        if ((size - 1) % 15 != 0) {
            k = (size - 1) % 15
        }
        var n = (size - k) / 15
        if (n == 0) {
            return
        }
        chart?.maxOfVisible = n * 6
        var baseTime = time.toInt() / 15.0
        var count = 0
        var lines = ArrayList<Unit>()
        for (i in 0..(size - 1)) {
            if (i % n == 0) {
                count++
                var t = Unit(list[i].toFloat() + 1, (DecimalFormat("0.0").format(baseTime * count) + "s"))
                lines.add(t)
            } else if (i == size - 1) {
                var t = Unit(list[i].toFloat() + 1, "")
                lines.add(t)
            } else {
                lines.add(Unit(list[i].toFloat() + 1))
            }
        }
        chart?.feed(lines)


//        Observable.just(list).subscribeOn(Schedulers.io()).map(Function<ArrayList<Double>, ArrayList<Unit>> {
//            var k = 0
//            var size = list.size
//            if ((size - 1) % 15 != 0) {
//                k = (size - 1) % 15
//            }
//            var n = (size - k) / 15
//            chart?.maxOfVisible = n * 6
//            var baseTime = time.toInt() / 15.0
//            var count = 0
//            var lines = ArrayList<Unit>()
//            for (i in 0..(size - 1)) {
//                if (i % n == 0) {
//                    count++
//                    var t = Unit(list[i].toFloat() + 1, (DecimalFormat("0.00").format(baseTime * count) + "s"))
//                    lines.add(t)
//                } else if (i == size - 1) {
//                    var t = Unit(list[i].toFloat() + 1, "")
//                    lines.add(t)
//                } else {
//                    lines.add(Unit(list[i].toFloat() + 1))
//                }
//            }
//            return@Function lines
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
//            chart?.feed(it)
//        }
    }

    var weatherDatas = ObservableArrayList<WeatherEntity>().apply {
        for (i in 0..24) {
            if (i < 10) {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("0$i:00"), ObservableField("14℃")))
            } else {
                this.add(WeatherEntity(ObservableField(context.getDrawable(R.drawable.ic_sun)), ObservableField("$i:00"), ObservableField("14℃")))
            }
        }
    }

    fun addChildView(layout: LinearLayout?) {
        weatherDatas.forEachIndexed { i, entity ->
            var inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflater.inflate(R.layout.horizontal_weather_child, layout, false)
            var img = view.findViewById<ImageView>(R.id.weather_icon)
            var time = view.findViewById<TextView>(R.id.weather_time)
            var temperature = view.findViewById<TextView>(R.id.weather_temperature)
            img.setImageDrawable(entity.icon.get())
            time.text = entity.time.get()
            temperature.text = entity.temperature.get()
            layout?.addView(view)
            layout?.invalidate()
        }
    }

}