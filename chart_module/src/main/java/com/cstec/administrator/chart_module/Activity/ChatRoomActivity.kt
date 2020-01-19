package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.ChatRoomViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChartRoomBinding
import com.zk.library.Utils.RouterUtils
import com.cstec.administrator.chart_module.View.ChatUtils.EmoticonsKeyboardUtils
import com.cstec.administrator.chart_module.Adapter.ChattingListAdapter


@Route(path = RouterUtils.Chat_Module.Chat_AC)
class ChatRoomActivity : ChartBaseActivity<ActivityChartRoomBinding, ChatRoomViewModel>(), View.OnClickListener {


    @Autowired(name = RouterUtils.Chat_Module.Chat_App_Key)
    @JvmField
    var mTargetAppKey: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_DRAFT)
    @JvmField
    var draft: String? = null

    @Autowired(name = RouterUtils.Chat_Module.Chat_TARGET_ID)
    @JvmField
    var mTargetId: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_GROUP_ID)
    @JvmField
    var mGroupId: Long = 0L

    @Autowired(name = RouterUtils.Chat_Module.Chat_CONV_TITLE)
    @JvmField
    var mTitle: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_AtId)
    @JvmField
    var mAtMsgId: Int = 0


    @Autowired(name = RouterUtils.Chat_Module.Chat_AtAllId)
    @JvmField
    var mAtAllMsgId: Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_AtAllId)
    @JvmField
    var fromGroup: Boolean = false


    @Autowired(name = RouterUtils.Chat_Module.Chat_MEMBERS_COUNT)
    @JvmField
    var MEMBERS_COUNT: Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_Room_ID)
    @JvmField
    var chatRoomId: Long = 0

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.jmui_return_btn -> mViewModel?.returnBtn()
            R.id.jmui_right_btn ->
                //如果是聊天室
                if (mViewModel?.isChatRoom!!) {
                    mViewModel?.startChatRoomActivity(chatRoomId)
                } else {
                    mViewModel?.startChatDetailActivity(mTargetId, mTargetAppKey, mGroupId)
                }
            R.id.jmui_at_me_btn -> {
                if (mViewModel?.mUnreadMsgCnt!! < ChattingListAdapter.PAGE_MESSAGE_COUNT) {
                    var position = ChattingListAdapter.PAGE_MESSAGE_COUNT + mAtMsgId - mViewModel?.mConv?.latestMessage?.id!!
                    mViewModel?.mChatView!!.setToPosition(position)
                } else {
                    mViewModel?.mChatView!!.setToPosition(mAtMsgId + mViewModel?.mUnreadMsgCnt!! - mViewModel?.mConv!!.latestMessage.id)
                }
            }
        }
    }

    override fun initVariableId(): Int {
        return BR.char_room_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_chart_room
    }


    override fun initViewModel(): ChatRoomViewModel? {
        return ViewModelProviders.of(this)[ChatRoomViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            var isConsum = mViewModel?.ekBar!!.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mViewModel?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}