package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.cstec.administrator.party_module.Adapter.PartyAdapter
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.Fragment.PartyFragment
import com.cstec.administrator.party_module.PartyHomeEntity
import com.cstec.administrator.party_module.PartyHotRecommand
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Inteface.ClockActiveClickListener
import com.elder.zcommonmodule.Inteface.MBCommandClickListener
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Inteface.WonderfulClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class PartyViewModel : BaseViewModel(), TitleClickListener, ClockActiveClickListener, WonderfulClickListener, MBCommandClickListener, HttpInteface.PartyHome, HttpInteface.PartyUnReadNotify_inf {
    var itemBinding = OnItemBindClass<Any>()
            .map(String::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBinding111-->$position--${Gson().toJson(item)}")
                itemBinding.set(BR.title, R.layout.base_item_title)
                        .bindExtra(BR.title_listener, titlelistener)
                        .bindExtra(BR.position, position)
            }
            .map(PartyHotRecommand::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBinding222-->$position")
                itemBinding.set(BR.hot_recommend, R.layout.hot_recommend_item_layout)
                        .bindExtra(BR.model, this@PartyViewModel)
            }
            .map(PartyHomeEntity.ClockActive::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBinding333-->$position")
                itemBinding.set(BR.clock_active_item_data,
                        R.layout.clock_item_layout)
                        .bindExtra(BR.listener, activeListener)
            }
            .map(PartyHomeEntity.HotDistination::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBinding444-->$position")
            }
            .map(PartyHomeEntity.MBRecommend::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBinding555-->$position")
                itemBinding.set(BR.mb_recommand, R.layout.mb_recommand_item_layout)
                        .bindExtra(BR.mb_listener, mb_click)
            }.map(PartyHomeEntity.WonderfulActive::class.java) { itemBinding, position, item ->
                Log.e("activity","itemBindin666-->$position")
                itemBinding.set(BR.wonderful, R.layout.wonderful_item_layout)
                        .bindExtra(BR.wonderful_listener, wonderfulListener)
            }



    override fun PartyUnReadNotifySucccess(it: String) {
        var o = Integer.valueOf(it)
        msgCount.set(o)
    }

    override fun PartyUnReadNotifyError(it: Throwable) {

    }

    var refreshStatus = ObservableField(0)

    var refreshCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            initData(location)
            refreshStatus.set(1)
        }
    })

    var msgCount = ObservableField(0)

    var city = ObservableField<String>("湖南省")
    fun onClick(view: View) {
        when (view.id) {
            R.id.notify_icon -> {
                ARouter.getInstance().build(RouterUtils.Chat_Module.ActiveNotify_AC).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, Location(location!!.latitude, location!!.longitude)).navigation()
            }
            R.id.title_ev -> {
                DialogUtils.showProviceDialog(partyFragment.activity!!, province, "选择城市")
            }
            R.id.search_address -> {
                ARouter.getInstance().build(RouterUtils.PartyConfig.SEARCH_PARTY)
                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(location!!.latitude, location!!.longitude))
                        .withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
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
        partyFragment.dismissProgressDialog()
        if (it.isNullOrEmpty() || it == "系统繁忙") {
            return
        }
        refreshStatus.set(2)
        items.removeAll()
        var home = Gson().fromJson<PartyHomeEntity>(it, PartyHomeEntity::class.java)
        var entity = PartyHotRecommand()
        home.topActivityList!!.forEach {
            var item = it
            var start = item.ACTIVITY_START!!.split(" ")[0]
            var stop = item.ACTIVITY_STOP!!.split(" ")[0]
            item.ACTIVITY_START = start + "至" + stop + "  距离" + item.SQRTVALUE + "km"
            if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                item.TICKET_PRICE = "免费"
            } else {
                item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
            }
            entity.list.add(item)
        }
        var clock = ObservableArrayList<PartyHomeEntity.ClockActive>()
        var mobo = ObservableArrayList<PartyHomeEntity.MBRecommend>()
        var select = ObservableArrayList<PartyHomeEntity.WonderfulActive>()
        if (!home.clockActivityList.isNullOrEmpty()) {
            home.clockActivityList!!.forEach {
                var item = it
                item.DISTANCE = "全长" + item.DISTANCE + "km" + " 距离" + it.SQRTVALUE + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
                clock.add(item)
            }
        }

        if (!home.motoActivityList.isNullOrEmpty()) {
            home.motoActivityList!!.forEach {
                var item = it
                if (it.DISTANCE.isNullOrEmpty()) {
                    item.DISTANCE = "时长" + item.DAY + "天" + " 里程" + "0" + "km"
                } else {
                    item.DISTANCE = "时长" + item.DAY + "天" + " 里程" + it.DISTANCE + "km"
                }
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }
                mobo.add(item)
            }
        }
        if (!home.selectedActivityList.isNullOrEmpty()) {
            home.selectedActivityList!!.forEach {
                var item = it
//                item.DISTANCE = "全长" + item.DISTANCE + "km" + " 距离" + it.SQRTVALUE / 1000 + "km"
                var start = item.ACTIVITY_START!!.split(" ")[0]
                var stop = item.ACTIVITY_STOP!!.split(" ")[0]
                item.ACTIVITY_START = start + "至" + stop + " 距离" + item.SQRTVALUE + "km"
                if (item.TICKET_PRICE.isNullOrEmpty() || item.TICKET_PRICE!!.toDouble() <= 0) {
                    item.TICKET_PRICE = "免费"
                } else {
                    item.TICKET_PRICE = getString(R.string.rmb) + item.TICKET_PRICE
                }

                if (!item.TYPE.isNullOrEmpty()) {
                    item.TYPE!!.split(",").forEachIndexed { index, s ->
                        when (index) {
                            0 -> {
                                item.type1 = s
                            }
                            1 -> {
                                item.type2 = s
                            }
                            2 -> {
                                item.type3 = s
                            }
                        }
                    }
                }
                select.add(item)
            }
        }

        hot.set(entity)
        items.insertItem(ObservableField("热门推荐").get())
        items.insertItem(hot.get())
        if (!clock.isEmpty()) {
            items.insertItem(ObservableField("打卡活动").get())
            items.insertList(clock)
        }
        items.insertItem(ObservableField("摩旅推荐").get())
        items.insertList(mobo)
        items.insertItem(ObservableField("精彩活动").get())
        items.insertList(select)

        Log.e("activity","================================")
        items.forEach {
            Log.e("activity","${Gson().toJson(it)}")
        }
        Log.e("activity","================================")
