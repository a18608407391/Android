package com.cstec.administrator.social.ItemViewModel

import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.ReleaseDynamicsActivity
import com.cstec.administrator.social.Adapter.GridHomeRecycleViewAdapter
import com.cstec.administrator.social.Entity.GridClickEntity
import com.cstec.administrator.social.Inteface.DynamicClickListener
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.*
import com.elder.zcommonmodule.RELEASE_RESULT
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class CavalierDynamicItem : CavalierItemModel, DynamicClickListener, HttpInteface.SocialDynamicsList, HttpInteface.deleteSocialResult {
    override fun deleteSocialSuccess(it: String) {
        if (deleteItem != null) {
            items.remove(deleteItem)
            adapter.initDatas(items)
        }
        Toast.makeText(context, getString(R.string.social_delete_success), Toast.LENGTH_SHORT).show()
    }

    override fun deleteSocialError(it: Throwable) {
        Toast.makeText(context, getString(R.string.social_delete_fail), Toast.LENGTH_SHORT).show()
    }

    override fun deleteClick(view: DynamicsCategoryEntity.Dynamics) {
        var dialog = DialogUtils.createNomalDialog(viewModel.activity.activity!!, getString(R.string.delete_social), getString(R.string.cancle), getString(R.string.confirm))
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            this.deleteItem = view
            var map = HashMap<String, String>()
            map["id"] = view.id.toString()
            HttpRequest.instance.deleteSocialResult = this
            HttpRequest.instance.deleteSocial(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    var deleteItem: DynamicsCategoryEntity.Dynamics? = null

    override fun spanclick(): BindingCommand<DynamicsSimple> {
        var spanclick = BindingCommand(object : BindingConsumer<DynamicsSimple> {
            override fun call(t: DynamicsSimple) {

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel?.activity.location)
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
                var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                model.arguments = bundle
                viewModel.activity.start(model)
//                ARouter.getInstance()
//                        .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(viewModel.activity.location?.latitude!!, viewModel.activity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.activity.location?.aoiName!!, viewModel.activity.location!!.poiName))
//                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
//                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
//                        .navigation()
            }
        })
        return spanclick
    }

    override fun ResultSDListSuccess(it: String) {
//        if (!items.isEmpty()) {
//            items.clear()
//        }

        Log.e("result", "但是下次打算分手" + it)

        if (pageSize == 1) {
            items.clear()
        }

        var entity = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
        if (entity.data!!.size == 0) {
            return
        }
        entity.data?.forEach {
            items.add(it)
        }

        Log.e("result", entity.data!!.size.toString() + "当前数据条数" + items.size)
        adapter.initDatas(items)
    }

    override fun ResultSDListError(ex: Throwable) {

    }

    override fun bindingCommand(): BindingCommand<GridClickEntity> {
        var clickBinding = BindingCommand(object : BindingConsumer<GridClickEntity> {
            override fun call(t: GridClickEntity) {
                var list = ObservableArrayList<String>()
                t.urlList!!.forEach {
                    list.add(it)
                }
                DialogUtils.createBigPicShow(viewModel.activity!!.activity!!, list, t.childPosition)
            }
        })
        return clickBinding
    }

    var CurrentClickTime = 0L
    override fun LikeClick(entiy: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }

        var view = entiy
        var position = items.indexOf(view)
        var m = Integer.valueOf(view.fabulousCount)
        if (view.isLike == 0) {
            view.isLike = 1
            view = addBeanSpot(entiy)
            m++
        } else {
            view.isLike = 0
            view = removeBeanSpot(entiy)
            m--
        }
        view.fabulousCount = m.toString()
        var map = java.util.HashMap<String, String>()
        map["dynamicId"] = view.id!!

        HttpRequest.instance.getDynamicsLike(map)
//        config.submitList(Arrays.asList(view))
//        diff.update(, diff.calculateDiff(Arrays.asList(view)))
        items[position] = view
        adapter.notifyItemChanged(position, "LikeClick")
    }

    override fun storeClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        var position = items.indexOf(view)
        var m = Integer.valueOf(view.collectionCount)
        if (view.hasCollection == 0) {
            view.hasCollection = 1
            m++
        } else {
            view.hasCollection = 0
            m--
        }
        view.collectionCount = m.toString()
        var map = java.util.HashMap<String, String>()
        map["dynamicId"] = view.id!!
        HttpRequest.instance.getDynamicsCollection(map)
        items[position] = view
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "storeClick")
    }

    fun addBeanSpot(entiy: DynamicsCategoryEntity.Dynamics): DynamicsCategoryEntity.Dynamics {
        var entity = SocialHoriEntity()
        entity.memberImages = user!!.data?.headImgFile
        entity.memberId = Integer.valueOf(user!!.data?.id)
        entity.memberName = user!!.data?.name
        entity.createDate = entiy.createDate
        entiy.dynamicSpotFabulousList!!.add(entity)
        return entiy
    }

    fun removeBeanSpot(entiy: DynamicsCategoryEntity.Dynamics): DynamicsCategoryEntity.Dynamics {
        if (entiy.dynamicSpotFabulousList.isNullOrEmpty()) {
            return null!!
        }
        var member: SocialHoriEntity? = null

        entiy.dynamicSpotFabulousList!!.forEach {
            if (it.memberId.toString() == user!!.data?.id) {
                member = it
            }
        }
        entiy.dynamicSpotFabulousList!!.remove(member)
        return entiy
    }

    override fun yelpClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(viewModel.activity.location?.latitude!!, viewModel.activity.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.activity.location?.aoiName!!, viewModel.activity.location!!.poiName))
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        viewModel?.activity.startForResult(model, DETAIL_RESULT)

