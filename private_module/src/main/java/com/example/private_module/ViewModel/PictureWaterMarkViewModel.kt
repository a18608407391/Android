package com.example.private_module.ViewModel

import android.content.Context
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.elder.zcommonmodule.Intel.OnItemClickListener
import com.elder.zcommonmodule.REAL_PATH
import com.example.private_module.Activity.PictureWaterMackActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_picture_watermack.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Utils.ConvertUtils
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.provider.MediaStore
import android.widget.Toast
import org.cs.tec.library.Base.Utils.getString
import java.io.File


class PictureWaterMarkViewModel : BaseViewModel(), OnItemClickListener {
    override fun HorizontalScrollViewItemClick(view: View, position: Int) {
        when (position) {
            R.drawable.warter_marker_non -> {
                pictureWaterMackActivity.move_img.setImageResource(R.drawable.trans_bg)
            }
            R.drawable.warter_marker -> {
                pictureWaterMackActivity.move_img.setImageResource(R.drawable.maker_logo)
            }
            R.drawable.warter_marker_q -> {
                pictureWaterMackActivity.move_img.setImageResource(R.drawable.marker_driver)
            }
            R.drawable.warter_marker_z -> {
                pictureWaterMackActivity.move_img.setImageResource(R.drawable.maker_machine)
            }
            R.drawable.warter_marker_m -> {
                pictureWaterMackActivity.move_img.setImageResource(R.drawable.marker_molv)
            }
        }
    }

    var CurrentPath = ObservableField<String>()
    lateinit var pictureWaterMackActivity: PictureWaterMackActivity
    var data = arrayOf(R.drawable.warter_marker_non, R.drawable.warter_marker, R.drawable.warter_marker_q, R.drawable.warter_marker_z, R.drawable.warter_marker_m)
    fun inject(pictureWaterMackActivity: PictureWaterMackActivity) {
        this.pictureWaterMackActivity = pictureWaterMackActivity
        CurrentPath.set(pictureWaterMackActivity.intent.getStringExtra(REAL_PATH))
        var layoutR = pictureWaterMackActivity.findViewById<RelativeLayout>(R.id.draw_layout)
        pictureWaterMackActivity.findViewById<ImageView>(R.id.marker_photo_create).setOnClickListener {
            var bitmap = ConvertUtils.view2Bitmap(layoutR)
            val sdf = SimpleDateFormat("yyyyMMddHHmmss")
            var file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator + sdf.format(Date()) + ".jpg")
            var fos = FileOutputStream(file.path)
            var b = bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            if (b!!) {
                Toast.makeText(context, getString(R.string.water_marker_picture_success), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.water_marker_picture_fail), Toast.LENGTH_SHORT).show()
            }
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, file.name, null)
            finish()









        }
        pictureWaterMackActivity.findViewById<ImageView>(R.id.marker_photo_back).setOnClickListener {
            finish()
        }
        var linear = pictureWaterMackActivity.findViewById<LinearLayout>(com.elder.zcommonmodule.R.id.horizontallayout)
        data.forEach {
            var m = it
            var t = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = t.inflate(R.layout.warter_mark_type_layout, linear, false)
            var img = view.findViewById<ImageView>(R.id.acse)
            img.setImageResource(it)
            img.setOnClickListener {
                itemClick.HorizontalScrollViewItemClick(it, m)
            }
            linear.addView(view)
        }
    }

    var itemClick: OnItemClickListener = this


}