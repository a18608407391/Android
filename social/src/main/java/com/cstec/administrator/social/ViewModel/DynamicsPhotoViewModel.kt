package com.cstec.administrator.social.ViewModel

import android.content.Intent
import android.database.Cursor
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.cstec.administrator.social.Activity.DynamicsPhotoActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Inteface.PictureItemClick
import com.elder.zcommonmodule.PictureInfo
import com.elder.zcommonmodule.SOCIAL_SELECT_PHOTOS
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import java.io.File


class DynamicsPhotoViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        activity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        var list = ArrayList<String>()
        items.forEach {
            if (it.isCheced.get()!!) {
                list.add(it.path.get()!!)
            }
        }
        var bundler = Bundle()
        bundler.putStringArrayList("PhotoUrls", list)
        activity.setFragmentResult(SOCIAL_SELECT_PHOTOS, bundler)
        activity._mActivity!!.onBackPressedSupport()
//        var intent = Intent()
//        intent.putStringArrayListExtra("PhotoUrls", list)
//        activity.setResult(SOCIAL_SELECT_PHOTOS, intent)
//        finish()
    }

    var realPath: Uri? = null

    fun onItemClick(info: PictureInfo, position: Int) {
        if (position == 0) {
            //开始拍摄  进行图片裁剪 水印添加等等
            realPath = DialogUtils.startCameraFragment(activity)
        }
    }


    var selectCount = 0
    var adapter = BindingRecyclerViewAdapter<PictureInfo>()
    var items = ObservableArrayList<PictureInfo>().apply {
        this.add(PictureInfo(ObservableField(""), ObservableField(0), ObservableField(""), ObservableField(0)))
    }
    var itemBinding = ItemBinding.of<PictureInfo> { itemBinding, position, item ->
        itemBinding.set(BR.picture_img_item, R.layout.social_picture_item).bindExtra(BR.position, position).bindExtra(BR.picmodel, this@DynamicsPhotoViewModel)
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

    var titleComponent = TitleComponent().apply {
        this.title.set(getString(R.string.all_picture))
        this.rightText.set(getString(R.string.finish))
        this.arrowVisible.set(false)
    }

    fun onAdapterItemClick(item: PictureInfo, position: Int) {
        if (!item.isCheced.get()!!) {
            if (selectCount > activity.count - 1) {
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


    lateinit var activity: DynamicsPhotoActivity
    fun inject(activity: DynamicsPhotoActivity) {
        this.activity = activity
        titleComponent.callback = this
        getAllPicture()
    }

}