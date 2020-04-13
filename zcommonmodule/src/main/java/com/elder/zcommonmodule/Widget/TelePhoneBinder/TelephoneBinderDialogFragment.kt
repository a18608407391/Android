package com.elder.zcommonmodule.Widget.TelePhoneBinder

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.StyleRes
import android.support.v4.app.DialogFragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.BindPhoneBean
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.util.concurrent.TimeUnit


class TelephoneBinderDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onDetach() {
        super.onDetach()
        if(timer!=null){
            timer!!.cancel()
            timer  =  null
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.send_varifycode) {
            if (isDowm) {
                return
            }
            doVerfy()
        } else if (v?.id == R.id.back) {//返回
            functionDismiss?.onBack()
        } else {
            if (phoneNumber.text.toString().length < 11) {
                Toast.makeText(context, "手机号输入错误！", Toast.LENGTH_SHORT).show()
                return
            }
            if (verifyCodeNumber.text.toString().length != 4) {
                Toast.makeText(context, "验证码错误！", Toast.LENGTH_SHORT).show()
                return
            }
            binder()
        }
    }

    private var mAnimStyle = R.style.DefaultCityPickerAnimation
    private var mContentView: View? = null
    private var height: Int = 0
    private var width: Int = 0

    companion object {
        fun newInstance(enable: Boolean): TelephoneBinderDialogFragment {
            var fragment = TelephoneBinderDialogFragment()
            var args = Bundle()
            args.putBoolean("cp_enable_anim", enable)
            fragment.setArguments(args)
            return fragment
        }
    }


    fun doVerfy() {
        if (phoneNumber.text.toString().length < 11) {
            Toast.makeText(context, "手机号输入错误！", Toast.LENGTH_SHORT).show()
            return
        }

        var dialog = DialogUtils.showProgress(this.activity!!, getString(R.string.http_loading))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = phoneNumber.text.toString()
            map["type"] = "4"
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().url(Base_URL + "AmoskiActivity/memberUser/getMobileCode").post(body).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(activity) {
            override fun onNext(it: String) {
                super.onNext(it)
                var request = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                if (request.code == 0) {
                    initTimer(120000)
                    timer!!.start()
                    Toast.makeText(context, "获取验证码成功！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "手机号已被注册！", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                dialog.dismiss()
            }

            override fun onComplete() {
                super.onComplete()
                dialog.dismiss()
            }
        })
    }


    fun binder() {
        var dialog = DialogUtils.showProgress(this.activity!!, getString(R.string.http_loading))
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        var openid = PreferenceUtils.getString(context, OPENID)
        var unionid = PreferenceUtils.getString(context, UNIONID)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["mobile"] = phoneNumber.text.toString()
            map["validCode"] = verifyCodeNumber.text.toString()
            map["openId"] = openid
            map["unionId"] = unionid
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8")
                    .post(body).url(Base_URL + "AmoskiActivity/userCenterManage/bindMobileNumber").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(activity) {
            override fun onNext(t: String) {
                super.onNext(t)
                var bindPhoneBean = Gson().fromJson(t, BindPhoneBean::class.java)
                if (bindPhoneBean.code == 0) {
                    PreferenceUtils.putString(context, USER_TOKEN, bindPhoneBean.data)
                    dismiss()
                    Toast.makeText(activty, bindPhoneBean.msg, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activty, bindPhoneBean.msg, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        })
    }

    var isDowm = false
    var timer: CountDownTimer? = null
    fun initTimer(totalTime: Long) {
        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onFinish() {
                isDowm = false
                verifyCodeTv.text = getString(R.string.send_again)
            }

            override fun onTick(millisUntilFinished: Long) {
                isDowm = true
                verifyCodeTv.text = (millisUntilFinished / 1000).toString() + "S"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CityPickerStyle)
    }

    @SuppressLint("ResourceType")
    fun setAnimationStyle(@StyleRes resId: Int) {
        this.mAnimStyle = if (resId <= 0) mAnimStyle else resId
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = inflater.inflate(R.layout.phone_dialog_binder, container, false)
        return mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
    }

    lateinit var verifyCodeTv: TextView
    lateinit var clear_phoneIv: ImageView
    lateinit var verifyCodeNumber: EditText
    lateinit var phoneNumber: EditText
    lateinit var binderBtn: TextView
    lateinit var ivBack: ImageView

    private fun initViews() {
        verifyCodeNumber = mContentView?.findViewById<EditText>(R.id.verifyCode_number_et)!!
        phoneNumber = mContentView?.findViewById<EditText>(R.id.phone_number_et)!!
        clear_phoneIv = mContentView?.findViewById<ImageView>(R.id.clear_phone)!!
        verifyCodeTv = mContentView?.findViewById(R.id.send_varifycode)!!
        binderBtn = mContentView?.findViewById(R.id.binder_phone)!!
        ivBack = mContentView?.findViewById(R.id.back)!!
        ivBack.setOnClickListener(this)
        binderBtn.setOnClickListener(this)
        verifyCodeTv.setOnClickListener(this)
        clear_phoneIv.setOnClickListener {
            phoneNumber.setText("")
            phoneNumber.setSelection(0)
        }
    }

    private fun initData() {

    }

    private fun measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val dm = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getRealMetrics(dm)
            height = dm.heightPixels
            width = dm.widthPixels
        } else {
            val dm = resources.displayMetrics
            height = dm.heightPixels
            width = dm.widthPixels
        }
    }


    override fun dismiss() {
        super.dismiss()
        functionDismiss?.onDismiss()
    }

    var functionDismiss: DismissListener? = null

    interface DismissListener {
        fun onDismiss()
        fun onBack()
    }

}