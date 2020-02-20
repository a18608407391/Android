package com.cstec.administrator.chart_module.Activity

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.BrowserViewModel
import com.cstec.administrator.chart_module.databinding.ActivityBrowserPagerBinding
import cn.jpush.im.android.api.model.Conversation
import android.app.ProgressDialog
import android.content.Context
import cn.jpush.im.android.api.model.Message
import com.cstec.administrator.chart_module.Activity.pickImage.ImgBrowserViewPager
import com.cstec.administrator.chart_module.Activity.pickImage.PhotoView
import android.util.SparseBooleanArray
import android.content.Intent
import cn.jpush.im.android.api.callback.DownloadCompletionCallback
import cn.jpush.im.android.api.callback.ProgressUpdateCallback
import cn.jpush.im.android.api.content.ImageContent
import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import cn.jpush.im.android.api.enums.ConversationType
import android.support.v4.view.ViewPager
import android.provider.MediaStore
import android.content.ContentValues
import android.os.*
import com.cstec.administrator.chart_module.Utils.AttachmentStore
import com.cstec.administrator.chart_module.Utils.StorageUtil
import android.view.WindowManager
import com.cstec.administrator.chart_module.View.ChatUtils.DialogCreator
import android.view.ViewGroup
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ContentType
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.Utils.BitmapLoader
import com.cstec.administrator.chart_module.Utils.NativeImageLoader
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import java.io.File
import java.lang.ref.WeakReference
import java.text.NumberFormat


//用于浏览图片
class BrowserViewPagerActivity : ChartBaseActivity<ActivityBrowserPagerBinding, BrowserViewModel>() {
    private var photoView: PhotoView? = null
    private var mViewPager: ImgBrowserViewPager? = null
    private var mProgressDialog: ProgressDialog? = null
    //存放所有图片的路径
    private var mPathList = ArrayList<String>()
    //存放图片消息的ID
    private var mMsgIdList = ArrayList<Int>()
    private var mNumberTv: TextView? = null
    private var mSendBtn: Button? = null
    private var mOriginPictureCb: CheckBox? = null
    private var mTotalSizeTv: TextView? = null
    private var mPictureSelectedCb: CheckBox? = null
    private var mLoadBtn: Button? = null
    private var mPosition: Int = 0
    private var mConv: Conversation? = null
    private var mMsg: Message? = null
    private var mFromChatActivity = true
    //当前消息数
    private var mStart: Int = 0
    private var mOffset = 18
    private var mContext: Context? = null
    private var mDownloading = false
    private var mMessageId: Int = 0
    private var mMsgIds: IntArray? = null
    private var mIndex = 0
    private var mUIHandler = UIHandler(this)
    private var mBackgroundHandler: BackgroundHandler? = null

    companion object {
        var DOWNLOAD_ORIGIN_IMAGE_SUCCEED = 1
        var DOWNLOAD_PROGRESS = 2
        var DOWNLOAD_COMPLETED = 3
        var SEND_PICTURE = 5
        var DOWNLOAD_ORIGIN_PROGRESS = 6
        var DOWNLOAD_ORIGIN_COMPLETED = 7
        var INITIAL_PICTURE_LIST = 0x2000
        var INIT_ADAPTER = 0x2001
        var GET_NEXT_PAGE_OF_PICTURE = 0x2002
        var SET_CURRENT_POSITION = 0x2003
    }

    private var mDialog: Dialog? = null
    var MSG_JSON = "msg_json"
    var MSG_LIST_JSON = "msg_list_json"
    private var messages = ArrayList<Message>()
    private var mSelectMap = SparseBooleanArray()
    override fun initVariableId(): Int {
        return BR.brower_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_image_browser
    }

    override fun initViewModel(): BrowserViewModel? {
        return ViewModelProviders.of(this)[BrowserViewModel::class.java]
    }

