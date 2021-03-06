package com.cstec.administrator.social.ItemViewModel

import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.ReleaseDynamicsActivity
import com.cstec.administrator.social.Adapter.GridRecycleViewAdapter
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.Entity.GridClickEntity
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.SocialViewModel
import com.elder.zcommonmodule.DETAIL_RESULT
import com.elder.zcommonmodule.DataBases.queryUserInfo
import com.elder.zcommonmodule.Entity.DynamicsSimple
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.SocialHoriEntity
import com.elder.zcommonmodule.RELEASE_RESULT
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.RouterUtils.SocialConfig.Companion.SOCIAL_DETAIL_ENTITY
import com.zk.library.Utils.RouterUtils.SocialConfig.Companion.SOCIAL_LOCATION
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import kotlin.collections.HashMap


class SocialItemModel : ItemViewModel<SocialViewModel> {

    var type = 0

    constructor(type: Int, model: SocialViewModel) {
        this.viewModel = model
        this.type = type
        adapter = GridRecycleViewAdapter(context!!, items, this@SocialItemModel)
    }

    var items = ObservableArrayList<DynamicsCategoryEntity.Dynamics>()
    var adapter: GridRecycleViewAdapter

    var clickBinding = BindingCommand(object : BindingConsumer<GridClickEntity> {
        override fun call(t: GridClickEntity) {
            var list = ObservableArrayList<String>()
            t.urlList!!.forEach {
                list.add(it)
            }
            DialogUtils.createBigPicShow(viewModel.socialFragment.activity!!, list, t.childPosition)
        }
    })
    var pageSize = 1
    var page = 20


    override fun initDatas(t: Int) {
        super.initDatas(t)
        if (viewModel.location == null) {
            return
        }
        if (System.currentTimeMillis() - viewModel.CurrentClickTime < 1200) {

        } else {
            viewModel.CurrentClickTime = System.currentTimeMillis()
//            viewModel.refreshLayout.autoRefresh()
//            if (viewModel.progress == null) {
//                viewModel.progress = DialogUtils.showProgress(viewModel.socialFragment.activity!!, getString(R.string.http_load_social))
//            } else {
//                if (!viewModel.progress!!.isShowing) {
//                    viewModel.progress!!.show()
//                }
//            }
        }
        HttpRequest.instance.DynamicListResult = viewModel
        var map = HashMap<String, String>()
        map["pageSize"] = pageSize.toString()
        map["length"] = page.toString()
        map["yAxis"] = viewModel.location!!.longitude.toString()
        map["xAxis"] = viewModel.location!!.latitude.toString()
        map["type"] = (type + 1).toString()
        HttpRequest.instance.getDynamicsList(map)
    }

    var CurrentClickTime = 0L

    fun LikeClick(entiy: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        HttpRequest.instance.DynamicLikerListResult = viewModel
        var view = entiy
        var position = items.indexOf(view)
        var id = PreferenceUtils.getString(context, USERID)
        var user = queryUserInfo(id)[0]
        var m = Integer.valueOf(view.fabulousCount)
        if (view.isLike == 0) {
            view.isLike = 1
            var entity = SocialHoriEntity()
            entity.memberImages = user.data?.headImgFile
            entity.memberId = Integer.valueOf(user.data?.id)
            entity.memberName = user.data?.name
            entity.createDate = entiy.createDate
            entiy.dynamicSpotFabulousList!!.add(entity)
            m++
        } else {
            view.isLike = 0
            m--
            removeBeanSpot(entiy)
        }
        view.fabulousCount = m.toString()
        var map = HashMap<String, String>()
        map["dynamicId"] = view.id!!
        HttpRequest.instance.getDynamicsLike(map)
//        config.submitList(Arrays.asList(view))
//        diff.update(, diff.calculateDiff(Arrays.asList(view)))
        items[position] = view
        adapter.notifyItemChanged(position, "LikeClick")
    }