//        getUnRead()
    }


    fun getUnRead() {
        HttpRequest.instance.partyUnRead = this
        HttpRequest.instance.getPartyActiveUnRead(HashMap())
    }

    var hot = ObservableField<PartyHotRecommand>()
    override fun getPartyHomeError(it: Throwable) {
        partyFragment.dismissProgressDialog()
    }

    override fun onMBcommandClick(entity: Any) {
        var en = entity as PartyHomeEntity.MBRecommend
        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, en.ID)
                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(location!!.latitude, location!!.longitude)).withInt(RouterUtils.PartyConfig.PARTY_CODE, en.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
    }

    override fun onWonderfulClick(entity: Any) {
        var ac = entity as PartyHomeEntity.WonderfulActive
        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, ac.ID)
                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(location!!.latitude, location!!.longitude)).withInt(RouterUtils.PartyConfig.PARTY_CODE, ac.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
    }

    override fun onClockActiveClick(entity: Any) {
        var clock = entity as PartyHomeEntity.ClockActive
        ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, clock.ID)
                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(location!!.latitude, location!!.longitude)).withInt(RouterUtils.PartyConfig.PARTY_CODE, clock.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
    }


    override fun onTitleArrowClick(entity: Any) {
        //处理Arrow跳转
        var title = entity as String
        var type = 0
        if (title == "打卡活动") {
            type = 2
        } else if (title == "摩旅推荐") {
            type = 1
        } else if (title == "精彩活动") {
            type = 0
        }
        ARouter.getInstance().build(RouterUtils.PartyConfig.SUBJECT_PARTY).withInt(RouterUtils.PartyConfig.Party_SELECT_TYPE, type)
                .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(location!!.latitude, location!!.longitude)).withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
    }

    var hotRecommendCommand = BindingCommand(object : BindingConsumer<PartyHomeEntity.HotRecommend> {
        override fun call(t: PartyHomeEntity.HotRecommend) {
            //处理点击事件
            if (t.ID == 0) {
                return
            }
            ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, t.ID)
                    .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                            Location(location!!.latitude, location!!.longitude)).withInt(RouterUtils.PartyConfig.PARTY_CODE, t.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, city.get()).navigation()
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



    var titlelistener: TitleClickListener = this

    var activeListener: ClockActiveClickListener = this

    var wonderfulListener: WonderfulClickListener = this

    var mb_click: MBCommandClickListener = this


    var adapter = PartyAdapter()
    var items = MergeObservableList<Any>()

}