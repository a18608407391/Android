package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.SHARE_PICTURE
import com.example.private_module.Activity.PhotoAddWaterMarkerActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import java.io.File


class PhotoAddWaterMarkerViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    var component = TitleComponent()
    lateinit var photoActivity: PhotoAddWaterMarkerActivity
    fun inject(photoActivity: PhotoAddWaterMarkerActivity) {
        this.photoActivity = photoActivity
        component.title.set(getString(R.string.check_picture))
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.setCallBack(this)
        path.set(photoActivity.url)
    }

    var path = ObservableField<String>()
    fun saveToLocal(url: String) {
        photoActivity.showProgressDialog(getString(R.string.http_loading))
        CoroutineScope(ioContext).async {
            var file = Glide.with(photoActivity)
                    .load(url)
                    .downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            var files = file.get()
            if (files != null) {
                var bitmap = BitmapFactory.decodeFile(files.path)
                MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, files?.name, null)
                launch(uiContext) {
                    photoActivity.dismissProgressDialog()
                    Toast.makeText(context, getString(R.string.save_local_success), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    var time = 0L

    fun onClick(view: View) {
        if (System.currentTimeMillis() - time > 500) {
            time = System.currentTimeMillis()
        } else {
            return
        }
        when (view.id) {
            R.id.add_download -> {
                saveToLocal(path.get()!!)
            }
            R.id.add_share -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.SHARE_ADD_MARKER_ACTIVITY).withString(RouterUtils.PrivateModuleConfig.ADD_MARKER_URL, photoActivity.url).navigation(photoActivity, SHARE_PICTURE)
            }
        }
    }

}