package com.example.drivermodule.Component

import android.databinding.ObservableArrayList
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Widget.RoadBook.HoriDatas
import com.elder.zcommonmodule.getRoadImgUrl
import com.example.drivermodule.BR
import com.example.drivermodule.Controller.RoadBookItemModel
import com.example.drivermodule.Entity.RoadBook.*
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBookFirstViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_roadbook_first.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.MergeObservableList
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat


class SimpleItemRecycleComponent {

    var driverViewModel: RoadBookItemModel? = null

    constructor(driverViewModel: RoadBookItemModel) {
        this.driverViewModel = driverViewModel
    }

    var roadBookFirst: RoadBookFirstViewModel? = null

    constructor(roadBookFirst: RoadBookFirstViewModel) {
        this.roadBookFirst = roadBookFirst
    }

    fun click(view: HoriDatas) {
        var index = horiDatas.indexOf(view)
        if (!view.isCheck.get()!!) {
            lastView?.isCheck?.set(false)
            view.isCheck.set(true)
            lastView = view
            if (index == 0) {
                if (driverViewModel != null) {
                    if (driverViewModel!!.viewModel?.currentPosition == 0) {

                    } else if (driverViewModel!!.viewModel?.currentPosition == 2) {
                        driverViewModel?.items!!.clear()
                        driverViewModel?.createMarker(allData!!)
                        driverViewModel?.bottomVisible!!.set(false)
                        driverViewModel?.behavior_by_routes!!.set(BottomSheetBehavior.STATE_COLLAPSED)
                    }
                } else {
                    roadBookFirst!!.items!!.clear()
                    roadBookFirst!!.createMarker(allData!!)
                    roadBookFirst!!.bottomVisible!!.set(false)
                    roadBookFirst!!.activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            } else {
                if (driverViewModel != null) {
                    if (driverViewModel?.viewModel?.currentPosition == 0) {

                    } else if (driverViewModel?.viewModel?.currentPosition == 2) {
                        driverViewModel!!.items!!.clear()
                        var li = this!!.allData!![index - 1]
                        driverViewModel!!.createMarker(li)
                        driverViewModel?.title!!.set("DAY$index")
                        driverViewModel?.pointAddress!!.set(li.allRoutepoint)
                        var t = DecimalFormat("0.0").format(li.aboutdis / 1000) + "km"
                        var k = ConvertUtils.duration2Min(li.ridingtime.toInt())
                        driverViewModel?.distance_andtime!!.set("距离" + t + "  " + "预计" + k)
                        driverViewModel?.discribe!!.set(li.simpIntroduction)
                        driverViewModel?.id!!.set(li.id.toString())
                        driverViewModel?.bottomVisible!!.set(true)
                        driverViewModel?.behavior_by_routes!!.set(BottomSheetBehavior.STATE_HIDDEN)
                    }
                } else {
                    roadBookFirst!!.items!!.clear()
                    var li = this!!.allData!![index - 1]
                    roadBookFirst!!.createMarker(li)
                    roadBookFirst!!.title!!.set("DAY$index")
                    roadBookFirst!!.pointAddress!!.set(li.allRoutepoint)
                    var t = DecimalFormat("0.0").format(li.aboutdis / 1000) + "km"
                    var k = ConvertUtils.duration2Min(li.ridingtime.toInt())
                    roadBookFirst!!.distance_andtime.set("距离" + t + "  " + "预计" + k)
                    roadBookFirst!!.discribe!!.set(li.simpIntroduction)
                    roadBookFirst!!.id!!.set(li.id.toString())
                    roadBookFirst!!.bottomVisible!!.set(true)
                    roadBookFirst!!.activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        } else {
            if (index != 0) {
                if (driverViewModel != null) {
                    if (driverViewModel!!.viewModel?.currentPosition == 0) {
                    } else if (driverViewModel!!.viewModel?.currentPosition == 2) {
                        driverViewModel?.items!!.clear()
                        var li = this!!.allData!![index - 1]
                        driverViewModel?.createMarker(li)
                        driverViewModel?.title!!.set("DAY$index")
                        driverViewModel?.pointAddress!!.set(li.allRoutepoint)
                        var t = DecimalFormat("0.0").format(li.aboutdis / 1000) + "km"
                        var k = ConvertUtils.duration2Min(li.ridingtime.toInt())
                        driverViewModel!!.distance_andtime.set("距离" + t + "  " + "预计" + k)
                        driverViewModel!!.discribe!!.set(li.simpIntroduction)
                        driverViewModel?.id!!.set(li.id.toString())
                        driverViewModel?.bottomVisible!!.set(true)
                        driverViewModel?.behavior_by_routes!!.set(BottomSheetBehavior.STATE_HIDDEN)
                    }
                } else {
                    roadBookFirst!!.items!!.clear()
                    var li = this!!.allData!![index - 1]
                    roadBookFirst!!.createMarker(li)
                    roadBookFirst!!.title!!.set("DAY$index")
                    var t = DecimalFormat("0.0").format(li.aboutdis / 1000) + "km"
                    var k = ConvertUtils.duration2Min(li.ridingtime.toInt())
                    roadBookFirst!!.distance_andtime.set("距离" + t + "  " + "预计" + k)
                    roadBookFirst!!.pointAddress!!.set(li.allRoutepoint)
                    roadBookFirst!!.discribe!!.set(li.simpIntroduction)
                    roadBookFirst!!.id!!.set(li.id.toString())
                    roadBookFirst!!.bottomVisible!!.set(true)
                    roadBookFirst!!.activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    fun horiClick(view: HoriDatas) {
        click(view)
    }

    fun detalClick(entity: RoadBookDistanceEntity) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_WEB_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_WEB_TYPE, 2).withString(RouterUtils.MapModuleConfig.ROAD_WEB_ID, entity.id.get()).navigation()
    }

    var megerList = MergeObservableList<Any>()

    var adapter = BindingRecyclerViewAdapter<Any>()

    var secondAdapter = BindingRecyclerViewAdapter<Any>()

    var itemsbinding = OnItemBindClass<Any>()
            .map(RoadBookDistanceEntity::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.road_book_one_entity, R.layout.roadbook_one_layout).bindExtra(BR.simple_model, this@SimpleItemRecycleComponent)
            }
            .map(RoadBookListEntity::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.road_book_two_entity, R.layout.roadbook_two_layout).bindExtra(BR.re_child_road_book_model, this).bindExtra(BR.position, position)
            }
            .map(RoadBookRecycleEntity::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.road_book_three_entity, R.layout.roadbook_three_layout).bindExtra(BR.child_road_book_model, this)
            }

    var childAdapter = BindingRecyclerViewAdapter<Any>()
    var childItems = MergeObservableList<Any>()
    var childBinding = OnItemBindClass<Any>()
            .map(RoadBookRecycleOneEntity::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.road_book_recycle_one_entity, R.layout.roadbook_recycle_one_layout)
            }.map(RoadBookRecycleFourEntity::class.java) { itemBinding, position, item ->
                itemBinding.set(BR.road_book_recycle_four_entity, R.layout.roadbook_recycle_four_layout)
            }.map(RoadBookRecycleTwoEntity::class.java) { itemBinding, position, item ->
                if (position == 0 || position % 2 == 0) {
                    itemBinding.set(BR.road_book_recycle_two_entity, R.layout.roadbook_recycle_two_layout)
                } else {
                    itemBinding.set(BR.road_book_recycle_three_entity, R.layout.roadbook_recycle_three_layout)
                }
            }
    var lastView: HoriDatas? = null

    var horiDatas = ObservableArrayList<HoriDatas>()


    fun itemClickModel(list: RoadBookListEntity, position: Int) {
        changeIndex(position - 1, this!!.allData!![position - 1], false)
    }

//    private fun initHori(linear: LinearLayout, type: Int) {
//        linear.removeAllViews()
//        horiDatas.forEachIndexed { index, horiDatas ->
//            var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            var binding: ViewDataBinding
//            if (type == 0) {
//                binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.roadbook_hori_item_layout, linear, false)
//            } else {
//                binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.roadbook_hori_item_layout_type, linear, false)
//            }
//            binding.setVariable(BR.horiDatas, horiDatas)
//            binding.setVariable(BR.road_model, this)
//            if (index == 0) {
//                horiDatas.isCheck.set(true)
//                lastView = horiDatas
//            }
//            linear.addView(binding.root)
//        }
//        linear.invalidate()
//    }


    var allData: ArrayList<RoadDetailEntity>? = null

    fun initDatas(linear: ArrayList<RoadDetailEntity>, data: HotData?, dex: Int) {
        Log.e("result","initData")
        this.allData = linear
        horiDatas.clear()
        childItems.removeAll()
        megerList.removeAll()
        CoroutineScope(uiContext).launch {
            delay(500)
            var lin = linear[dex]
            var distance = RoadBookDistanceEntity()
            distance.guideId.set(lin.id.toString())
            distance.id.set(lin.guideId.toString())
            distance.title.set(data?.title)
            distance.carType.set(data?.vehicletype)
            distance.seasonType.set(data?.season)
            distance.speType.set(data?.feature)
            distance.distance.set(DecimalFormat("0").format(data?.aboutdis!!.toInt() / 1000))
            distance.day_count.set(data.ridingtime.toString())
            if (roadBookFirst != null) {
                roadBookFirst!!.dayCount!!.set(data.ridingtime)
            } else {
                driverViewModel?.dayCount!!.set(data.ridingtime)
            }

            distance.pointCount.set(data!!.routepointcount!!.toString())
            distance.content.set(data?.simpIntroduction)
            var list = ObservableArrayList<RoadBookListEntity>()
            var d = HoriDatas()
            d.title.set("HOME")
            horiDatas.add(d)
            linear.forEachIndexed { index, roadDetailEntity ->
                var d = HoriDatas()
                d.title.set("D" + (index + 1))
                horiDatas.add(d)
                var e = RoadBookListEntity()
                e.type.set(3)
                e.title.set("D" + (index + 1))
                e.city.set(roadDetailEntity.allRoutepoint)
                list.add(e)
            }
            var recy = RoadBookRecycleEntity()

            megerList.insertItem(distance)
            megerList.insertList(list)
            megerList.insertItem(recy)

            changeIndex(dex, lin, false)
//            if (driverViewModel != null) {
//                if (driverViewModel?.mapActivity!!.getRoadBookFragment().isAdded) {
//                    initHori(driverViewModel?.mapActivity!!.getRoadBookFragment().road_book_hori_linear, 0)
//                }
//            } else {
//
//                initHori(roadBookFirst!!.activity?.road_book_first_hori_linear, 1)
//            }
        }
    }

    fun changeIndex(dex: Int, lin: RoadDetailEntity, boolean: Boolean): MergeObservableList<Any> {
        Log.e("result","changeIndex")
        if (!childItems.isEmpty()) {
            childItems.removeAll()
        }
        var one = RoadBookRecycleOneEntity()
        one.title.set("DAY" + (dex + 1))
        one.address.set(lin.allRoutepoint)
        one.addressContent.set(lin.simpIntroduction)
        var t = DecimalFormat("0.0").format(lin.aboutdis / 1000) + "km"
        var k = ConvertUtils.duration2Min(lin.ridingtime.toInt())
        one.driverTime.set("预计骑行： " + "$t  $k")
        var four = RoadBookRecycleFourEntity()
        four.title.set("DAY" + (dex + 1))
        var twolist = ObservableArrayList<RoadBookRecycleTwoEntity>()
        if (lin.pointList!!.size != 0) {
            if (lin.pointList!!.size == 1) {
                var item = lin.pointList!![0]
                var s = RoadBookRecycleTwoEntity()
                s.type.set(1)
                s.title.set(lin.pointList!![0].routeName)
                s.routeId.set(lin.pointList!![0].routeIcon)
                s.descripte.set(lin.pointList!![0].remake)
                s.imgUrl.set(getRoadImgUrl(lin.pointList!![0].imgUrl))
                twolist.add(s)
            } else {
                lin.pointList!!.forEachIndexed { index, roadDetailItem ->
                    if (roadDetailItem.aboutdis > 0) {
                        var s = RoadBookRecycleTwoEntity()
                        s.title.set(roadDetailItem.routeName)
                        s.routeId.set(roadDetailItem.routeIcon)
                        s.type.set(1)
                        s.descripte.set(roadDetailItem.remake)
                        s.imgUrl.set(getRoadImgUrl(roadDetailItem.imgUrl))
                        twolist.add(s)
                        var m = RoadBookRecycleTwoEntity()
                        var t = DecimalFormat("0.0").format(roadDetailItem?.aboutdis / 1000) + "km"
                        var k = ConvertUtils.duration2Min(roadDetailItem.ridingtime.toInt())
                        m.distanceTv.set("距离" + t + ";预计" + k)
                        m.type.set(0)
                        twolist.add(m)

                    } else {
                        var s = RoadBookRecycleTwoEntity()
                        s.type.set(1)
                        s.title.set(roadDetailItem.routeName)
                        s.routeId.set(roadDetailItem.routeIcon)
                        s.descripte.set(roadDetailItem.remake)
                        s.imgUrl.set(getRoadImgUrl(roadDetailItem.imgUrl))
                        twolist.add(s)
                    }
                }
            }
        }


//        linear.pointList!!.forEachIndexed { index, roadDetailItem ->
//            var s = RoadBookRecycleTwoEntity()
//            if ((index + 1) % 2 == 0) {
//                s.type.set(0)
//            } else {
//                s.type.set(1)
//            }
//            twolist.add(RoadBookRecycleTwoEntity())
//        }
        childItems.insertItem(one)
        childItems.insertItem(four)
        childItems.insertList(twolist)

        if (driverViewModel != null) {
            driverViewModel?.items!!.clear()
        } else {
            roadBookFirst!!.items.clear()
        }
        if (driverViewModel != null) {
            if (driverViewModel?.viewModel?.currentPosition == 2) {
                if (driverViewModel!!.behavior_by_routes.get() != BottomSheetBehavior.STATE_EXPANDED) {
                    CoroutineScope(uiContext).launch {
                        driverViewModel!!.behavior_by_routes.set(BottomSheetBehavior.STATE_COLLAPSED)
                        delay(500)
                        Log.e("result","STATE_COLLAPSED")
                        driverViewModel?.scrollPosition!!.set(0)
//                        driverViewModel?.mapActivity!!.getRoadBookFragment().roadbook_recycle.scrollToPosition(0)
                        if (!boolean) {
                            driverViewModel!!.createMarker(allData!!)
                        }
                    }
                }
            }
        } else {
            if (roadBookFirst!!.activity.behavior_by_routes!!.state != BottomSheetBehavior.STATE_EXPANDED) {
                CoroutineScope(uiContext).launch {
                    roadBookFirst!!.activity.behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
                    delay(1000)
                    roadBookFirst!!.activity.first_roadbook_recycle.scrollToPosition(0)
                    if (!boolean) {
                        roadBookFirst?.createMarker(allData!!)

                    }
                }
            }
        }
        return childItems
    }

    fun clear() {
        megerList.removeAll()
        childItems.removeAll()
    }
}