    fun storeClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        HttpRequest.instance.DynamicCollectionResult = viewModel
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
        var map = HashMap<String, String>()
        map["dynamicId"] = view.id!!
        HttpRequest.instance.getDynamicsCollection(map)
        items[position] = view
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "storeClick")
    }

    fun removeBeanSpot(detialBean: DynamicsCategoryEntity.Dynamics) {
        if (detialBean.dynamicSpotFabulousList.isNullOrEmpty()) {
            return
        }
        var member: SocialHoriEntity? = null
        detialBean.dynamicSpotFabulousList!!.forEach {
            if (it.memberId.toString() == PreferenceUtils.getString(context, USERID)) {
                member = it
            }
        }
        detialBean.dynamicSpotFabulousList!!.remove(member)
    }

    fun yelpClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }

        var bundle = Bundle()
        bundle.putSerializable(SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F,viewModel. location?.aoiName!!, viewModel.location!!.poiName))
        bundle.putSerializable(SOCIAL_DETAIL_ENTITY, view)
        var fr = viewModel?.socialFragment.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation() as BaseFragment<ViewDataBinding,BaseViewModel>
        model.arguments = bundle
        fr.startForResult(model, DETAIL_RESULT)

//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).withSerializable(SOCIAL_DETAIL_ENTITY, view).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName)).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation()
    }

    fun retransClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        if (viewModel.location == null) {
            Toast.makeText(context, "获取定位信息异常，请重试!", Toast.LENGTH_SHORT).show()
            return
        }
        var bundle = Bundle()
        bundle.putSerializable(SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F,viewModel. location?.aoiName!!, viewModel.location!!.poiName))
        bundle.putSerializable(SOCIAL_DETAIL_ENTITY, view)
        var fr = viewModel?.socialFragment.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).navigation() as ReleaseDynamicsActivity
        model.arguments = bundle
        fr.startForResult(model, RELEASE_RESULT)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).withSerializable(SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName)).withSerializable(SOCIAL_DETAIL_ENTITY, view).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 2).navigation()
    }


    var deleteItem: DynamicsCategoryEntity.Dynamics? = null
    fun deleteClick(view: DynamicsCategoryEntity.Dynamics) {
        var dialog = DialogUtils.createNomalDialog(viewModel.socialFragment.activity!!, getString(R.string.delete_social), getString(R.string.cancle), getString(R.string.confirm))
        dialog.setOnBtnClickL(OnBtnClickL {
            dialog.dismiss()
        }, OnBtnClickL {
            this.deleteItem = view
            var map = HashMap<String, String>()
            map["id"] = view.id.toString()
            HttpRequest.instance.deleteSocialResult = viewModel
            HttpRequest.instance.deleteSocial(map)
            dialog.dismiss()
        })
        dialog.show()
    }

    fun avatarClick(view: DynamicsCategoryEntity.Dynamics) {


        var fr = viewModel?.socialFragment.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION,  Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName))
        bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, view.memberId)
        var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
        model.arguments = bundle
        fr.start(model)

//        ARouter.getInstance()
//                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName))
//                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, view.memberId)
//                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
//                .navigation()



        Log.e("result", "avatarClick" + view.memberId)
    }


    var focusClickPosition = 0
    fun FocusClick(view: DynamicsCategoryEntity.Dynamics) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }
        HttpRequest.instance.DynamicFocusResult = viewModel
        focusClickPosition = items.indexOf(view)
        var map = HashMap<String, String>()
        map["fansMemberId"] = view.memberId.toString()
        HttpRequest.instance.getDynamicsFocus(map)
    }

    var curLoad = 0
    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("resulttttttttt", "加载更多" + t)
            if (t > curLoad) {
                pageSize++
                viewModel?.items[viewModel?.socialFragment.SocialViewPger!!.currentItem].initDatas(0)
                curLoad = t
            }
        }
    })


    var spanclick = BindingCommand(object : BindingConsumer<DynamicsSimple> {
        override fun call(t: DynamicsSimple) {

            var fr = viewModel?.socialFragment.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
            var bundle = Bundle()
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION,  Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName))
            bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID,t.memberId)
            var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
            model.arguments = bundle
            fr.start(model)
//            ARouter.getInstance()
//                    .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
//                    .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(viewModel.location?.latitude!!, viewModel.location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, viewModel.location?.aoiName!!, viewModel.location!!.poiName))
//                    .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, t.memberId)
//                    .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0)
//                    .navigation()

        }
    })
}