package com.elder.zcommonmodule.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.elder.zcommonmodule.Entity.CityEntity
import com.zyyoona7.picker.DatePickerView
import com.zyyoona7.picker.OptionsPickerView
import com.zyyoona7.wheel.WheelView
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.databinding.ObservableArrayList
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Environment
import com.elder.zcommonmodule.Utils.Dialog.ActionSheetDialog
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.widget.*
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.AllCarsEntity
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog.STYLE_ONE
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog.STYLE_TWO
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.*
import org.cs.tec.library.binding.command.BindingCommand
import java.io.File


class DialogUtils() {


    companion object {

        fun showProgress(mActivity: Activity, text: String): Dialog {
            var progressDialog: Dialog? = null
            if (progressDialog == null) {
                progressDialog = Dialog(mActivity)
                var inflate = mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var view = inflate.inflate(com.zk.library.R.layout.loading_layout, null, false)
                progressDialog!!.setContentView(view)
                progressDialog!!.setCancelable(false)
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.window.setBackgroundDrawableResource(android.R.color.transparent)
            }
            var img = progressDialog!!.findViewById<ImageView>(com.zk.library.R.id.progress_animate)
            var title = progressDialog!!.findViewById<TextView>(com.zk.library.R.id.progress_title)
            if (!progressDialog!!.isShowing) {
                title!!.text = text
                var d = img.background as AnimationDrawable
                d.start()
                progressDialog!!.show()
            }
            return progressDialog!!
        }


        fun creteDialog(activity: AppCompatActivity, type: Int): Dialog {
            var dialog = Dialog(activity)
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.register_notice_dialog)
            activity.window.setGravity(Gravity.CENTER)
            dialog!!.setCanceledOnTouchOutside(true)
            dialog!!.setCancelable(true)
            var img = dialog.findViewById<ImageView>(R.id.register_notice_img)
            var name = dialog.findViewById<TextView>(R.id.register_notice_name)
            if (type == 0) {
                img.setImageResource(R.drawable.ok_ovel)
                name.text = getString(R.string.register_success)
            } else {
                img.setImageResource(R.drawable.error_ovel)
                name.text = getString(R.string.register_error)
            }
            return dialog
        }

        fun LoadBigImageLook(context: AppCompatActivity, url: String): AlertDialog {
            var builder = AlertDialog.Builder(context, R.style.NoTitleFullscreen)
            var create = builder.create()
            create.show()
            create.setCancelable(true)
            var mInflater = org.cs.tec.library.Base.Utils.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.bigimagelook, null)
            var window = create.window
            var windowManager = context.windowManager
            var defaultDisplay = windowManager.defaultDisplay
            var params = create.window.attributes
            window.decorView.setPadding(0, 0, 0, 0)
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.CENTER)
            var img = view.findViewById<ImageView>(R.id.bigimagelook_image)
            val options = RequestOptions().error(R.drawable.bond_car_camera).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true)
            Glide.with(img.context).asBitmap().load(url).apply(options).into(img)
            view.findViewById<RelativeLayout>(R.id.bigimage_layout).setOnClickListener {
                create.dismiss()
            }
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            return create
        }

        fun createPictureToolsDialog(activity: AppCompatActivity): AlertDialog? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            var mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.picture_tool_dialog, null)
            var linear = view.findViewById<LinearLayout>(R.id.horizontallayout)
            var window = alertDialog!!.window
            window.decorView.setPadding(0, 0, 0, 0)
            var params = activity.window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return alertDialog
        }

        fun getRealPathFromURI(activity: AppCompatActivity, contentUri: Uri): String {
            try {
                val proj = arrayOf(MediaStore.MediaColumns.DATA)
                val cursor = activity.managedQuery(contentUri, proj, null, null, null)
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            } catch (e: Exception) {
                return contentUri.path
            }
        }

        fun startCrop(activity: AppCompatActivity, uri: Uri, width: Int, hight: Int): String {
            var intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(uri, "image/*")
            // 图片处于可裁剪状态
            intent.putExtra("crop", "true")
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", width)
            intent.putExtra("aspectY", hight)
            // 是否之处缩放
            intent.putExtra("scale", true)
            intent.putExtra("outputX", width)
            intent.putExtra("outputY", hight)
            val outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            var cropFile = File(outDir, System.currentTimeMillis().toString() + ".jpg")
            val cropImageUri: Uri
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            cropImageUri = Uri.fromFile(cropFile)
            var realPathFromURI = getRealPathFromURI(activity, cropImageUri)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri)
            // 设置图片输出格式
            // 关闭人脸识别
            activity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE)
            return realPathFromURI
        }


        fun showYearDialog(activity: AppCompatActivity, birthdayCommand: BindingCommand<String>): DatePickerView? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.year_dialog, null)
            var window = alertDialog!!.window
            var year = view.findViewById<DatePickerView>(R.id.wheel_year)
            year.setTextSize(48F)
            year.setShowLabel(false)
            year.setDrawSelectedRect(true)
            year.setLineSpacing(30F, true)
            year.yearWv.setTextBoundaryMargin(20F, true)
            year.monthWv.visibility = View.GONE
            year.dayWv.visibility = View.GONE
