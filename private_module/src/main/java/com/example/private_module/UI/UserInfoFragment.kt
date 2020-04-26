package com.example.private_module.UI

import android.app.ProgressDialog
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.DataBases.insertUserInfo
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.MsgCountData
import com.elder.zcommonmodule.Http.BaseObserver
import com.example.private_module.BR
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Entitiy.PrivateEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.UserInfoViewModel
import com.example.private_module.databinding.FragmentUserBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.google.gson.Gson
import com.zk.library.Bus.DataEven
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils.PrivateModuleConfig.Companion.USER_INFO
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


@Route(path = RouterUtils.FragmentPath.MYSELFPAGE)
class UserInfoFragment : BaseFragment<FragmentUserBinding, UserInfoViewModel>(), HttpInteface.getMsgNotifyCount {
    override fun getNotifyCountSuccess(it: String) {
        var data = Gson().fromJson<MsgCountData>(it, MsgCountData::class.java)
        var even = ServiceEven()
        even.type = "MsgCount"
        count = data.callMeCount + data.commentCount + data.fabulousCount + data.lastSystemCount
        RxBus.default?.post(even)
    }

    var count = 0

    override fun getNotifyCountError(ex: Throwable) {

    }

    var userInfo: UserInfo? = null
    override fun initContentView(): Int {
        return R.layout.fragment_user
    }

    override fun initVariableId(): Int {
        return BR.user_fr_viewModel
    }

    override fun onUserInvisible() {
        super.onUserInvisible()
    }

    override fun onUserVisible() {
        super.onUserVisible()
    }

