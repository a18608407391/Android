package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.View.ChatUtils.EmoticonsEditText
import com.cstec.administrator.chart_module.ViewModel.ChooseAtMemberViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChooseAtMemberBinding
import android.content.Intent
import com.cstec.administrator.chart_module.Model.ParentLinkedHolder
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils


class ChooseAtMemberActivity : ChartBaseActivity<ActivityChooseAtMemberBinding, ChooseAtMemberViewModel>() {
    override fun initVariableId(): Int {
        return BR.choose_member_viewmodel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_choose_at_member
    }

    override fun initViewModel(): ChooseAtMemberViewModel? {
        return ViewModelProviders.of(this)[ChooseAtMemberViewModel::class.java]
    }


    companion object {
        private var textParentLinkedHolder: ParentLinkedHolder<EmoticonsEditText>? = null

        fun show(textWatcher: ChatRoomActivity, edit: EmoticonsEditText, id: String) {
            synchronized(ChooseAtMemberActivity::class.java) {
                var holder = ParentLinkedHolder(edit)
                textParentLinkedHolder = holder.addParent(textParentLinkedHolder)
            }

            val intent = Intent(textWatcher, ChooseAtMemberActivity::class.java)
            intent.putExtra(RouterUtils.Chat_Module.Chat_GROUP_ID, java.lang.Long.parseLong(id))
            textWatcher.startActivityForResult(intent, BaseApplication
                    .REQUEST_CODE_AT_MEMBER)
        }

    }


}