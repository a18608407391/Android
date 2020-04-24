package com.cstec.administrator.social.ViewModel

import android.app.AlertDialog
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.cstec.administrator.social.Activity.DynamicsPhotoActivity
import com.cstec.administrator.social.Activity.ReleaseDynamicsActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.Entity.NearlyAdressEntity
import com.elder.zcommonmodule.Entity.SocialPhotoEntity
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.RELEASE_RESULT
import com.elder.zcommonmodule.SOCIAL_SELECT_PHOTOS
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_releasedynamics.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.io.File
import java.io.IOException


class ReleaseDynamicsViewModel : BaseViewModel(), DialogUtils.Companion.IconUriCallBack, TitleComponent.titleComponentCallBack, HttpInteface.SocialUploadPhoto, HttpInteface.SocialReleaseDynamics, DialogUtils.Companion.opitionCallBack, PoiSearch.OnPoiSearchListener {
    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

    }

    override fun onPoiSearched(result: PoiResult?, p1: Int) {

        bottomItems.clear()
        var nea = NearlyAdressEntity()
        nea.name = "不显示位置"
        nea.lat = activity.location?.latitude!!
        nea.lon = activity.location?.longitude!!
        bottomItems.add(nea)
        result!!.pois.forEach {
            var info = NearlyAdressEntity()
            info.name = it.title
            info.address = it.snippet
            info.lat = it.latLonPoint.latitude
            info.lon = it.latLonPoint.longitude
            bottomItems.add(info)
        }
        activity.behaviors.state = BottomSheetBehavior.STATE_EXPANDED
    }

    var publickState = 1


    var publicStr = ObservableField<String>("公开")
    override fun getType(type: Int) {
        this.publickState = type
        if (type == 1) {
            publicStr.set("公开")
        } else {
            publicStr.set("仅自己可见")
        }
    }

    override fun ResultReleaseDynamicsSuccess(it: String) {
        activity.dismissProgressDialog()
        var bundle = Bundle()
        bundle.putBoolean("ResultReleaseDynamicsSuccess",true)
        activity.setFragmentResult(RELEASE_RESULT,bundle)
        Toast.makeText(context, getString(R.string.send_dynamics_success), Toast.LENGTH_SHORT).show()
        activity._mActivity!!.onBackPressedSupport()
    }

    override fun ResultReleaseDynamicsError(ex: Throwable) {
        activity.dismissProgressDialog()
    }

    override fun postPhotoSuccess(it: String) {
//       var resp =  Gson().fromJson<BaseResponse>(it,BaseResponse::class.java)
        var list = Gson().fromJson<ArrayList<SocialPhotoEntity>>(it, object : TypeToken<ArrayList<SocialPhotoEntity>>() {}.type)
        items.forEachIndexed { index, s ->
            if (!s.isNullOrEmpty()) {
                list.forEachIndexed { dex, t ->
                    if (s.endsWith(t.fileNameUrl!!)) {
                        var f = BitmapFactory.decodeFile(s)
                        list[dex].singleHeight = f.height
                        list[dex].singleWidth = f.width
                    }
                }
            }
        }
        sendNoPhoto(list)
    }

    fun sendNoPhoto(list: ArrayList<SocialPhotoEntity>) {
        var map = HashMap<String, Any>()
        if (layoutType.get() == 1) {
            map["type"] = activity.entity?.type!!
            if (activity.entity?.parentDynaminId.isNullOrEmpty()) {
                map["parentDynaminId"] = activity.entity?.id.toString()
            } else {
                map["parentDynaminId"] = activity.entity?.parentDynaminId.toString()
            }
        } else {
            map["type"] = "1"
        }
        if (locationIcon.get()!!) {
            map["releaseAddress"] = locationAddress.get()!!
        } else {
            map["releaseAddress"] = ""
        }
        map["publishContent"] = activity.et.text.toString().trim()
        map["yAxis"] = activity.location?.longitude.toString()
        map["xAxis"] = activity.location?.latitude.toString()
        map["state"] = publickState.toString()
        map["saveAlbum"] = saveAlbum.toString()
        map["dynamicImageList"] = list
        var str = ""
        activity.et.realUserList.forEachIndexed { index, userModel ->
            if (activity.et.realUserList.size - 1 == index) {
                str += userModel.user_id
            } else {
                str += userModel.user_id + ","
            }
        }

        map["mentionList"] = str
        HttpRequest.instance.postReleaseDynamics(map)
    }

    var saveAlbum = 1

    var onCheckedChangedCommand = BindingCommand(object : BindingConsumer<Boolean> {
        override fun call(t: Boolean) {
            if (t) {
                saveAlbum = 1
            } else {
                saveAlbum = 0
            }
        }
    })

    override fun postPhotoError(ex: Throwable) {
    }

    override fun onComponentClick(view: View) {
        if (activity.et.text!!.isEmpty() && items.size == 1) {
            activity._mActivity!!.onBackPressedSupport()
        } else {
            var dialog = DialogUtils.createNomalDialog(activity.activity!!, getString(R.string.exit_edit), getString(R.string.cancle), getString(R.string.confirm))
            dialog.setOnBtnClickL(OnBtnClickL {
                dialog.dismiss()
            }, OnBtnClickL {
                activity._mActivity!!.onBackPressedSupport()
            })
            dialog.show()
        }

    }

    override fun onComponentFinish(view: View) {
        if (activity.location == null) {
            Toast.makeText(context, "正在获取当前定位，请稍后......", Toast.LENGTH_SHORT).show()
            return
        }
        if (items.size == 1 && activity.et.realText.length < 10) {
            Toast.makeText(context, "请输入大于等于10个字的内容", Toast.LENGTH_SHORT).show()
            return
        }
        HttpRequest.instance.postPhotoResult = this

        HttpRequest.instance.resultReleaseDynamics = this
        activity.showProgressDialog("正在发布动态中......")
        if (layoutType.get() == 0) {
            if (items.size > 1) {
                var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
                items.forEach {
                    if (!it.isNullOrEmpty()) {
                        mult.addFormDataPart("files", File(it).name, RequestBody.create(MediaType.parse("image/jpg"), File(it)))
                    }
                }
                HttpRequest.instance.postMuiltyPhoto(mult.build())


            } else {
                sendNoPhoto(ArrayList())
            }
        } else {
            sendNoPhoto(ArrayList())
        }
    }

    override fun getIcon(iconUri: Uri) {
        realPath = iconUri
    }

    var adapter = BindingRecyclerViewAdapter<String>()

    var items = ObservableArrayList<String>().apply {
        this.add("")
    }
    var itemBinding = ItemBinding.of<String> { itemBinding, position, item ->
        itemBinding.set(BR.release_dynaimg, R.layout.release_dyna_image_items).bindExtra(BR.extra_model, this@ReleaseDynamicsViewModel).bindExtra(BR.position, position)
    }

    var realPath: Uri? = null

    var locationIcon = ObservableField<Boolean>(false)

    var layoutType = ObservableField(0)

    var titleCommand = TitleComponent()
    fun openPhotos() {
        DialogUtils.showFragmentAnim(this.activity!!, 10 - items.size)
    }

    fun startPhotos() {
        if (!items.contains("") && items.size == 9) {
            Toast.makeText(context, getString(R.string.photo_max), Toast.LENGTH_SHORT).show()
            return
        }
        var bundler = Bundle()
        bundler.putInt(RouterUtils.SocialConfig.SOCIAL_MAX_COUNT, 10 - items.size)
        var fr = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_PHOTO).navigation() as DynamicsPhotoActivity
        fr.arguments = bundler
        activity.startForResult(fr, SOCIAL_SELECT_PHOTOS)