    lateinit var returnBtn: ImageButton
    lateinit var titleBarRl: RelativeLayout
    lateinit var checkBoxRl: RelativeLayout
    override fun initData() {
        super.initData()
        mContext = this
        setContentView(R.layout.activity_image_browser)
        mViewPager = findViewById(R.id.img_browser_viewpager)
        returnBtn = findViewById(R.id.return_btn)
        mNumberTv = findViewById(R.id.number_tv)
        mSendBtn = findViewById(R.id.pick_picture_send_btn)
        titleBarRl = findViewById(R.id.title_bar_rl)
        checkBoxRl = findViewById(R.id.check_box_rl)
        mOriginPictureCb = findViewById(R.id.origin_picture_cb)
        mTotalSizeTv = findViewById(R.id.total_size_tv)
        mPictureSelectedCb = findViewById(R.id.picture_selected_cb)
        mLoadBtn = findViewById(R.id.load_image_btn)

        var backgroundThread = HandlerThread("Work on BrowserActivity");
        backgroundThread.start()
        mBackgroundHandler = BackgroundHandler(backgroundThread.getLooper())
        var intent = this.getIntent();
        var conversationType = intent.getSerializableExtra(RouterUtils.Chat_Module.Chat_CONV_TYPE);
        var targetId = intent.getStringExtra(RouterUtils.Chat_Module.Chat_TARGET_ID);
        var targetAppKey = intent.getStringExtra(RouterUtils.Chat_Module.Chat_App_Key);
        if (conversationType != null) {
            when (conversationType) {
                ConversationType.single -> {
                    mConv = JMessageClient.getSingleConversation(targetId, targetAppKey)
                }
                ConversationType.group -> {
                    mConv = JMessageClient.getGroupConversation(targetId.toLong())
                }
                ConversationType.chatroom -> {
                    mConv = JMessageClient.getChatRoomConversation(targetId.toLong())
                }
            }
        }
        mMessageId = intent.getIntExtra("msgId", 0);
        mStart = intent.getIntExtra("msgCount", 0);
        mPosition = intent.getIntExtra(BaseApplication.POSITION, 0);
        mFromChatActivity = intent.getBooleanExtra("fromChatActivity", true);
        var browserAvatar = intent.getBooleanExtra("browserAvatar", false);
        returnBtn.setOnClickListener(listener);
        mSendBtn!!.setOnClickListener(listener);
        mLoadBtn!!.setOnClickListener(listener);
        // 在聊天界面中点击图片
        if (mFromChatActivity) {
            titleBarRl.setVisibility(View.GONE);
            checkBoxRl.setVisibility(View.GONE);
            //预览头像
            if (browserAvatar) {
                var path = intent.getStringExtra("avatarPath");
                photoView = PhotoView(mFromChatActivity, mContext)
                mLoadBtn!!.setVisibility(View.GONE);
                try {
                    var file = File(path);
                    mPathList.add(path);
                    mViewPager!!.setAdapter(pagerAdapter);
                    mViewPager!!.addOnPageChangeListener(onPageChangeListener);
                    if (file.exists()) {
                        Picasso.with(mContext).load(file).into(photoView);
                    } else {
                        photoView!!.setImageBitmap(NativeImageLoader.getInstance().getBitmapFromMemCache(path));
                    }
                } catch (e: Exception) {
                    photoView!!.setImageResource(R.drawable.jmui_picture_not_found);
                }
                //预览聊天界面中的图片
            } else {
                mBackgroundHandler!!.sendEmptyMessage(INITIAL_PICTURE_LIST);
            }
            // 在选择图片时点击预览图片
        } else {
            mPathList = intent.getStringArrayListExtra("pathList")
            mViewPager!!.setAdapter(pagerAdapter);
            mViewPager!!.addOnPageChangeListener(onPageChangeListener)
            var pathArray = intent.getIntArrayExtra("pathArray")
            //初始化选中了多少张图片
            pathArray.forEachIndexed { index, i ->
                if (pathArray[index] == 1) {
                    mSelectMap.put(i, true);
                }
            }
            showSelectedNum();
            mLoadBtn!!.setVisibility(View.GONE);
            mViewPager!!.setCurrentItem(mPosition);
            var numberText = (mPosition + 1).toString() + "/" + mPathList.size;
            mNumberTv!!.setText(numberText);
            var currentItem = mViewPager!!.getCurrentItem()
            checkPictureSelected(currentItem)
            checkOriginPictureSelected()
            //第一张特殊处理
            mPictureSelectedCb!!.setChecked(mSelectMap.get(currentItem))
            showTotalSize()
        }
    }

