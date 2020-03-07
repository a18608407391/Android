package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocation
import com.cstec.administrator.party_module.Adapter.PartyAdapter
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.Fragment.PartyFragment
import com.cstec.administrator.party_module.PartyHomeEntity
import com.cstec.administrator.party_module.PartyHotRecommand
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Inteface.ClockActiveClickListener
import com.elder.zcommonmodule.Inteface.MBCommandClickListener
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Inteface.WonderfulClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class PartyViewModel : BaseViewModel(), TitleClickListener, ClockActiveClickListener, WonderfulClickListener, MBCommandClickListener, HttpInteface.PartyHome {


    var city = ObservableField<String>("湖南省")
    fun onClick(view: View) {
        when (view.id) {
            R.id.notify_icon -> {

            }
            R.id.title_ev -> {
                DialogUtils.showProviceDialog(partyFragment.activity!!, province, "选择城市")
            }
            R.id.search_address -> {

            }
        }
    }

    var province = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            city.set(t)
            initData(location)
        }
    })

    override fun getPartyHomeSuccess(it: String) {
        if (it.isNullOrEmpty() || it == "系统繁忙") {
            return
        }
        partyFragment.dismissProgressDialog()
        items.removeAll()
        var home = Gson().fromJson<PartyHomeEntity>(it, PartyHomeEntity::class.java)
        var entity = PartyHotRecommand()
        home.topActivityList!!.forEach {
            var item = it
            var start = item.ACTIVITY_START!!.split(" ")[0]
            var stop = item.ACTIVITY_STOP!!.split(" ")[0]
            item.ACTIVITY_START = start + "至" + stop + "  距离" + item.SQRTVALUE / 1000 + "km"
            entity.list.add(item)
        }
        var clock = ObservableArrayList<PartyHomeEntity.ClockActive>()
        var mobo = ObservableArrayList<PartyHomeEntity.MBRecommend>()
        var select = ObservableArrayList<PartyHomeEntity.WonderfulActive>()
        if (!home.clockActivityList.isNullOrEmpty()) {
            home.clockActivityList!!.forEach {

                var item = it
                item.DISTANCE = "全长" + item.DISTANCE + "km" + " 距离" + it.SQRTVALUE / 1000 + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
                clock.add(item)
            }
        }
        if (!home.motoActivityList.isNullOrEmpty()) {
            home.motoActivityList!!.forEach {
                var item = it
                item.DISTANCE = "时长" + item.DAY + "天  里程" + item.DISTANCE + "km"
                mobo.add(it)
            }
        }
        if (!home.selectedActivityList.isNullOrEmpty()) {
            home.selectedActivityList!!.forEach {
                var item = it
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop + "  距离" + item.SQRTVALUE / 1000 + "km"
                select.add(item)
            }
        }
        hot.set(entity)
        items.insertItem(ObservableField("热门推荐").get())
        items.insertItem(hot.get())
        items.insertItem(ObservableField("打卡活动").get())
        items.insertList(clock)
        items.insertItem(ObservableField("摩旅推荐").get())
        items.insertList(mobo)
        items.insertItem(ObservableField("精彩活动").get())
        items.insertList(select)
    }

    var hot = ObservableField<PartyHotRecommand>()
    override fun getPartyHomeError(it: Throwable) {
        Log.e("result", "活动数据错误" + it.message)
        partyFragment.dismissProgressDialog()
    }

    override fun onMBcommandClick(entity: Any) {
    }

    override fun onWonderfulClick(entity: Any) {
    }

    override fun onClockActiveClick(entity: Any, position: Int) {

    }

    override fun onTitleArrowClick(entity: Any) {
        //处理Arrow跳转

    }

    var hotRecommendCommand = BindingCommand(object : BindingConsumer<PartyHomeEntity.HotRecommend> {
        override fun call(t: PartyHomeEntity.HotRecommend) {
            //处理点击事件
        }
    })
    lateinit var partyFragment: PartyFragment
    fun inject(partyFragment: PartyFragment) {
        this.partyFragment = partyFragment
        partyFragment.showProgressDialog("正在获取活动数据，请稍后......")
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            invoke(it)
        })
    }

    var location: AMapLocation? = null
    private fun invoke(it: AMapLocation?) {
        if (location == null) {
            initData(it)
            this.location = it
        }
    }

    fun initData(location: AMapLocation?) {
        HttpRequest.instance.partyHome = this
        var map = HashMap<String, String>()
        map["city"] = city.get()!!
        map["x"] = location!!.longitude.toString()
        map["y"] = location!!.latitude.toString()
        HttpRequest.instance.getPartyHome(map)
    }

    var adapter = PartyAdapter()

    var items = MergeObservableList<Any>()

    var titlelistener: TitleClickListener = this

    var activeListener: ClockActiveClickListener = this

    var wonderfulListener: WonderfulClickListener = this

    var mb_click: MBCommandClickListener = this

    var itemBinding = OnItemBindClass<Any>()
            .map(String::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.title, R.layout.base_item_title).bindExtra(BR.title_listener, titlelistener).bindExtra(BR.position,position)
            }
            .map(PartyHotRecommand::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.hot_recommend, R.layout.hot_recommend_item_layout).bindExtra(BR.model, this@PartyViewModel)
            }
            .map(PartyHomeEntity.ClockActive::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.clock_active_item_data, R.layout.clock_item_layout).bindExtra(BR.listener, activeListener)
            }
            .map(PartyHomeEntity.HotDistination::class.java) { itemBinding, position, item -> }
            .map(PartyHomeEntity.MBRecommend::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.mb_recommand, R.layout.mb_recommand_item_layout).bindExtra(BR.mb_listener, mb_click)
            }.map(PartyHomeEntity.WonderfulActive::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.wonderful, R.layout.wonderful_item_layout).bindExtra(BR.wonderful_listener, wonderfulListener)
            }

}