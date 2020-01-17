package com.example.drivermodule.ViewModel.RoadBook

import android.content.Intent
import android.databinding.ObservableArrayList
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Activity.MyRoadBookActivity
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.USERID
import org.json.JSONObject


class MyRoadViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, HttpInteface.getMyRoadBook {
    override fun getMyRoadBookSuccess(it: String) {
        Log.e("result",it)
        myRoadBookActivity.dismissProgressDialog()
        var data = JSONObject(it).getString("data")
        var list = Gson().fromJson<ArrayList<HotData>>(data, object : TypeToken<ArrayList<HotData>>() {}.type)
        list.forEach {
            items.add(it)
        }
    }


    fun ItemClickCommand(date: HotData) {
        var ho = PreferenceUtils.getString(myRoadBookActivity, PreferenceUtils.getString(context, USERID) + "hot")
        var s = Gson().fromJson<HotData>(ho, HotData::class.java)
        if (s != null && s!!.id != date.id) {
            var dialog = DialogUtils.createNomalDialog(myRoadBookActivity, getString(R.string.isExChangeLine), getString(R.string.cancle), getString(R.string.confirm))
            dialog.setOnBtnClickL(OnBtnClickL {
                dialog.dismiss()
            }, OnBtnClickL {
                dialog.dismiss()
                RxBus.default?.post(date)
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad").withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, date).navigation(myRoadBookActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            })
            dialog.show()
        } else {
            RxBus.default?.post(date)
            ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "myroad").withSerializable(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY_ROAD, date).navigation(myRoadBookActivity, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        }
    }

    override fun getMyRoadBookkError(ex: Throwable) {
    }

    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }

    override fun onComponentFinish(view: View) {

    }

    lateinit var myRoadBookActivity: MyRoadBookActivity
    fun inject(myRoadBookActivity: MyRoadBookActivity) {
        this.myRoadBookActivity = myRoadBookActivity
        titleComponent.title.set(getString(R.string.my_road_book))
        titleComponent.rightText.set("")
        titleComponent.rightVisibleType.set(true)
        titleComponent.arrowVisible.set(false)
        titleComponent.callback = this
        myRoadBookActivity.showProgressDialog(getString(R.string.get_my_road_http))
        HttpRequest.instance.myRoadBook = this
        var map = HashMap<String, String>()
        map["limit"] = "30"
        map["page"] = "1"
        HttpRequest.instance.getMyRoadBook(map)
    }

    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<HotData>()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.hot_data, R.layout.hot_recy_myitem_layout).bindExtra(BR.my_model, this@MyRoadViewModel)
    }

}