    var pagerAdapter: PagerAdapter = object : PagerAdapter() {

        override fun getCount(): Int {
            return mPathList.size
        }

        /**
         * 点击某张图片预览时，系统自动调用此方法加载这张图片左右视图（如果有的话）
         */
        override fun instantiateItem(container: ViewGroup, position: Int): View {
            photoView = PhotoView(mFromChatActivity, container.context)
            photoView!!.setTag(position)
            val path = mPathList[position]
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapLoader.getBitmapFromFile(path, mWidth, mHeight)
                    if (bitmap != null) {
                        photoView!!.setMaxScale(9F)
                        photoView!!.setImageBitmap(bitmap)
                    } else {
                        photoView!!.setImageResource(R.drawable.jmui_picture_not_found)
                    }
                } else {
                    var bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(path)
                    if (bitmap != null) {
                        photoView!!.setMaxScale(9F)
                        photoView!!.setImageBitmap(bitmap)
                    } else {
                        photoView!!.setImageResource(R.drawable.jmui_picture_not_found)
                    }
                }
            } else {
                photoView!!.setImageResource(R.drawable.jmui_picture_not_found)
            }
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            //图片长按保存到手机
            onImageViewFound(photoView!!, path)
            return photoView!!
        }

        override fun getItemPosition(`object`: Any): Int {
            val view = `object` as View
            val currentPage = mViewPager!!.getCurrentItem()
            return if (currentPage == view.getTag() as Int) {
                POSITION_NONE
            } else {
                POSITION_UNCHANGED
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }

    private fun onImageViewFound(photoView: PhotoView, path: String?) {
        photoView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View): Boolean {

                val listener = object : View.OnClickListener {
                    override fun onClick(v: View) {
                        when (v.getId()) {
                            R.id.jmui_delete_conv_ll//保存图片
                            -> savePicture(path, mDialog!!)
                            R.id.jmui_top_conv_ll//转发
                            -> {
//                                var intent = Intent(this@BrowserViewPagerActivity, ForwardMsgActivity::class.java)
//                                BaseApplication.forwardMsg.clear()
//                                BaseApplication.forwardMsg.add(mMsg!!)
//                                startActivity(intent)
                            }
                            else -> {
                            }
                        }
                        mDialog?.dismiss()
                    }
                }
                mDialog = DialogCreator.createSavePictureDialog(mContext, listener)
                mDialog?.show()
                mDialog?.getWindow()!!.setLayout((0.8 * mWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
                return false
            }
        })
    }

    // 保存图片
    fun savePicture(path: String?, dialog: Dialog) {
        if (TextUtils.isEmpty(path)) {
            return
        }

        val picPath = StorageUtil.getSystemImagePath()
        val dstPath = picPath + path!!
        if (AttachmentStore.copy(path, dstPath).toInt() != -1) {
            try {
                val values = ContentValues(2)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.Images.Media.DATA, dstPath)
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                Toast.makeText(mContext, getString(R.string.picture_save_to), Toast.LENGTH_LONG).show()
                dialog.dismiss()
            } catch (e: Exception) {
                dialog.dismiss()
                Toast.makeText(mContext, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show()
            }

        } else {
            dialog.dismiss()
            Toast.makeText(mContext, getString(R.string.picture_save_fail), Toast.LENGTH_LONG).show()
        }
    }

    private fun setLoadBtnText(ic: ImageContent) {
        val ddf1 = NumberFormat.getNumberInstance()
        //保留小数点后两位
        ddf1.setMaximumFractionDigits(2)
        val size = ic.fileSize / 1048576.0
        val loadText = mContext!!.getString(R.string.load_origin_image) + "(" + ddf1.format(size) + "M" + ")"
        mLoadBtn!!.setText(loadText)
    }

    /**
     * 在图片预览中发送图片，点击选择CheckBox时，触发事件
     *
     * @param currentItem 当前图片索引
     */
    private fun checkPictureSelected(currentItem: Int) {
        mPictureSelectedCb!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            if (mSelectMap.size() + 1 <= 9) {
                if (isChecked) {
                    mSelectMap.put(currentItem, true)
                } else {
                    mSelectMap.delete(currentItem)
                }
            } else if (isChecked) {
                Toast.makeText(mContext, mContext!!.getString(R.string.picture_num_limit_toast), Toast.LENGTH_SHORT).show()
                mPictureSelectedCb!!.setChecked(mSelectMap.get(currentItem))
            } else {
                mSelectMap.delete(currentItem)
            }

            showSelectedNum()
            showTotalSize()
        })

    }

    /**
     * 点击发送原图CheckBox，触发事件
     */
    private fun checkOriginPictureSelected() {
        mOriginPictureCb!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                if (mSelectMap.size() < 1) {
                    mPictureSelectedCb!!.setChecked(true)
                }
            }
        })
    }

    //显示选中的图片总的大小
    private fun showTotalSize() {
        if (mSelectMap.size() > 0) {
            val pathList = ArrayList<String>()
            for (i in 0 until mSelectMap.size()) {
                pathList.add(mPathList[mSelectMap.keyAt(i)])
            }
            val totalSize = BitmapLoader.getPictureSize(pathList)
            val totalText = mContext!!.getString(R.string.origin_picture) + String.format(mContext!!.getString(R.string.combine_title), totalSize)
            mTotalSizeTv!!.setText(totalText)
        } else {
            mTotalSizeTv!!.setText(mContext!!.getString(R.string.origin_picture))
        }
    }

    //显示选中了多少张图片
    private fun showSelectedNum() {
        if (mSelectMap.size() > 0) {
            val sendText = mContext!!.getString(R.string.jmui_send) + "(" + mSelectMap.size() + "/" + "9)"
            mSendBtn!!.setText(sendText)
        } else {
            mSendBtn!!.setText(mContext!!.getString(R.string.jmui_send))
        }
    }

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        //在滑动的时候更新CheckBox的状态
        override fun onPageScrolled(i: Int, v: Float, i2: Int) {
            checkPictureSelected(i)
            checkOriginPictureSelected()
            mPictureSelectedCb!!.setChecked(mSelectMap.get(i))
        }

        override fun onPageSelected(i: Int) {
            if (mFromChatActivity) {
                if (mConv!!.getType() === ConversationType.chatroom) {
                    mMsg = messages[i]
                } else {
                    mMsg = mConv!!.getMessage(mMsgIdList[i])
                }
                val ic = mMsg!!.getContent() as ImageContent
                //每次选择或滑动图片，如果不存在本地图片则下载，显示大图
                if (ic.localPath == null && i != mPosition) {
                    //                    mLoadBtn.setVisibility(View.VISIBLE);
                    downloadImage()
                } else if (ic.getBooleanExtra("hasDownloaded") != null && !ic.getBooleanExtra("hasDownloaded")) {
                    setLoadBtnText(ic)
                    mLoadBtn!!.setVisibility(View.GONE)
                } else {
                    mLoadBtn!!.setVisibility(View.GONE)
                }
                if (i == 0) {
                    getImgMsg()
                }
            } else {
                val numText = (i + 1).toString() + "/" + mPathList.size
                mNumberTv!!.setText(numText)
            }
        }

        override fun onPageScrollStateChanged(i: Int) {

        }
    }

    /**
     * 滑动到当前页图片的第一张时，加载上一页消息中的图片
     */
    private fun getImgMsg() {
        //        ImageContent ic;
        //        final int msgSize = mMsgIdList.size();
        //        List<Message> msgList = mConv.getMessagesFromNewest(mStart, mOffset);
        //        mOffset = msgList.size();
        //        if (mOffset > 0) {
        //            for (Message msg : msgList) {
        //                if (msg.getContentType().equals(ContentType.image)) {
        //                    mMsgIdList.add(0, msg.getId());
        //                    ic = (ImageContent) msg.getContent();
        //                    if (!TextUtils.isEmpty(ic.getLocalPath())) {
        //                        mPathList.add(0, ic.getLocalPath());
        //                    } else {
        //                        mPathList.add(0, ic.getLocalThumbnailPath());
        //                    }
        //                }
        //            }
        //            mStart += mOffset;
        //            if (msgSize == mMsgIdList.size()) {
        //                getImgMsg();
        //            } else {
        //                //加载完上一页图片后，设置当前图片仍为加载前的那一张图片
        //                mPosition = mMsgIdList.size() - msgSize;
        //                mUIHandler.sendMessage(mUIHandler.obtainMessage(SET_CURRENT_POSITION, mPosition));
        //            }
        //        }
    }

    /**
     * 初始化会话中的所有图片路径
     */
    private fun initImgPathList() {
        var ic: ImageContent
        if (mConv!!.getType() === ConversationType.chatroom) {
            messages.clear()
            messages.addAll(Message.fromJsonToCollection(intent.getStringExtra(MSG_LIST_JSON)))
            for (message in messages) {
                ic = message.content as ImageContent
                if (!TextUtils.isEmpty(ic.localPath)) {
                    mPathList.add(ic?.localPath)
                } else {
                    mPathList.add(ic.localThumbnailPath)
                }
            }
        } else {
            var msg: Message
            mMsgIdList = this.intent.getIntegerArrayListExtra(RouterUtils.Chat_Module.Chat_MsgId)
            for (msgID in mMsgIdList) {
                msg = mConv!!.getMessage(msgID)
                if (msg.contentType == ContentType.image) {
                    ic = msg.content as ImageContent
                    Log.e("result", "当前图片路径" + Gson().toJson(ic))
                    if (!TextUtils.isEmpty(ic.localPath)) {
                        Log.e("result","添加本地图")
                        mPathList.add(ic.localPath)
                    } else {
                        Log.e("result","添加本地缩略图")
                        mPathList.add(ic.localThumbnailPath)
                    }
                }
            }
        }
    }

    private fun getChatRoomMsgItem(msg: Message): Int {
        var item = 0
        for (i in 0 until messages.size) {
            if (messages[i].serverMessageId.equals(msg.serverMessageId)) {
                item = i
                break
            }
        }
        return item
    }

    protected fun initCurrentItem() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, this.getString(R.string.jmui_local_picture_not_found_toast), Toast.LENGTH_SHORT).show()
        }
        var currentItem = 0
        if (mConv!!.getType() === ConversationType.chatroom) {
            mMsg = Message.fromJson(intent.getStringExtra(MSG_JSON))
            currentItem = getChatRoomMsgItem(mMsg!!)
        } else {
            mMsg = mConv!!.getMessage(mMessageId)
            currentItem = mMsgIdList.indexOf(mMsg!!.getId())
        }
        photoView = PhotoView(mFromChatActivity, this)
        try {
            val ic = mMsg?.getContent() as ImageContent
            //如果点击的是第一张图片并且图片未下载过，则显示大图
            if (ic.localPath == null && currentItem == 0) {
                downloadImage()
            }
            val path = mPathList[currentItem]
            //如果发送方上传了原图
            if (ic.getBooleanExtra("originalPicture") != null && ic.getBooleanExtra("originalPicture")!!) {
                mLoadBtn!!.setVisibility(View.GONE)
                setLoadBtnText(ic)
                photoView?.setImageBitmap(BitmapLoader.getBitmapFromFile(path, mWidth, mHeight))
            } else {
                Picasso.with(mContext).load(File(path)).into(photoView)
            }

            mViewPager!!.setCurrentItem(currentItem)
        } catch (e: NullPointerException) {
            photoView!!.setImageResource(R.drawable.jmui_picture_not_found)
            mViewPager!!.setCurrentItem(currentItem)
        } finally {
            if (currentItem == 0) {
                mBackgroundHandler!!.sendEmptyMessage(GET_NEXT_PAGE_OF_PICTURE)
            }
        }
    }

    private val listener = object : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.getId()) {
                R.id.return_btn -> {
                    val pathArray = IntArray(mPathList.size)
                    for (i in pathArray.indices) {
                        pathArray[i] = 0
                    }
                    for (j in 0 until mSelectMap.size()) {
                        pathArray[mSelectMap.keyAt(j)] = 1
                    }
                    val intent = Intent()
                    intent.putExtra("pathArray", pathArray)
                    setResult(BaseApplication.RESULT_CODE_SELECT_PICTURE, intent)
                    finish()
                }
                R.id.pick_picture_send_btn -> {
                    mProgressDialog = ProgressDialog(mContext)
                    mProgressDialog!!.setMessage(mContext!!.getString(R.string.sending_hint))
                    mProgressDialog!!.setCanceledOnTouchOutside(false)
                    mProgressDialog!!.show()
                    mPosition = mViewPager!!.getCurrentItem()

                    if (mOriginPictureCb!!.isChecked()) {
//                        Log.i(FragmentActivity.TAG, "发送原图")
                        getOriginPictures(mPosition)
                    } else {
//                        Log.i(FragmentActivity.TAG, "发送缩略图")
                        getThumbnailPictures(mPosition)
                    }
                }
                //点击显示原图按钮，下载原图
                R.id.load_image_btn -> downloadOriginalPicture()
            }
        }
    }

    private fun downloadOriginalPicture() {
        val imgContent = mMsg!!.getContent() as ImageContent
        //如果不存在下载进度
        if (!mMsg!!.isContentDownloadProgressCallbackExists()) {
            mMsg!!.setOnContentDownloadProgressCallback(object : ProgressUpdateCallback() {
                override fun onProgressUpdate(progress: Double) {
                    val msg = mUIHandler.obtainMessage()
                    val bundle = Bundle()
                    if (progress < 1.0) {
                        msg.what = DOWNLOAD_ORIGIN_PROGRESS
                        bundle.putInt("progress", (progress * 100).toInt())
                        msg.setData(bundle)
                        msg.sendToTarget()
                    } else {
                        msg.what = DOWNLOAD_ORIGIN_COMPLETED
                        msg.sendToTarget()
                    }
                }
            })
            imgContent.downloadOriginImage(mMsg, object : DownloadCompletionCallback() {
                override fun onComplete(status: Int, desc: String, file: File) {
                    if (status == 0) {
                        imgContent.setBooleanExtra("hasDownloaded", true)
                    } else {
                        imgContent.setBooleanExtra("hasDownloaded", false)
                        if (mProgressDialog != null) {
                            mProgressDialog!!.dismiss()
                        }
                    }
                }
            })
        }
    }


    /**
     * 获得选中图片的原图路径
     *
     * @param position 选中的图片位置
     */
    private fun getOriginPictures(position: Int) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true)
        }
        mMsgIds = IntArray(mSelectMap.size())
        //根据选择的图片路径生成队列
        for (i in 0 until mSelectMap.size()) {
            createImageContent(mPathList[mSelectMap.keyAt(i)], true)
        }
    }

    /**
     * 获得选中图片的缩略图路径
     *
     * @param position 选中的图片位置
     */
    private fun getThumbnailPictures(position: Int) {
        if (mSelectMap.size() < 1) {
            mSelectMap.put(position, true)
        }
        mMsgIds = IntArray(mSelectMap.size())
        for (i in 0 until mSelectMap.size()) {
            createImageContent(mPathList[mSelectMap.keyAt(i)], false)
        }
    }

    /**
     * 根据图片路径生成ImageContent
     *
     * @param path       图片路径
     * @param isOriginal 是否发送原图
     */
    private fun createImageContent(path: String, isOriginal: Boolean) {
        val bitmap: Bitmap
        if (isOriginal || BitmapLoader.verifyPictureSize(path)) {
            val file = File(path)
            ImageContent.createImageContentAsync(file, object : ImageContent.CreateImageContentCallback() {
                override fun gotResult(status: Int, desc: String, imageContent: ImageContent) {
                    if (status == 0) {
                        if (isOriginal) {
                            imageContent.setBooleanExtra("originalPicture", true)
                        }
                        val msg = mConv!!.createSendMessage(imageContent)
                        mMsgIds!![mIndex] = msg.id
                    } else {
                        mMsgIds!![mIndex] = -1
                    }
                    mIndex++
                    if (mIndex >= mSelectMap.size()) {
                        mUIHandler.sendEmptyMessage(SEND_PICTURE)
                    }
                }
            })
        } else {
            bitmap = BitmapLoader.getBitmapFromFile(path, 720, 1280)
            ImageContent.createImageContentAsync(bitmap, object : ImageContent.CreateImageContentCallback() {
                override fun gotResult(status: Int, desc: String, imageContent: ImageContent) {
                    if (status == 0) {
                        val msg = mConv!!.createSendMessage(imageContent)
                        mMsgIds!![mIndex] = msg.id
                    } else {
                        mMsgIds!![mIndex] = -1
                    }
                    mIndex++
                    if (mIndex >= mSelectMap.size()) {
                        mUIHandler.sendEmptyMessage(SEND_PICTURE)
                    }
                }
            })
        }
    }

    public override fun onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (mDownloading) {
            mProgressDialog!!.dismiss()
        }
        val pathArray = IntArray(mPathList.size)
        for (i in pathArray.indices) {
            pathArray[i] = 0
        }
        for (i in 0 until mSelectMap.size()) {
            pathArray[mSelectMap.keyAt(i)] = 1
        }
        val intent = Intent()
        intent.putExtra("pathArray", pathArray)
        setResult(BaseApplication.RESULT_CODE_SELECT_PICTURE, intent)
        super.onBackPressed()
    }

    //每次在聊天界面点击图片或者滑动图片自动下载大图
    private fun downloadImage() {
        val imgContent = mMsg!!.getContent() as ImageContent
        if (imgContent.localPath == null) {
            //如果不存在进度条Callback，重新注册
            if (!mMsg!!.isContentDownloadProgressCallbackExists()) {
                mProgressDialog = ProgressDialog(this)
                mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                mProgressDialog!!.setCanceledOnTouchOutside(false)
                mProgressDialog!!.setIndeterminate(false)
                mProgressDialog!!.setMessage(mContext!!.getString(R.string.downloading_hint))
                mDownloading = true
                mProgressDialog!!.show()
                // 显示下载进度条
                mMsg!!.setOnContentDownloadProgressCallback(object : ProgressUpdateCallback() {

                    override fun onProgressUpdate(progress: Double) {
                        val msg = mUIHandler.obtainMessage()
                        val bundle = Bundle()
                        if (progress < 1.0) {
                            msg.what = DOWNLOAD_PROGRESS
                            bundle.putInt("progress", (progress * 100).toInt())
                            msg.setData(bundle)
                            msg.sendToTarget()
                        } else {
                            msg.what = DOWNLOAD_COMPLETED
                            msg.sendToTarget()
                        }
                    }
                })
                // msg.setContent(imgContent);
                imgContent.downloadOriginImage(mMsg, object : DownloadCompletionCallback() {
                    override fun onComplete(status: Int, desc: String, file: File) {
                        mDownloading = false
                        if (status == 0) {
                            val msg = mUIHandler.obtainMessage()
                            msg.what = DOWNLOAD_ORIGIN_IMAGE_SUCCEED
                            val bundle = Bundle()
                            bundle.putString("path", file.getAbsolutePath())
                            bundle.putInt(BaseApplication.POSITION, mViewPager!!.getCurrentItem())
                            msg.setData(bundle)
                            msg.sendToTarget()
                        } else {
                            if (mProgressDialog != null) {
                                mProgressDialog!!.dismiss()
                            }
                        }
                    }
                })
            }
        }
    }

    private class UIHandler(activity: BrowserViewPagerActivity) : Handler() {
        private val mActivity: WeakReference<BrowserViewPagerActivity>

        init {
            mActivity = WeakReference<BrowserViewPagerActivity>(activity)
        }

        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            val activity = mActivity.get()
            if (activity != null) {
                when (msg.what) {
                    DOWNLOAD_ORIGIN_IMAGE_SUCCEED -> {
                        activity!!.mProgressDialog!!.dismiss()
                        //更新图片并显示
                        val bundle = msg.data
                        activity!!.mPathList.set(bundle.getInt(BaseApplication.POSITION), bundle.getString("path"))
                        activity!!.mViewPager!!.getAdapter()!!.notifyDataSetChanged()
                        activity!!.mLoadBtn!!.setVisibility(View.GONE)
                    }
                    DOWNLOAD_PROGRESS -> activity!!.mProgressDialog!!.setProgress(msg.data.getInt("progress"))
                    DOWNLOAD_COMPLETED -> activity!!.mProgressDialog!!.dismiss()
                    SEND_PICTURE -> {
                        val intent = Intent()

                        intent.putExtra(RouterUtils.Chat_Module.Chat_MsgId, activity!!.mMsgIds)

                        activity!!.setResult(BaseApplication.RESULT_CODE_BROWSER_PICTURE, intent)
                        activity!!.finish()
                    }
                    //显示下载原图进度
                    DOWNLOAD_ORIGIN_PROGRESS -> {
                        val progress = msg.data.getInt("progress").toString() + "%"
                        activity!!.mLoadBtn?.setText(progress)
                    }
                    DOWNLOAD_ORIGIN_COMPLETED -> {
                        activity!!.mLoadBtn?.setText(activity!!.getString(R.string.download_completed_toast))
                        activity!!.mLoadBtn?.setVisibility(View.GONE)
                    }
                    INIT_ADAPTER -> {
                        activity!!.mViewPager?.setAdapter(activity!!.pagerAdapter)
                        activity!!.mViewPager?.addOnPageChangeListener(activity!!.onPageChangeListener)
                        activity!!.initCurrentItem()
                    }
                    SET_CURRENT_POSITION -> if (activity!!.mViewPager != null && activity!!.mViewPager?.getAdapter() != null) {
                        activity!!.mViewPager!!.getAdapter()!!.notifyDataSetChanged()
                        val position = msg.obj as Int
                        activity!!.mViewPager!!.setCurrentItem(position)
                    }
                }
            }
        }
    }

    private inner class BackgroundHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            when (msg.what) {
                INITIAL_PICTURE_LIST -> {
                    initImgPathList()
                    mUIHandler.sendEmptyMessage(INIT_ADAPTER)
                }
                GET_NEXT_PAGE_OF_PICTURE -> getImgMsg()
            }
        }
    }


    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.trans_finish_in)
    }

}
