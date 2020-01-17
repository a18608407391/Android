package com.example.private_module.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.BondCarActivity
import com.elder.zcommonmodule.Entity.AllCarsEntity
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.Bean.HeadResultInfo
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zyyoona7.picker.OptionsPickerView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bondcars.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class BondCarViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, DialogUtils.Companion.IconUriCallBack {
    override fun getIcon(iconUri: Uri) {
        this.cameraUri = iconUri
    }

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    var carName = ObservableField<String>()
    var carBrandId: String? = null
    var brandName = ObservableField<String>()
    var brandTypeName = ObservableField<String>()
    var year: OptionsPickerView<AllCarsEntity.AllCarsTypeBean>? = null
    var onlineImg: String? = null
    var avatar = ObservableField<String>()
    var cameraUri: Uri? = null
    var component = TitleComponent()
    lateinit var bondCarActivity: BondCarActivity
    fun inject(bondCarActivity: BondCarActivity) {
        this.bondCarActivity = bondCarActivity
        brandName.set(bondCarActivity.entity?.brandName?.get())
        brandTypeName.set(bondCarActivity.entity?.brandTypeName?.get())
        avatar.set(bondCarActivity.entity?.imgUrl?.get())
        carBrandId = bondCarActivity.entity?.carBrandId?.get()
        carName.set(bondCarActivity.entity?.carName?.get())
        component.title.set(getString(R.string.bond_car))
        component.rightText.set("")
        component.arrowVisible.set(false)
        component.callback = this
    }
    var CurrentClickTime = 0L
    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }

        when (view.id) {
            R.id.bond_car_camera -> {
                DialogUtils.showAnim(bondCarActivity,0)
                DialogUtils.lisentner = this@BondCarViewModel
            }
            R.id.cars_type_choice -> {

                var token = PreferenceUtils.getString(context, USER_TOKEN)
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryAllVehicleBrandAndType").build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    var entity = Gson().fromJson<AllCarsEntity>(it, AllCarsEntity::class.java)
                    year = DialogUtils.showCarsDialog(bondCarActivity, carsCommand, entity)
                }
            }
            R.id.make_sure_add_car -> {
                var name = bondCarActivity.car_name.text.toString()
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(context, getString(R.string.notaddcarName), Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(brandName.get()) || TextUtils.isEmpty(brandTypeName.get())) {
                    Toast.makeText(context, getString(R.string.notaddcarType), Toast.LENGTH_SHORT).show()
                    return
                }
                if (TextUtils.isEmpty(avatar.get())) {
                    Toast.makeText(context, getString(R.string.not_car_photo), Toast.LENGTH_SHORT).show()
                    return
                }
                var token = PreferenceUtils.getString(context, USER_TOKEN)
                bondCarActivity.showProgressDialog(getString(R.string.uploading_cars))
                if (bondCarActivity.entity != null) {
                    Observable.create(ObservableOnSubscribe<Response> {
                        var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
                        var map = HashMap<String, String>()
                        map["id"] = bondCarActivity.entity?.id?.get()!!
                        map["carName"] = name!!
                        onlineImg = bondCarActivity.entity?.imgShot?.get()
                        map["carImg"] = onlineImg!!
                        map["carBrandId"] = carBrandId!!
                        map["brandName"] = brandName.get()!!
                        map["brandTypeName"] = brandTypeName.get()!!
                        var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                        var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updateVehicleInfo").build()
                        var call = client.newCall(request)
                        var response = call.execute()
                        it.onNext(response)
                    }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                        return@Function it.body()?.string()
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        var t = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                        if (t.code == 0) {
                            Toast.makeText(context, "修改车辆信息成功！！", Toast.LENGTH_SHORT).show()
                            bondCarActivity.dismissProgressDialog()
                            carName.set(name)
                            var carEntitiy = CarsEntity()

                            carEntitiy.imgShot.set(bondCarActivity?.entity?.imgShot?.get())
                            carEntitiy.isDefault.set(bondCarActivity?.entity?.isDefault!!.get())
                            carEntitiy.imgUrl.set(getImageUrl(onlineImg!!))
                            carEntitiy.carName.set(name)
                            carEntitiy.carBrandId.set(carBrandId!!)
                            carEntitiy.brandName.set(brandName.get()!!)
                            carEntitiy.brandTypeName.set(brandTypeName.get())
                            carEntitiy.id.set(bondCarActivity.entity?.id!!.get())
                            var intent = Intent()
                            intent.putExtra("carsEntitiy", carEntitiy)
                            bondCarActivity.setResult(EDIT_CARS, intent)
                            finish()
                        }
                    }
                } else {
                    Observable.create(ObservableOnSubscribe<Response> {
                        var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
                        var file = File(avatar.get())
                        var body = RequestBody.create(MediaType.parse("image/jpg"), file)
                        var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.name, body).build()
                        var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/uploadVehicleFile").post(part).build()
                        var call = client.newCall(request)
                        var response = call.execute()
                        it.onNext(response)
                    }).subscribeOn(Schedulers.io()).flatMap(Function<Response, Observable<String>> {
                        var json = it.body()?.string()
                        var t = Observable.create(ObservableOnSubscribe<Response> {
                            var se = Gson().fromJson<HeadResultInfo>(json, HeadResultInfo::class.java)
                            var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
                            var map = HashMap<String, String>()
                            map["carName"] = name!!
                            onlineImg = se.data?.originaImgPath
                            map["carImg"] = onlineImg!!
                            map["carBrandId"] = carBrandId!!
                            map["brandName"] = brandName.get()!!
                            map["brandTypeName"] = brandTypeName.get()!!
                            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/bindSelfVehicle").build()
                            var call = client.newCall(request)
                            var response = call.execute()
                            it.onNext(response)
                        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                            return@Function it.body()?.string()
                        })
                        return@Function t
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                        Log.e("result","添加车辆"+it)
                        Toast.makeText(context, "添加车辆成功！！", Toast.LENGTH_SHORT).show()
                        bondCarActivity.dismissProgressDialog()
                        carName.set(name)
                        var carEntitiy = CarsEntity()
                        carEntitiy.imgUrl.set(getImageUrl(onlineImg!!))
                        carEntitiy.carName.set(name)
                        carEntitiy.carBrandId.set(carBrandId!!)
                        carEntitiy.brandName.set(brandName.get()!!)
                        carEntitiy.brandTypeName.set(brandTypeName.get())
                        var intent = Intent()
                        intent.putExtra("carsEntitiy", carEntitiy)
                        bondCarActivity.setResult(INSERT_CARS, intent)
                        finish()
                    }
                }
            }
        }
    }

    var carsCommand = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            if (t == "ok") {
                brandName.set(year!!.opt1SelectedData.brandName)
                brandTypeName.set(year!!.opt2SelectedData.name)
                carBrandId = year!!.opt2SelectedData.id!!.toString()
            }
        }
    })


}