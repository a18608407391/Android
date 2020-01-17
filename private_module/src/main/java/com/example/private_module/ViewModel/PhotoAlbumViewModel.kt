package com.example.private_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Http.BaseObserver
import com.example.private_module.Activity.MyPhotoAlbumActivity
import com.example.private_module.Adapter.PhotoAdapter
import com.example.private_module.BR
import com.example.private_module.Bean.PhotoBean
import com.example.private_module.Bean.PhotoEntitiy
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.checkbox.CheckBoxInfo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photoalbum.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.Utils.ToastUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class PhotoAlbumViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener {
    override fun onRefresh() {
        tes = ""
        items.clear()
        limit = 30
        page = 1
        getAllPhoto(true)
    }

    var lastdate = ""
    var page: Int = 1
    var limit: Int = 30
    var isLongClick = ObservableField<Boolean>(false)
    var checedCount = ObservableField<String>("0")
    var totalCount = ObservableField<String>("/100")
    var isEmpty = ObservableField<Boolean>(false)
    override fun onComponentClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        myPhotoAlbumActivity.finish()
    }

    override fun onComponentFinish(view: View) {
        ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.PICTURE_SELECTOR).navigation(myPhotoAlbumActivity, ENTER_TO_SELECTE)
    }

    lateinit var myPhotoAlbumActivity: MyPhotoAlbumActivity
    var component = TitleComponent()
    fun inject(myPhotoAlbumActivity: MyPhotoAlbumActivity) {
        this.myPhotoAlbumActivity = myPhotoAlbumActivity
        component.title.set(getString(R.string.my_photo_album))
        component.rightVisibleType.set(false)
        component.arrowVisible.set(false)
        component.setCallBack(this)
        getAllPhoto(true)
    }

    var tes = ""
    var categoryTitle = ArrayList<String>()
    fun getAllPhoto(refresh: Boolean) {
        if (refresh) {
            page = 1
            limit = 30
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        myPhotoAlbumActivity.showProgressDialog(getString(R.string.get_photo_info))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            map["page"] = page.toString()
            map["limit"] = limit.toString()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/queryUserPicByUserId").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : BaseObserver<String>(myPhotoAlbumActivity) {
            override fun onNext(t: String) {
                super.onNext(t)

                Log.e("result", "相册数据" + t)
                myPhotoAlbumActivity.dismissProgressDialog()
                if (refresh) {
                    tes = ""
                    checkPositionList.clear()
                    myPhotoAlbumActivity.Swiperefresh.isRefreshing = false
                    items.clear()
                }
                var result = Gson().fromJson<PhotoBean>(t, PhotoBean::class.java)
                if (result.code == 0) {
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
                myPhotoAlbumActivity.dismissProgressDialog()
            }

            override fun onComplete() {
                super.onComplete()
                myPhotoAlbumActivity.dismissProgressDialog()
            }
        })
    }

    var adapter = PhotoAdapter(this)
    var items = ObservableArrayList<PhotoEntitiy>()
    var itemBinding = ItemBinding.of<PhotoEntitiy> { itemBinding, position, item ->
        if (item.getItemType() == 0) {
            itemBinding.set(BR.title, R.layout.my_photo_title_item)
        } else if (item.getItemType() == 1) {
            itemBinding.set(BR.item, R.layout.my_photo_item).bindExtra(BR.album_click, this@PhotoAlbumViewModel).bindExtra(BR.img_position, position)
        }
    }
    var viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> HomeViewHolder(binding.root) }

    private class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var longCommand = BindingCommand(object : BindingConsumer<PhotoEntitiy> {
        override fun call(t: PhotoEntitiy) {
            myPhotoAlbumActivity.Swiperefresh.isEnabled = false
            isLongClick.set(true)
        }
    })

    var checkPositionList = ArrayList<Int>()
    var checkCommand = BindingCommand(object : BindingConsumer<Boolean> {
        override fun call(t: Boolean) {
            if (t) {
                checkPositionList.clear()
                var count = 0
                items.forEachIndexed { index, photoEntitiy ->
                    if (!photoEntitiy.isCheced.get()!!) {
                        photoEntitiy.isCheced.set(true)
                    }
                    if (photoEntitiy.isCheced.get()!! && !photoEntitiy.path.get().isNullOrEmpty()) {
                        checkPositionList.add(index)
                        count++
                    }
                }
                checedCount.set(count.toString())

            } else {
                items.forEach {
                    if (it.isCheced.get()!!) {
                        it.isCheced.set(false)
                    }
                }
                checkPositionList.clear()
                checedCount.set("0")
            }
        }
    })

    fun onClick(view: View) {
        when (view.id) {
            R.id.album_delete -> {
                if (checkPositionList.isEmpty()) {
                    Toast.makeText(context, getString(R.string.select_empty_by_photo), Toast.LENGTH_SHORT).show()
                    return
                }
                var m = ""
                checkPositionList.forEachIndexed { index, i ->
                    if (index == checkPositionList.size) {
                        m = m + items[i].ids.get()
                    } else {
                        m = m + items[i].ids.get() + ","
                    }
                }
                var token = PreferenceUtils.getString(context, USER_TOKEN)
                myPhotoAlbumActivity.showProgressDialog(getString(R.string.deleting))
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map["ids"] = m
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/removePic").build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    var result = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                    if (result.code == 0) {
                        myPhotoAlbumActivity.Swiperefresh.isEnabled = true
                        isLongClick.set(false)
                        getAllPhoto(true)
                        myPhotoAlbumActivity.dismissProgressDialog()
                    }
                }
            }
            R.id.album_cancle -> {
                isLongClick.set(false)
                myPhotoAlbumActivity.Swiperefresh.isEnabled = true
            }
        }
    }

    fun onAdapterItemClick(item: PhotoEntitiy, position: Int) {
        if (isLongClick.get()!!) {
            if (!item.isCheced.get()!!) {
                checkPositionList.add(position)
            } else {
                checkPositionList.remove(position)
            }
            checedCount.set(checkPositionList.size.toString())
            item.isCheced.set(!item.isCheced.get()!!)
            items.set(position, item)
        } else {
            ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.ADD_MARKER_ACTIVITY).withString(RouterUtils.PrivateModuleConfig.ADD_MARKER_URL, item.path.get()).navigation(myPhotoAlbumActivity, SHARE_PICTURE)


//            DialogUtils.LoadBigImageLook(myPhotoAlbumActivity, items.get(position).path.get()!!)
        }
    }

    var onLoadMoreCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            page++
            limit = 30
            getAllPhoto(false)
        }
    })
}