package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Widget.CoverFlowLayoutManger
import com.example.private_module.Activity.AddCarActivity
import com.example.private_module.Adapter.AddCarAdapter
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.Bean.QueryAllcars
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import java.util.concurrent.TimeUnit


class AddCarViewModel : BaseViewModel(), AddCarAdapter.AddCarItemClickCallBack, CoverFlowLayoutManger.OnSelected, AddCarAdapter.editClickCallBack, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
//        finish()

        addCarActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {


    }

    override fun editClick(view: View, position: Int) {
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.PrivateModuleConfig.Edit_CARS, cars[position])
        startFragment(addCarActivity,RouterUtils.PrivateModuleConfig.BOND_CARS,bundle, EDIT_CARS)
//        ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.BOND_CARS).withSerializable(RouterUtils.PrivateModuleConfig.Edit_CARS, cars[position]).navigation(addCarActivity, EDIT_CARS)
    }

    var bottomVisible = ObservableField<Boolean>(true)


    override fun onItemSelected(position: Int) {
        if (position == cars.size - 1) {
            bottomVisible.set(false)
        } else {
            bottomVisible.set(true)
        }
    }

    override fun onItemClick(view: View, position: Int) {
        if (position == cars.size - 1) {
            startFragment(addCarActivity,RouterUtils.PrivateModuleConfig.BOND_CARS, INSERT_CARS)
//            ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.BOND_CARS).navigation(addCarActivity, INSERT_CARS)
        }
    }

    lateinit var addCarActivity: AddCarActivity
    lateinit var adapter: AddCarAdapter
    lateinit var token: String
    var component = TitleComponent()
    var cars = ArrayList<CarsEntity>().apply {
        var entitiy = CarsEntity()
        this.add(entitiy)
    }

    fun inject(addCarActivity: AddCarActivity) {
        this.addCarActivity = addCarActivity
        component.title.set(getString(R.string.add_car))
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.callback = this
        initRecycleView()
    }


    fun initDatas() {
        token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryUserVehicleInfo").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()!!.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            var car = Gson().fromJson<QueryAllcars>(it, QueryAllcars::class.java)
            if (car != null && car.code == 0) {
                car.data?.forEach {
                    var carsEntity = CarsEntity()
                    carsEntity.imgShot.set(it.carImg)
                    carsEntity.brandName.set(it.brandName)
                    carsEntity.brandTypeName.set(it.brandTypeName)
                    carsEntity.carBrandId.set(it.carBrandId)
                    carsEntity.imgUrl.set(getImageUrl(it.carImg!!))
                    carsEntity.carName.set(it.carName)
                    carsEntity.isDefault.set(it.isDefault)
                    carsEntity.id.set(it.id.toString())
                    cars.add(cars.size - 1, carsEntity)
                }
                adapter.setCarDatas(cars)
            }
        }
    }

    private fun initRecycleView() {
        addCarActivity.add_car_recycle.setGreyItem(true)
        addCarActivity.add_car_recycle.setAlphaItem(true)
        addCarActivity.add_car_recycle.setOnItemSelectedListener(this)
        adapter = AddCarAdapter(addCarActivity)
        addCarActivity.add_car_recycle.adapter = adapter
        adapter.setOnItemClickListener(this)
        adapter.setEditOnItemClickListener(this)
        initDatas()
    }

    var CurrentClickTime = 0L
    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        when (view.id) {
            R.id.set_car_default -> {
                if (cars[addCarActivity.add_car_recycle.selectedPos].isDefault.get() == 1) {
                    Toast.makeText(context, getString(R.string.isDefaultSelected), Toast.LENGTH_SHORT).show()
                    return
                }
                if (addCarActivity.add_car_recycle.selectedPos == cars.size - 1) {
                    return
                }
                var refixCar: CarsEntity? = null
                var indexs = 0
                cars.forEachIndexed { index, carsEntity ->
                    if (carsEntity.isDefault.get() == 1) {
                        refixCar = carsEntity
                        indexs = index
                    }
                }
                var car = cars[addCarActivity.add_car_recycle.selectedPos]
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map["id"] = car.id.get()!!
                    map["isDefault"] = "1"
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updateVehicleInfo").build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    if (refixCar != null) {
                        refixCar?.isDefault?.set(0)
                        car.isDefault.set(1)
                        cars[indexs] = car
                        cars[addCarActivity.add_car_recycle.selectedPos] = refixCar!!
                        adapter.setCarDatas(cars)
                    }
                }
            }
            R.id.delete_cars -> {
                if (cars.size == 2) {
                    var dialog = com.elder.zcommonmodule.Utils.DialogUtils.createNomalDialog(addCarActivity.activity!!, getString(R.string.only_one_car), getString(R.string.cancle), getString(R.string.confirm))
                    dialog.setOnBtnClickL(OnBtnClickL {
                        dialog.dismiss()
                    }, OnBtnClickL {
                        delete(false)
                        dialog.dismiss()
                    })
                    dialog.show()
                } else {
                    if (cars[addCarActivity.add_car_recycle.selectedPos].isDefault.get() == 1) {
                        delete(true)
                    } else {
                        delete(false)
                    }
                }
            }
        }
    }

    fun delete(flag: Boolean) {
        if (flag) {
            if (addCarActivity.add_car_recycle.selectedPos == cars.size - 1) {
                return
            }
            var refixCar: CarsEntity? = null
            var indexs = 0
            cars.forEachIndexed { index, carsEntity ->
                Log.e("result", carsEntity.id.get().toString())
                if (carsEntity.isDefault.get() == 1) {
                    refixCar = carsEntity
                    indexs = index
                }
            }

            var car = cars[addCarActivity.add_car_recycle.selectedPos]
            Log.e("result", addCarActivity.add_car_recycle.selectedPos.toString() + "当前索引" + car.id.get() + cars.size)
            var nextCar = cars[addCarActivity.add_car_recycle.selectedPos + 1]
            Observable.create(ObservableOnSubscribe<Response> {
                var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                var map = HashMap<String, String>()
                map["id"] = car.id.get()!!
                var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/deleteUserVehicleInfo").build()
                var call = client.newCall(request)
                var response = call.execute()
                it.onNext(response)
            }).subscribeOn(Schedulers.io()).flatMap(Function<Response, Observable<String>> {
                var t = Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map["id"] = nextCar.id.get()!!
                    map["isDefault"] = "1"
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/updateVehicleInfo").build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                })
                return@Function t
            }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                Log.e("result", it)
                var res = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                if (res.code == 0) {
                    nextCar.isDefault.set(1)
                    cars.set(addCarActivity.add_car_recycle.selectedPos + 1, nextCar)
                    cars.remove(car)
                    adapter.setCarDatas(cars)
                } else {
                    Toast.makeText(context, res.msg, Toast.LENGTH_SHORT).show()
                }

//                if (refixCar != null) {
//                    refixCar?.isDefault?.set(0)
//                    car.isDefault.set(1)
//                    cars[indexs] = car
//                    cars[addCarActivity.add_car_recycle.selectedPos] = refixCar!!
//                    adapter.setCarDatas(cars)
//                }
            }
        } else {
            var car = cars[addCarActivity.add_car_recycle.selectedPos]
            Log.e("result", addCarActivity.add_car_recycle.selectedPos.toString() + "当前的索引的" + car.id.get())
            Observable.create(ObservableOnSubscribe<Response> {
                var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                var map = HashMap<String, String>()
                map["id"] = car.id.get()!!
                var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/deleteUserVehicleInfo").build()
                var call = client.newCall(request)
                var response = call.execute()
                it.onNext(response)
            }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                return@Function it.body()?.string()
            }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                var res = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                if (res.code == 0) {
                    Toast.makeText(context, getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                    cars.remove(car)
                    adapter.setCarDatas(cars)
                } else {
                    Toast.makeText(context, res.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}