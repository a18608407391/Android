package com.example.private_module.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.UserInfoActivity
import com.example.private_module.Bean.HeadResultInfo
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zyyoona7.wheel.WheelView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_userinfo.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.io.File
import java.util.concurrent.TimeUnit


class AcUserInfoViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, com.zyyoona7.wheel.WheelView.OnItemSelectedListener<String>, DialogUtils.Companion.IconUriCallBack {
    var iconUri: Uri? = null


    override fun getIcon(iconUri: Uri) {
        this.iconUri = iconUri
    }

    override fun onItemSelected(wheelView: WheelView<String>?, data: String?, position: Int) {

    }

    override fun onComponentClick(view: View) {
//        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
//        finish()

        userInfoActivity._mActivity!!.onBackPressedSupport()
    }


    override fun onComponentFinish(view: View) {
        if (nickname.get().isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.enter_name_warm), Toast.LENGTH_SHORT).show()
            return
        }

        if (avatars.get().isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.avatar_warm), Toast.LENGTH_SHORT).show()
            return
        }

        if (birthday.get().isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.birthday_warm), Toast.LENGTH_SHORT).show()
            return
        }

        if (gender.get().isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.gender_warm), Toast.LENGTH_SHORT).show()
            return
        }

        if (adress.get().isNullOrEmpty()) {
            Toast.makeText(context, getString(R.string.address_warm), Toast.LENGTH_SHORT).show()
            return
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        userInfoActivity.showProgressDialog(getString(R.string.get_user_info))
//        Log.e("result",Gson().toJson(userInfoActivity?.userInfo) + "userInfo")
        if (userInfoActivity.realPath == null) {
            uploadNetPic(userInfoActivity?.userInfo?.data?.headImgFile!!, token)
        } else if (avatars.get()?.startsWith("http")!!) {
            uploadNetPic(avatars.get()!!, token)
        } else {
//            Log.e("result",avatars.get())
            Observable.create(ObservableOnSubscribe<Response> {
                var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
                var file = File(avatars.get())
                var body = RequestBody.create(MediaType.parse("image/jpg"), file)
                var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
                var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/uploadHeaderFile").post(part).build()
                var call = client.newCall(request)
                var response = call.execute()
                it.onNext(response)
            }).doOnError {
                userInfoActivity.dismissProgressDialog()
            }.subscribeOn(Schedulers.io()).flatMap(Function<Response, Observable<String>> {
                var result = Gson().fromJson<HeadResultInfo>(it.body()?.string(), HeadResultInfo::class.java)
                if (result != null && result.code == 0) {
                    var t = Observable.create(ObservableOnSubscribe<Response> {
                        var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
                        var map = HashMap<String, String>()
                        map["name"] = nickname.get()!!
                        map["headImgFile"] = result.data?.originaImgPath!!
                        map["yearOfBirth"] = birthday.get()!!
                        map["address"] = adress.get()!!
                        map["synopsis"] = content.get()!!
                        userInfoActivity.userInfo!!.data!!.headImgFile = result.data?.originaImgPath!!
                        if (gender.get() == "男") {
                            map["memberSex"] = "1"
                        } else if (gender.get() == "女") {
                            map["memberSex"] = "2"
                        }
                        var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                        var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updateUserInfo1").build()
                        var call = client.newCall(request)
                        var response = call.execute()
                        it.onNext(response)
                    }).doOnError {
                        userInfoActivity.dismissProgressDialog()
                    }.subscribeOn(Schedulers.io()).map(Function<Response, String> {
                        return@Function it.body()?.string()
                    })
                    return@Function t
                }
                return@Function Observable.just("")

            }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<String> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: String) {
                    if (t != null && !t.isEmpty()) {
                        saveAndReturn(t)
                    }
                }

                override fun onError(e: Throwable) {

                    userInfoActivity.dismissProgressDialog()
                }
            })
        }
    }


    fun uploadNetPic(url: String, token: String) {
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            Log.e("result", "nickname" + nickname.get())
            map["name"] = nickname.get()!!
            map["headImgFile"] = url
            map["yearOfBirth"] = birthday.get()!!
            map["address"] = adress.get()!!
            map["synopsis"] = content.get()!!
            if (gender.get() == "男") {
                map["memberSex"] = "1"
            } else if (gender.get() == "女") {
                map["memberSex"] = "2"
            }

            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updateUserInfo1").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map {
            return@map it.body()?.string()
        }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            var json = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
            if (json.code == 0) {
                saveAndReturn(it!!)
            } else {
                userInfoActivity.dismissProgressDialog()
                Toast.makeText(context, json.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveAndReturn(it: String) {

        userInfoActivity.userInfo!!.data!!.name = nickname.get()
        userInfoActivity.userInfo!!.data!!.headImgUrl = avatars.get()
        userInfoActivity.userInfo!!.data!!.yearOfBirth = birthday.get()
        userInfoActivity.userInfo!!.data!!.synopsis = content.get()
        userInfoActivity.userInfo!!.data!!.sex = if (gender.get() == "男") "1" else "2"
        userInfoActivity.userInfo!!.data!!.address = adress.get()

        var bundle = Bundle()
        bundle.putSerializable("userInfo", userInfoActivity.userInfo)
        userInfoActivity.setFragmentResult(GET_USERINFO, bundle)
//        var intent = Intent()
//        intent.putExtra("userInfo", userInfoActivity.userInfo)
//        userInfoActivity.setResult(GET_USERINFO, intent)
//        Toast.makeText(context, "个人信息修改成功！", Toast.LENGTH_SHORT).show()
        userInfoActivity.dismissProgressDialog()
        userInfoActivity!!._mActivity!!.onBackPressedSupport()
//        finish()
    }

    var component = TitleComponent()
    var nickname = ObservableField<String>("")
    var gender = ObservableField<String>(getString(R.string.male))
    var birthday = ObservableField<String>("2014-09-07")
    var adress = ObservableField<String>("湖南省、长沙市")
    var avatars = ObservableField<String>("")
    var content = ObservableField<String>("")
    lateinit var userInfoActivity: UserInfoActivity
    fun inject(userInfoActivity: UserInfoActivity) {
        this.userInfoActivity = userInfoActivity
        if (userInfoActivity.userInfo == null) {
        }
        component.title.set(getString(R.string.privateInfo))
        component.setCallBack(this)
        userInfoActivity.edit_introduce.setSelection(0)
        component.arrowVisible.set(false)
        if (userInfoActivity.userInfo != null && userInfoActivity.userInfo!!.data != null) {
            if (userInfoActivity.userInfo!!.data!!.name != null) {
                nickname.set(userInfoActivity.userInfo!!.data!!.name)
            }
            if (userInfoActivity.userInfo!!.data!!.yearOfBirth != null) {
                birthday.set(userInfoActivity.userInfo!!.data!!.yearOfBirth)
            }
            if (!userInfoActivity.userInfo!!.data!!.sex.isNullOrEmpty()) {
                if (userInfoActivity.userInfo!!.data!!.sex == "1") {
                    gender.set(getString(R.string.male))
                } else {
                    gender.set(getString(R.string.remale))
                }
            } else {
                gender.set(getString(R.string.male))
            }
            if (userInfoActivity.userInfo!!.data!!.address != null) {
                adress.set(userInfoActivity.userInfo!!.data!!.address)
            }
            if (userInfoActivity.userInfo?.data!!.headImgFile != null) {
                avatars.set(getImageUrl(userInfoActivity.userInfo?.data!!.headImgFile!!))
            }
            if (userInfoActivity.userInfo?.data!!.synopsis != null) {
                content.set(userInfoActivity.userInfo?.data!!.synopsis)
                contentSize.set(userInfoActivity.userInfo?.data!!.synopsis!!.length.toString())
            }
        }
    }

    var CurrentClickTime = 0L
    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {
            R.id.user_info_avater -> {
                DialogUtils.showFragmentAnim(userInfoActivity, 0)
                DialogUtils.lisentner = this@AcUserInfoViewModel
            }
            R.id.user_info_avater_arrow -> {
            }
            R.id.user_info_nickname -> {
                var bundle = Bundle()
                bundle.putString(RouterUtils.PrivateModuleConfig.NICKNAME, nickname.get())
                startFragment(userInfoActivity,RouterUtils.PrivateModuleConfig.CHANGENICKNAME,bundle,GET_NICKNAME)
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.CHANGENICKNAME).withString(RouterUtils.PrivateModuleConfig.NICKNAME, nickname.get()).navigation(userInfoActivity.activity, GET_NICKNAME)
            }
            R.id.user_info_gender -> {
                var list = ArrayList<String>()
                list.add(getString(com.elder.zcommonmodule.R.string.male))
                list.add(getString(com.elder.zcommonmodule.R.string.remale))
                DialogUtils.showGenderDialog(userInfoActivity.activity!!, genderCommand, list, getString(R.string.choice_gender))
            }
            R.id.user_info_gender_arrow -> {
            }
            R.id.user_info_birthday -> {
                var year = DialogUtils.showBirthdayDialog(userInfoActivity.activity!!, birthdayCommand)
                var birthday = birthday.get()
                if (birthday.isNullOrEmpty()) {
                    birthday = "1980-08-27"
                }
                var yeard = 0
                var month = 0
                var day = 0
                var m = birthday?.split("-")
                yeard = Integer.valueOf(m!![0])
                month = Integer.valueOf(m!![1])
                day = Integer.valueOf(m!![2])
                year?.setSelectedYear(yeard, true)
                year?.setSelectedMonth(month, true)
                year?.selectedDay = day
            }
            R.id.user_info_birthday_arrow -> {
            }
            R.id.user_info_address -> {
                DialogUtils.showCityDialog(userInfoActivity.activity!!, cityCommand)
            }

            R.id.user_info_address_arrow -> {

            }
        }
    }

    var birthdayCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            birthday.set(t)
        }
    })
    var cityCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            adress.set(t)
        }
    })
    var genderCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            gender.set(t)
        }
    })

    var contentSize = ObservableField<String>("0")
    var contentChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            if (t.length > 160) {
                return
            }
            content.set(t)
            contentSize.set(t.length.toString())
        }
    })
}