    var lableList = ArrayList<String>()
    fun getUserInfo(flag: Boolean) {
        if (userInfo == null) {
            userInfo = Gson().fromJson(PreferenceUtils.getString(context, USER_INFO), UserInfo::class.java)
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
//                        POST /UserPersonalCenter/PersonalCenterDatil
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8")
                    .addHeader("appToken", token)
                    .post(body).url(Base_URL + "AmoskiActivity/UserPersonalCenter/PersonalCenterDetail").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(this!!.activity) {
            override fun onNext(it: String) {
                super.onNext(it)
                var data = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                var code = Gson().fromJson<PrivateEntity>(Gson().toJson(data.data), PrivateEntity::class.java)
                if (data.code == 0 && code != null) {
                    //骑行数据
                    var max = code.queryUserDisCountRidingInfo?.ridingData!![0].maxDis
                    if (max.toInt() == 0) {
                        val cd = Calendar.getInstance()
                        if (cd.get(Calendar.MONTH) in 0..2) {
                            if (cd.get(Calendar.MONTH) == 0) {
                                text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                text3.text = (cd.get(Calendar.MONTH) + 1 - 1 + 12).toString() + "月"
                                text2.text = (cd.get(Calendar.MONTH) + 1 - 2 + 12).toString() + "月"
                                text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            } else if (cd.get(Calendar.MONTH) == 1) {
                                text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                                text2.text = (cd.get(Calendar.MONTH) + 1 - 2 + 12).toString() + "月"
                                text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            } else if (cd.get(Calendar.MONTH) == 2) {
                                text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                                text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                                text2.text = (cd.get(Calendar.MONTH) + 1 - 2).toString() + "月"
                                text1.text = (cd.get(Calendar.MONTH) + 1 - 3 + 12).toString() + "月"
                            }
                        } else {
                            text4.text = (cd.get(Calendar.MONTH) + 1).toString() + "月"
                            text3.text = (cd.get(Calendar.MONTH) + 1 - 1).toString() + "月"
                            text2.text = (cd.get(Calendar.MONTH) + 1 - 2).toString() + "月"
                            text1.text = (cd.get(Calendar.MONTH) + 1 - 3).toString() + "月"
                        }
                    } else {
                        code.queryUserDisCountRidingInfo!!.ridingData!!.forEachIndexed { index, progressData ->
                            if (index == 0) {
                                viewModel?.allTotal?.set(DecimalFormat("0").format(progressData.allTotalDis!! / 1000))
                                viewModel?.allTime?.set(ConvertUtils.formatTimeS(progressData.allTotalTime))
                            } else if (index == 1) {
                                progress4.progress = (progressData.allTotalDis * 100 / max).toInt()
                                text4.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 2) {
                                progress3.progress = (progressData.allTotalDis * 100 / max).toInt()
                                text3.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 3) {
                                progress2.progress = (progressData.allTotalDis * 100 / max).toInt()
                                text2.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            } else if (index == 4) {
                                progress1.progress = (progressData.allTotalDis * 100 / max).toInt()
                                text1.text = progressData.ridingMonth!!.split("-")[1].toInt().toString() + "月"
                            }
                        }
                    }

                    viewModel?.fr_avatar?.set(getImageUrl(userInfo?.data?.headImgFile))
                    viewModel?.name?.set(userInfo?.data?.name)
                    viewModel?.dynamicsStr!!.set(code.PersonalCenterDatil?.DynamicCount.toString())
                    viewModel?.like!!.set(code.PersonalCenterDatil?.FabulousCount.toString())
                    viewModel?.fans!!.set(code.PersonalCenterDatil?.FansCount.toString())
                    viewModel?.focus!!.set(code.PersonalCenterDatil?.FollowCount.toString())
                    initNet()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
            }
        })
    }

    var twl: Array<String>? = null
    fun dismiss(d: ProgressDialog) {
        if (d != null && d.isShowing) {
            d.dismiss()
        }
    }

    lateinit var text1: TextView
    lateinit var text2: TextView
    lateinit var text3: TextView
    lateinit var text4: TextView

    lateinit var progress1: ProgressBar
    lateinit var progress2: ProgressBar
    lateinit var progress3: ProgressBar
    lateinit var progress4: ProgressBar
    lateinit var user_nest: NestedScrollView
    override fun initData() {
        super.initData()
        viewModel?.receiveLocation(arguments!!.getParcelable("location"))
        text1 = binding!!.root.findViewById(R.id.vertical_text1)
        text2 = binding!!.root.findViewById(R.id.vertical_text2)
        text3 = binding!!.root.findViewById(R.id.vertical_text3)
        text4 = binding!!.root.findViewById(R.id.vertical_text4)
        progress1 = binding!!.root.findViewById(R.id.vertical_progressbar1)
        progress2 = binding!!.root.findViewById(R.id.vertical_progressbar2)
        progress3 = binding!!.root.findViewById(R.id.vertical_progressbar3)
        progress4 = binding!!.root.findViewById(R.id.vertical_progressbar4)
        user_nest = binding!!.root.findViewById(R.id.user_nest)
        setchart()
        RxSubscriptions.add(RxBus.default?.toObservable(DataEven::class.java)!!.subscribe {
            var m = it.value + count
            CoroutineScope(uiContext).launch {
                viewModel?.msgCount!!.set(m)
            }
        })

        viewModel?.inject(this)
    }


    fun initNet() {
        CoroutineScope(ioContext).launch {
            delay(500)
            HttpRequest.instance.getMsgCount = this@UserInfoFragment
            HttpRequest.instance.getMsgNotifyCount(HashMap())
        }
    }

    fun setchart() {
//        line_chart.setYAxis(false)
//        line_chart.setXAxis(false)
//        line_chart.setBorderSpacing(ConvertUtils.dp2px(1F))
//        line_chart.setYLabels(AxisRenderer.LabelPosition.NONE)
//        line_chart.setXLabels(AxisRenderer.LabelPosition.OUTSIDE)
//        line_chart.setFontSize(ConvertUtils.dp2px(8F))
//        line_chart.setLabelsColor(getColor(R.color.hint_color_edit))
//        line_chart.setOnEntryClickListener { setIndex, entryIndex, rect ->
//            line_chart.setIndex(entryIndex)
//        }
    }


    fun callback(info: UserInfo) {
        Log.e("user", "${Gson().toJson(userInfo)}")
        userInfo = info
        insertUserInfo(userInfo?.data!!)
        PreferenceUtils.putString(context, USER_INFO, Gson().toJson(userInfo))
        CoroutineScope(uiContext).launch {
            viewModel?.fr_avatar?.set(getImageUrl(userInfo?.data?.headImgFile))
            viewModel?.name?.set(userInfo?.data?.name)
        }
    }


    //    private var mValues = floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.2f)
//    fun initChart(i: Int, lable: Array<String>?, half: Boolean) {
//        if (line_chart == null || lable == null) {
//            return
//        }
//        line_chart.reset()
//        line_chart.setIndex(i)
//        line_chart.notifyDataUpdate()
//
////        line_chart.setAxisLabelsSpacing(ConvertUtils.dp2px(20F))
////        line_chart.setBorderSpacing(ConvertUtils.dp2px(20F))
//        var data = LineSet()
//        if (half) {
//            var halfLable = arrayOf(lable!![lable.size - 6], lable!![lable.size - 5], lable!![lable.size - 4], lable!![lable.size - 3], lable!![lable.size - 2], lable!![lable.size - 1])
//            var halfValues = floatArrayOf(mValues[mValues.size - 6], mValues[mValues.size - 5], mValues[mValues.size - 4], mValues[mValues.size - 3], mValues[mValues.size - 2], mValues[mValues.size - 1])
//            halfLable.forEachIndexed { index, s ->
//                data.addPoint(s, halfValues[index])
//            }
//        } else {
//            lable!!.forEachIndexed { index, s ->
//                data.addPoint(s, mValues[index])
//            }
//        }
//        data.setColor(Color.parseColor("#62B297"))
////                .setGradientFill(intArrayOf(Color.parseColor("#FFFFFF"), Color.parseColor("#62B297")), null)
//                .setThickness(ConvertUtils.dp2px(3F).toFloat())
//                .setDotsRadius(ConvertUtils.dp2px(6F).toFloat())
//                .setDotsStrokeThickness(ConvertUtils.dp2px(3F).toFloat())
//                .setDotsStrokeColor(Color.WHITE)
//                .setDotsColor(Color.parseColor("#62B297"))
//                .beginAt(0)
//
//        var chartPaint = Paint()
//        chartPaint.color = getColor(R.color.hint_color_edit)
//        chartPaint.style = Paint.Style.STROKE
//        chartPaint.isAntiAlias = true
//        if (half) {
//            line_chart.setGrid(1, 5, chartPaint)
//        } else {
//            line_chart.setGrid(1, 11, chartPaint)
//        }
//        line_chart.addData(data)
//        line_chart.show()
//    }
    private var WAIT_TIME = 2000L
    private var TOUCH_TIME: Long = 0
    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity!!.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            Toast.makeText(_mActivity, "再次点击退出App", Toast.LENGTH_SHORT).show()
        }
        return true
    }

}