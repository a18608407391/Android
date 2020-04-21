package com.cstec.administrator.social.ItemViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.View
import com.cstec.administrator.social.Adapter.PhotoAdapter
import com.elder.zcommonmodule.Entity.PhotoEntitiy
import com.cstec.administrator.social.R
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.cstec.administrator.social.BR
import com.elder.zcommonmodule.Entity.PhotoBean
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Http.BaseObserver
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.getImageUrl
import com.google.gson.Gson
import com.zk.library.Utils.PreferenceUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CavalierPhotoItem : CavalierItemModel {


    constructor(driverHomeViewModel: DriverHomeViewModel) {
        this.viewModel = driverHomeViewModel
    }

    var adapter = PhotoAdapter(this)
    var items = ObservableArrayList<PhotoEntitiy>()
    var itemBinding = ItemBinding.of<PhotoEntitiy> { itemBinding, position, item ->
        if (item.getItemType() == 0) {
            itemBinding.set(BR.title, R.layout.my_photo_title_item)
        } else if (item.getItemType() == 1) {
            itemBinding.set(BR.item, R.layout.my_photo_item).bindExtra(BR.album_click, this@CavalierPhotoItem).bindExtra(BR.img_position, position)
        }
    }

    var viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> HomeViewHolder(binding.root) }
    var isLongClick = ObservableField<Boolean>(false)

    private class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun onAdapterItemClick(item: PhotoEntitiy, position: Int) {
        var list = ObservableArrayList<String>()
        items.forEach {
            if (!it.path.get().isNullOrEmpty()) {
                list.add(it.path.get())
            }
        }
        var index = list.indexOf(item.path.get())
        DialogUtils.createBigPicShow(viewModel.activity!!.activity!!, list, index)
    }

    fun init() {
        getAllPhoto(true)
    }

    var longCommand = BindingCommand(object : BindingConsumer<PhotoEntitiy> {
        override fun call(t: PhotoEntitiy) {

        }
    })


    var curLoad = 0

    var onLoadMoreCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t >= curLoad) {
                page++
                limit = 30
                getAllPhoto(false)
                curLoad = page*30
            }
        }
    })


    var checkPositionList = ArrayList<Int>()
    var page: Int = 1
    var limit: Int = 30
    var tes = ""
    var isEmpty = ObservableField<Boolean>(false)
    var totalCount = ObservableField<String>("/100")
    fun getAllPhoto(refresh: Boolean) {
        if (refresh) {
            page = 1
            limit = 30
            curLoad = 30
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        viewModel.activity.showProgressDialog(getString(R.string.get_photo_info))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["page"] = page.toString()
            map["limit"] = limit.toString()
            map["memberId"] = viewModel.activity?.id.toString()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryUserPicByUserId").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(viewModel.activity.activity) {
            override fun onNext(t: String) {
                super.onNext(t)
                viewModel.activity.dismissProgressDialog()
                var result = Gson().fromJson<PhotoBean>(t, PhotoBean::class.java)
                if (refresh) {
                    tes = ""
                    checkPositionList.clear()
//                    activity.Swiperefresh.isRefreshing = false
                    items.clear()
                }
                if (result.code == 0 && result.data?.data?.size!! > 0) {
                    var count = 0
                    result.data?.data?.forEachIndexed { index, photo ->
                        var cd = Calendar.getInstance()
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val lo = sdf.parse(photo.createTime).time
                        cd.time = Date(lo)
                        var year = cd.get(Calendar.YEAR)
                        var month = cd.get(Calendar.MONTH)
                        var day = cd.get(Calendar.DAY_OF_MONTH)
                        var date = "" + year + "" + month + "" + day
                        if (tes != date) {
                            tes = date
                            items.add(PhotoEntitiy(ObservableField(""), ObservableField(photo.createTime!!), ObservableField(0)))
                            items.add(PhotoEntitiy(ObservableField(getImageUrl(photo.imgUrl!!)), ObservableField(photo.createTime!!), ObservableField(1), ObservableField(photo.id!!)))
                        } else {
                            items.add(PhotoEntitiy(ObservableField(getImageUrl(photo.imgUrl!!)), ObservableField(photo.createTime!!), ObservableField(1), ObservableField(photo.id!!)))
                        }
                    }
                    var t = items.size - count
                    totalCount.set("/" + limit * page)
                }

                if (items.isNullOrEmpty()) {
                    isEmpty.set(true)
                } else {
                    isEmpty.set(false)
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                viewModel.activity.dismissProgressDialog()
            }

            override fun onComplete() {
                super.onComplete()
                viewModel.activity.dismissProgressDialog()
            }
        })
    }
}