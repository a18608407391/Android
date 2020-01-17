package com.example.private_module.ViewModel

import android.content.Intent
import android.database.Cursor
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Inteface.PictureItemClick
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.private_module.Activity.PictureSelectorActivity
import com.example.private_module.Activity.PictureWaterMackActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Utils.ImageUtils.compressWithRx
import org.cs.tec.library.Utils.constant.MemoryConstants.MB
import java.io.File
import java.util.concurrent.TimeUnit

class PictureSelectorViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
        var fileList = ArrayList<File>()
        var largeFileList = ArrayList<String>()
        items.forEach {
            if (it.isCheced.get()!!) {
                var file = File(it.path.get())
                if (file.length() > 2 * MB) {
                    largeFileList.add(file.path)
                } else {
                    fileList.add(File(it.path.get()))
                }
            }
        }
        if (fileList.size == 0 && largeFileList.size == 0) {
            Toast.makeText(context, getString(R.string.notselectPhoto), Toast.LENGTH_SHORT).show()
            return
        }
        pictureSelectorActivity.showProgressDialog(getString(R.string.upload_photo))
        var deletFile = ArrayList<File>()
        if (largeFileList.size > 0) {
            compressWithRx(largeFileList, object : Observer<File?> {
                override fun onComplete() {
                    doUpload(fileList, deletFile)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: File) {
                    deletFile.add(t)
                    fileList.add(t)
                }

                override fun onError(e: Throwable) {
                }
            })
        } else {
            doUpload(fileList, deletFile)
        }
    }


    fun doUpload(fileList: ArrayList<File>, deletFile: ArrayList<File>) {
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var mult = MultipartBody.Builder().setType(MultipartBody.FORM)
            fileList.forEach {
                mult.addFormDataPart("files", it.name, RequestBody.create(MediaType.parse("image/jpg"), it))
            }
            var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiActivity/userCenterManage/uploadFile").post(mult.build()).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()!!.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.e("result", it)
            var response = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
            if (response.code == 0) {
                finish()
            } else {
                Toast.makeText(context, response.msg, Toast.LENGTH_SHORT).show()
            }
            pictureSelectorActivity.dismissProgressDialog()
            if (deletFile.size != 0) {
                deletFile.forEach {
                    it.delete()
                }
            }
        }
    }


    var realPath: Uri? = null
    fun onItemClick(info: PictureInfo, position: Int) {
        if (position == 0) {
            //开始拍摄  进行图片裁剪 水印添加等等
            realPath = DialogUtils.startCamera(pictureSelectorActivity)
        } else {
            ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.ADD_MARKER_ACTIVITY).withString(RouterUtils.PrivateModuleConfig.ADD_MARKER_URL, info.path.get())
        }
    }

    var titleComponent = TitleComponent().apply {
        this.title.set(getString(R.string.all_picture))
        this.rightText.set(getString(R.string.finish))
        this.arrowVisible.set(false)
    }

    lateinit var pictureSelectorActivity: PictureSelectorActivity
    fun inject(pictureSelectorActivity: PictureSelectorActivity) {
        this.pictureSelectorActivity = pictureSelectorActivity
        titleComponent.setCallBack(this)
        getAllPicture()
    }

    var adapter = BindingRecyclerViewAdapter<PictureInfo>()
    var items = ObservableArrayList<PictureInfo>().apply {
        this.add(PictureInfo(ObservableField(""), ObservableField(0), ObservableField(""), ObservableField(0)))
    }
    var itemBinding = ItemBinding.of<PictureInfo> { itemBinding, position, item ->
        itemBinding.set(BR.picture_img_item, R.layout.picture_item).bindExtra(BR.position, position).bindExtra(BR.picmodel, this@PictureSelectorViewModel)
    }
    val viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> PictureViewHolder(binding.root) }

    private class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun getAllPicture() {

        Observable.create(ObservableOnSubscribe<Cursor> {
            var imgeUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            var resolver = context.contentResolver
            var cursor = resolver.query(imgeUri, arrayOf(MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID), null, null, MediaStore.Images.Media.DATE_ADDED + " DESC")
            it.onNext(cursor)
        }).subscribeOn(Schedulers.io()).map(Function<Cursor, ArrayList<PictureInfo>> {
            var mListDatas = ArrayList<PictureInfo>()
            while (it.moveToNext()) {
                var path = it.getString(
                        it.getColumnIndex(MediaStore.Images.Media.DATA))
                //获取图片名称
                var name = it.getString(
                        it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                //获取图片时间
                var time = it.getLong(
                        it.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                var mediaSize = it.getColumnIndex(MediaStore.Images.Media.SIZE)
                var size: Long = 0
                if (mediaSize > 0)
                    size = it.getLong(mediaSize) / 1024 //单位kb
                if (size <= 0) {
                    size = File(path).length() / 1024
                }
                var pictureInfo = PictureInfo(ObservableField(name), ObservableField(time), ObservableField(path), ObservableField(mediaSize))
                mListDatas.add(pictureInfo)
            }
            return@Function mListDatas
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            items.clear()
            selectCount = 0
            items.add(PictureInfo(ObservableField(""), ObservableField(0), ObservableField(""), ObservableField(0)))
            it.forEach {
                items.add(it)
            }
        }
    }

    fun onResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 105) {
            pictureSelectorActivity.startActivityForResult(Intent(pictureSelectorActivity, PictureWaterMackActivity::class.java).putExtra(REAL_PATH, realPath?.toString()), REQUEST_IMG_WARTER_MARK)
        } else if (requestCode == REQUEST_IMG_WARTER_MARK) {
            getAllPicture()
        }
    }

    var selectCount = 0

//    var boxCommand = BindingCommand(object : BindingConsumer<CheckBoxInfo> {
//        override fun call(t: CheckBoxInfo) {
//            if (t.flag && selectCount > 8) {
//                t.box.isChecked = false
//                Toast.makeText(context, getString(R.string.photo_max), Toast.LENGTH_SHORT).show()
//                return
//            }

    //        }
//    })


    fun onAdapterItemClick(item: PictureInfo, position: Int) {
        if (!item.isCheced.get()!!) {
            if (selectCount > 8) {
                Toast.makeText(context, getString(R.string.photo_max), Toast.LENGTH_SHORT).show()
                return
            }
            selectCount++
        } else {
            selectCount--
        }
        item.isCheced.set(!item.isCheced.get()!!)
        items.set(position, item)
    }
}