//        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_PHOTO).withInt(RouterUtils.SocialConfig.SOCIAL_MAX_COUNT, 10 - items.size).navigation(activity, SOCIAL_SELECT_PHOTOS)
    }

    fun startCamera() {
        realPath = DialogUtils.startCameraFragment(activity)
    }


    var transImage = ObservableField<String>()

    var transTitle = ObservableField<String>()

    var transDesc = ObservableField<String>()


    var locationAddress = ObservableField<String>("不显示位置")
    lateinit var activity: ReleaseDynamicsActivity
    fun inject(releaseDynamicsActivity: ReleaseDynamicsActivity) {
        this.activity = releaseDynamicsActivity
//        Log.e("result", "当前的动态" + Gson().toJson(this.activity.entity))
        if (this.activity.entity != null) {
            if (activity.entity?.parentDynamin != null) {
//                Log.e("result", "含父级动态" + activity.entity?.parentDynamin!!.publishContent)
                transTitle.set(activity?.entity!!.parentDynamin?.memberName + " 发布的动态")
                transDesc.set(activity.entity?.parentDynamin!!.publishContent)
                if (!activity.entity?.parentDynamin!!.dynamicImageList.isNullOrEmpty()) {
                    var img = activity.entity?.parentDynamin!!.dynamicImageList!![0]
                    transImage.set(Base_URL + img.projectUrl + img.filePathUrl + img.filePath)
                }
            } else {
//                Log.e("result", "不含父级动态" + activity.entity!!.publishContent)
                transTitle.set(activity.entity?.memberName + " 发布的动态")
                transDesc.set(activity.entity?.publishContent)
                if (!activity.entity?.dynamicImageList.isNullOrEmpty()) {
                    var img = activity.entity?.dynamicImageList!![0]
                    transImage.set(Base_URL + img.projectUrl + img.filePathUrl + img.filePath)
                }
            }
            layoutType.set(1)
        }
        DialogUtils.lisentner = this
        titleCommand.title.set(getString(R.string.release_dynamics))
        titleCommand.rightText.set("发布")
        titleCommand.arrowVisible.set(false)
        titleCommand.callback = this
        DialogUtils.selectType = this

    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.aite_click -> {
                activity.et.append("@")
            }
            R.id.camera_click -> {
                startCamera()
            }
            R.id.up_pic_click -> {
                startPhotos()
            }
            R.id.public_click -> {
                DialogUtils.showAnimSelect(activity.activity!!)
            }
            R.id.location_click -> {
                var poi = PoiSearch.Query("", "小区", "")
                poi.pageSize = 10
                var search = PoiSearch(activity.activity, poi)
                var bond = PoiSearch.SearchBound(LatLonPoint(activity.location!!.latitude, activity.location!!.longitude), 1000)
                search.bound = bond
                search.setOnPoiSearchListener(this)
                search.searchPOIAsyn()
            }
            R.id.bottom_close -> {
                activity.behaviors.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


    fun ImageDelete(url: String, position: Int) {
        if (!url.isEmpty()) {
            if (items.size == 9) {
                items.remove(url)
                if (!items.contains("")) {
                    items.add(8, "")
                }
            } else {
                items.remove(url)
            }
        }


//
//        if (!url.isEmpty()) {
//            items.remove(url)
//            if (items.size == 8) {
//                items.add(8, "")
//            }
//        }

    }


    var alertDialog: AlertDialog? = null

    fun ImageShowBig(url: String, position: Int) {
        if (url.isEmpty()) {
            openPhotos()
        } else {
            alertDialog = DialogUtils.createBigPicShow(activity.activity!!, items, position)
        }
    }

    var bottomAdapter = BindingRecyclerViewAdapter<NearlyAdressEntity>()
    var bottomItems = ObservableArrayList<NearlyAdressEntity>()
    var bottomItemBinding = ItemBinding.of<NearlyAdressEntity> { itemBinding, position, item ->
        itemBinding.set(BR.social_bottom_item, R.layout.social_bottom).bindExtra(BR.release_model, this@ReleaseDynamicsViewModel)
    }


    fun bottom_item_click(en: NearlyAdressEntity) {
        if (en.address == null) {
            locationAddress.set("不显示位置")
            locationIcon.set(false)
        } else {
            locationIcon.set(true)
            locationAddress.set(en.name)
        }
        activity.location!!.latitude = en.lat
        activity.location!!.longitude = en.lon
        activity.behaviors.state = BottomSheetBehavior.STATE_HIDDEN
    }
}