package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.ChatRoomViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChartRoomBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils


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
    var mAtAllMsgId : Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_AtAllId)
    @JvmField
    var fromGroup : Boolean = false


    @Autowired(name = RouterUtils.Chat_Module.Chat_MEMBERS_COUNT)
    @JvmField
    var MEMBERS_COUNT : Int = 0
    override fun onClick(v: View?) {

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
}