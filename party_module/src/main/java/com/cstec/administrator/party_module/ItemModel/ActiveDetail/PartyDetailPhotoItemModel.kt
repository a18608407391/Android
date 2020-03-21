package com.cstec.administrator.party_module.ItemModel.ActiveDetail

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.cstec.administrator.party_module.Adapter.PhotoAdapter
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.elder.zcommonmodule.Entity.PhotoEntitiy
import com.zk.library.Base.ItemViewModel
import com.cstec.administrator.party_module.BR
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Entity.PhotoBean
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getImageUrl
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.SimpleDateFormat
import java.util.*

class PartyDetailPhotoItemModel : BasePartyItemModel(), HttpInteface.PartyAlbum_inf {
    override fun PartyAlbumSucccess(it: String) {
        var result = Gson().fromJson<PhotoBean.ResultPhoto>(it, PhotoBean.ResultPhoto::class.java)
        if (result != null && !result.data.isNullOrEmpty()) {
            var count = 0
            result?.data?.forEachIndexed { index, photo ->
                var url = getImageUrl(photo.filePath!!.split("/home/uploadFile/images")[1] + "/" + photo.fileNameUrl)

                Log.e("result", "图片链接" + url)
                if (tes != photo.createDate) {
                    tes = photo.createDate!!
                    items.add(PhotoEntitiy(ObservableField(""), ObservableField(photo.createDate!!), ObservableField(0)))
                    items.add(PhotoEntitiy(ObservableField(url), ObservableField(photo.createDate!!), ObservableField(1), ObservableField(photo.basicsId.toString())))
                } else {
                    items.add(PhotoEntitiy(ObservableField(url), ObservableField(photo.createDate!!), ObservableField(1), ObservableField(photo.basicsId.toString())))
                }
            }
            var t = items.size - count
            totalCount.set("/" + pageSize * start)
        }

        if (items.isNullOrEmpty()) {
            isEmpty.set(true)
        } else {
            isEmpty.set(false)
        }
    }

    var checkPositionList = ArrayList<Int>()
    var isEmpty = ObservableField<Boolean>(false)
    var totalCount = ObservableField<String>("/100")
    var tes = ""
    override fun PartyAlbumError(it: Throwable) {

    }

    var adapter = PhotoAdapter()

    var items = ObservableArrayList<PhotoEntitiy>()

    var itemBinding = ItemBinding.of<PhotoEntitiy>() { itemBinding, position, item ->
        if (item.getItemType() == 0) {
            itemBinding.set(BR.title, R.layout.party_detail_title_item)
        } else if (item.getItemType() == 1) {
            itemBinding.set(BR.img_item, R.layout.party_detail_img_item).bindExtra(BR.photo_click, this@PartyDetailPhotoItemModel)
        }
    }

    fun onAdapterItemClick(item: PhotoEntitiy) {
        var list = ObservableArrayList<String>()
        items.forEach {
            if (!it.path.get().isNullOrEmpty()) {
                list.add(it.path.get())
            }
        }
        var index = list.indexOf(item.path.get())
        DialogUtils.createBigPicShow(mActivity!!, list, index)
    }

    var partyid = 0
    fun setPartyId(partyId: Int): PartyDetailPhotoItemModel {
        this.partyid = partyId
        return this
    }


    lateinit var mActivity: FragmentActivity
    fun setActivity(activity: FragmentActivity): PartyDetailPhotoItemModel {
        this.mActivity = activity
        return this@PartyDetailPhotoItemModel
    }

    fun initData(flag: Boolean) {
        if (flag) {
            start = 1
            pageSize = 30
            curLoad = 30
            tes = ""
            checkPositionList.clear()
            items.clear()
        }
        HttpRequest.instance.partyAlarm = this
        var map = HashMap<String, String>()
        map["id"] = partyid.toString()
        map["start"] = "1"
        map["pageSize"] = "30"
        HttpRequest.instance.getPartyAlumb(map)
    }

    var start = 1
    var pageSize = 30
    var curLoad = 30

    var onLoadMoreCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t >= curLoad) {
                start++
                initData(false)
                curLoad = start * pageSize
            }
        }
    })
}