//        RxBus.default?.postSticky(view)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel?.activity.location).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 3).navigation()
    }

    override fun retransClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        if (viewModel.activity.location == null) {
            Toast.makeText(context, "获取定位信息异常，请重试!", Toast.LENGTH_SHORT).show()
            return
        }
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel.activity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).navigation() as ReleaseDynamicsActivity
        model.arguments = bundle
        viewModel.activity.startForResult(model, RELEASE_RESULT)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel.activity.location).withSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY, view).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 5).navigation()
    }

    override fun avatarClick(view: DynamicsCategoryEntity.Dynamics) {
//        var fr = viewModel?.socialFragment.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, viewModel?.activity.location)
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, view.memberId)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        viewModel.activity.start(model)
    }

    override fun FocusClick(view: DynamicsCategoryEntity.Dynamics) {
    }

    fun init() {
        var map = HashMap<String, String>()
        map["pageSize"] = pageSize.toString()
        map["length"] = length.toString()
        map["memberId"] = viewModel.activity.id!!
        map["type"] = "4"
        map["yAxis"] = viewModel.activity.location?.longitude.toString()
        map["xAxis"] = viewModel.activity.location?.latitude.toString()
        Log.e("result", "收到收到收到" + Gson().toJson(map))
        HttpRequest.instance.getDynamicsList(map)
    }

    var pageSize = 1
    var length = 10
    var items = ObservableArrayList<DynamicsCategoryEntity.Dynamics>()
    var adapter: GridHomeRecycleViewAdapter
    var user: UserInfo

    constructor(model: DriverHomeViewModel) {
        this.viewModel = model
        var id = PreferenceUtils.getString(context, USERID)
        user = queryUserInfo(id)[0]
        HttpRequest.instance.DynamicListResult = this
        adapter = GridHomeRecycleViewAdapter(context, items, this@CavalierDynamicItem)

    }

    var listener: DynamicClickListener = this
    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t < length * pageSize) {
                return
            } else {
                pageSize++
                init()
            }
//            if (t > curLoad) {
//                pageSize++
//                init()
//                curLoad = t
//            }
        }
    })

}