//            year.monthWv.setTextBoundaryMargin(20F, true)
//            year.dayWv.setTextBoundaryMargin(20F, true)
            year.yearWv.setIntegerNeedFormat("%d年")
//            year.monthWv.setIntegerNeedFormat("%d月")
//            year.dayWv.setIntegerNeedFormat("%02d日")
            year.setShowDivider(true)
            year.setNormalItemTextColor(getColor(R.color.hint_color_edit))
            year.setSelectedItemTextColorRes(R.color.blackTextColor)
            year.setDividerType(WheelView.DIVIDER_TYPE_WRAP)
            year.setDividerColor(Color.parseColor("#62B297"))
            year.setDividerPaddingForWrap(10F, true)
            view.findViewById<TextView>(R.id.pickerView_cancle).setOnClickListener {
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.pickerView_ok).setOnClickListener {
                birthdayCommand.execute(year.selectedYear.toString())
                alertDialog.dismiss()
            }
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setBackgroundDrawable(context.getDrawable(R.color.colorWhite))
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return year

        }


        fun showBirthdayDialog(activity: AppCompatActivity, birthdayCommand: BindingCommand<String>): DatePickerView? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.birthday_dialog, null)
            var window = alertDialog!!.window
            var year = view.findViewById<DatePickerView>(R.id.wheel_year)
            year.setTextSize(48F)
            year.setShowLabel(false)
            year.setDrawSelectedRect(true)
            year.setLineSpacing(30F, true)
            year.yearWv.setTextBoundaryMargin(20F, true)
            year.monthWv.setTextBoundaryMargin(20F, true)
            year.dayWv.setTextBoundaryMargin(20F, true)
            year.yearWv.setIntegerNeedFormat("%d年")
            year.monthWv.setIntegerNeedFormat("%d月")
            year.dayWv.setIntegerNeedFormat("%02d日")
            year.setShowDivider(true)
            year.setNormalItemTextColor(getColor(R.color.hint_color_edit))
            year.setSelectedItemTextColorRes(R.color.blackTextColor)
            year.setDividerType(WheelView.DIVIDER_TYPE_WRAP)
            year.setDividerColor(Color.parseColor("#62B297"))
            year.setDividerPaddingForWrap(10F, true)
            view.findViewById<TextView>(R.id.pickerView_cancle).setOnClickListener {
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.pickerView_ok).setOnClickListener {
                birthdayCommand.execute(year.selectedYear.toString() + "-" + year.selectedMonth + "-" + year.selectedDay)
                alertDialog.dismiss()
            }
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return year
        }


        fun showGenderDialog(activity: AppCompatActivity, genderCommand: BindingCommand<String>, list: ArrayList<String>, title: String): WheelView<String>? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.gender_dialog, null)
            var window = alertDialog!!.window
            var year = view.findViewById<WheelView<String>>(R.id.gender_view)
            year.data = list
            year.textSize = 48F
            year.isShowDivider = true
            year.setLineSpacing(30F, true)
            year.dividerType = WheelView.DIVIDER_TYPE_WRAP
            year.dividerColor = Color.parseColor("#62B297")
            year.setDividerPaddingForWrap(15F, true)
            view.findViewById<TextView>(R.id.pickerView_cancle).setOnClickListener {
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.pickerView_ok).setOnClickListener {
                genderCommand.execute(year.selectedItemData)
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.gener_title).text = title
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return year
        }

        fun showCityDialog(activity: AppCompatActivity, cityCommand: BindingCommand<String>): OptionsPickerView<CityEntity>? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.city_dialog, null)
            var window = alertDialog!!.window
            var year = view.findViewById<OptionsPickerView<CityEntity>>(R.id.contry_view)
            var s1 = ArrayList<CityEntity>(1)
            var s2 = ArrayList<List<CityEntity>>()
            ParseHelper.initTwoLevelCityList(activity, s1, s2)
            year.setLinkageData(s1, s2)
            year.setTextSize(48F)
            year.setShowDivider(true)
            year.setLineSpacing(30F, true)
            year.setDividerType(WheelView.DIVIDER_TYPE_WRAP)
            year.setDividerColor(Color.parseColor("#62B297"))
            view.findViewById<TextView>(R.id.pickerView_cancle).setOnClickListener {
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.pickerView_ok).setOnClickListener {
                cityCommand.execute(year.opt1SelectedData.name + "、" + year.opt2SelectedData.name)
                alertDialog.dismiss()
            }
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return year
        }


        fun showCarsDialog(activity: AppCompatActivity, cityCommand: BindingCommand<String>, datas: AllCarsEntity): OptionsPickerView<AllCarsEntity.AllCarsTypeBean>? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.city_dialog, null)
            var window = alertDialog!!.window
            var year = view.findViewById<OptionsPickerView<AllCarsEntity.AllCarsTypeBean>>(R.id.contry_view)
            var s1 = ArrayList<AllCarsEntity.AllCarsTypeBean>(1)
            var s2 = ArrayList<List<AllCarsEntity.AllCarsTypeBean>>()
            s1.addAll(datas.data!!)
            datas.data?.forEach {
                var list = ArrayList<AllCarsEntity.AllCarsTypeBean>()
                var nextlist = it.list
                if (nextlist != null) {
                    list.addAll(nextlist!!)
                }
                s2.addAll(listOf(list))
            }
            year.setLinkageData(s1, s2)
            year.setTextSize(48F)
            year.setShowDivider(true)
            year.setLineSpacing(30F, true)
            year.setDividerType(WheelView.DIVIDER_TYPE_WRAP)
            year.setDividerColor(Color.parseColor("#62B297"))
            view.findViewById<TextView>(R.id.pickerView_cancle).setOnClickListener {
                alertDialog.dismiss()
            }
            view.findViewById<TextView>(R.id.pickerView_ok).setOnClickListener {
                cityCommand.execute("ok")
                alertDialog.dismiss()
            }
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return year
        }

        fun showAnimSelect(activity: AppCompatActivity): ActionSheetDialog {
            val cs = arrayOf(getString(R.string.publicStr), //
                    getString(R.string.privateSelf_Str)//
            )
            val itemList = ArrayList<String>()
            for (string in cs) {
                itemList.add(string)
            }
            val contents = arrayOfNulls<String>(itemList.size)
            var dialog = ActionSheetDialog(activity, //
                    cs, null)
//            dialog.show()
            dialog.isTitleShow(false)
                    .itemTextColor(getColor(R.color.blackTextColor))
                    .layoutAnimation(null)//
                    .show()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setOnOperItemClickL { parent, view, position, id ->
                when (position) {
                    0 -> {
                        selectType?.getType(1)
                        dialog.dismiss()
                    }
                    1 -> {           // 访问照相机
                        selectType?.getType(2)
                        dialog.dismiss()
                    }
                }
            }
            return dialog
        }

        fun showAnim(activity: AppCompatActivity, type: Int): ActionSheetDialog {
            val cs = arrayOf(getString(R.string.localImg), //
                    getString(R.string.opencamera)//
            )
            val itemList = ArrayList<String>()
            for (string in cs) {
                itemList.add(string)
            }
            val contents = arrayOfNulls<String>(itemList.size)
            var dialog = ActionSheetDialog(activity, //
                    cs, null)
//            dialog.show()
            dialog.isTitleShow(false)
                    .itemTextColor(getColor(R.color.blackTextColor))
                    .layoutAnimation(null)//
                    .show()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setOnOperItemClickL { parent, view, position, id ->
                when (position) {
                    0 -> {
                        // 打开本地相册
                        if (type == 0) {
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            activity.startActivityForResult(intent, PICK_IMAGE_ACTIVITY_REQUEST_CODE)
                        } else {
                            ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_PHOTO).withInt(RouterUtils.SocialConfig.SOCIAL_MAX_COUNT,type).navigation(activity, SOCIAL_SELECT_PHOTOS)
                        }
                        dialog.dismiss()
                    }
                    1 -> {           // 访问照相机
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity
                                        .checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                            activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), CHECK_PERMISSION)
                        } else {
                            openC(dialog, activity)
                        }
                    }
                }
            }
            return dialog
        }

        private fun openC(dialog: ActionSheetDialog, context: AppCompatActivity) {
            dialog.dismiss()
            startCamera(context)
        }

        fun startCamera(context: AppCompatActivity): Uri {
            val state = Environment.getExternalStorageState()
            if (state == Environment.MEDIA_MOUNTED) {
                val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!outDir.exists()) {
                    outDir.mkdirs()
                }
                val outFile = File(outDir, System.currentTimeMillis().toString() + ".jpg")
                var picFileFullName = outFile.absolutePath
                var iconuri: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    iconuri = FileProvider.getUriForFile(context!!, "com.elder.zcommonmodule.fileprovider", outFile)
                    intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    iconuri = Uri.fromFile(outFile)
                }
                if (lisentner != null) {
                    lisentner!!.getIcon(iconuri)
                }
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, iconuri)
                intent1.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                context!!.startActivityForResult(intent1, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
                return iconuri
            } else {
                return null!!
            }
        }


        @SuppressLint("SetTextI18n")
        fun createBigPicShow(activity: FragmentActivity, s: ObservableArrayList<String>, position: Int): AlertDialog {
           var list   = ArrayList<String>()
            s.forEach {
                if(!it.isNullOrEmpty()){
                    list.add(it)
                }
            }
            var buidler = AlertDialog.Builder(activity)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(false)
            alertDialog!!.setCancelable(true)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.big_pic_layout, null)
            var pager = view.findViewById<ViewPager>(R.id.dialog_viewpager)
            var adapter = MyPagerAdapter(activity, list)
            pager.adapter = adapter
            view.findViewById<ImageView>(R.id.dialog_back).setOnClickListener {
                alertDialog.dismiss()
            }
            var title = view.findViewById<TextView>(R.id.dialog_tv_title)
            title.setText((position + 1).toString() + "/" + list.size)
            pager.currentItem = position
            pager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {
                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(p0: Int) {
                    title.setText((p0 + 1).toString() + "/" + list.size)
                }
            })
            var window = alertDialog!!.window
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.MATCH_PARENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(context.getDrawable(R.color.white))
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            return alertDialog
        }

        class MyPagerAdapter : PagerAdapter {
            var url: ArrayList<String>
            var context: Context

            constructor(context: Context, url: ArrayList<String>) {
                this.url = url
                this.context = context
            }

            override fun isViewFromObject(p0: View, p1: Any): Boolean {

                return p0 == p1
            }

            override fun getCount(): Int {
                return url.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                var img = ImageView(this.context)
                Glide.with(img.context).asDrawable().load(url.get(position)).into(img)
                container.addView(img)
                return img
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }


        }


        fun createShareDialog(activity: Activity, left: String, right: String): AlertDialog? {
            var buidler = AlertDialog.Builder(activity, R.style.PopupAnimation)
            var alertDialog = buidler.create()
            alertDialog!!.setCanceledOnTouchOutside(false)
            alertDialog!!.setCancelable(false)
            alertDialog!!.show()
            val mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = mInflater.inflate(R.layout.share_bottom_dialog, null)
            view.findViewById<TextView>(R.id.share_left_tv).text = left
            view.findViewById<TextView>(R.id.share_right_tv).text = right
            view.findViewById<TextView>(R.id.share_dialog_cancle).setOnClickListener {
                alertDialog?.dismiss()
            }
            var window = alertDialog!!.window
            window.decorView.setPadding(0, 0, 0, 0)
            var params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(context.getDrawable(R.color.white))
            window.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            window.setWindowAnimations(R.style.PopupAnimation)
            return alertDialog
        }


        fun createNomalDialog(activity: Activity, content: String, vararg array: String): NormalDialog {
            var dialog = NormalDialog(activity).style(STYLE_TWO).content(content).btnText(*array)
            return dialog
        }

        fun createNomalStyleOneDialog(activity: Activity, content: String, array: String): NormalDialog {
            var dialog = NormalDialog(activity).style(STYLE_ONE).content(content).btnText(array)
            dialog.btnNum(1)
            return dialog
        }

        var lisentner: IconUriCallBack? = null

        var selectType :opitionCallBack? = null

        interface opitionCallBack {
            fun getType(type: Int)
        }

        interface IconUriCallBack {
            fun getIcon(iconUri: Uri)
        }
    }
}



