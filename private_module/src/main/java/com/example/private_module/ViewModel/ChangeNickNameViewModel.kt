package com.example.private_module.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.GET_NICKNAME
import com.elder.zcommonmodule.Utils.NameLengthFilter
import com.example.private_module.Activity.ChangeNickNameActivity
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_change_nickname.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString


class ChangeNickNameViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        changeNickNameActivity.finish()
    }

    override fun onComponentFinish(view: View) {
        var nickname = changeNickNameActivity.nickname_et.text.toString()
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(context, getString(R.string.nickname_no_empty), Toast.LENGTH_SHORT).show()
            return
        }
        var intent = Intent()
        intent.putExtra("nickname", changeNickNameActivity.nickname_et.text.toString())
        changeNickNameActivity.setResult(GET_NICKNAME, intent)
        finish()
    }

    var NickName = ObservableField<String>()
    lateinit var changeNickNameActivity: ChangeNickNameActivity
    fun inject(changeNickNameActivity: ChangeNickNameActivity) {
        this.changeNickNameActivity = changeNickNameActivity
        component.arrowVisible.set(false)
        NickName.set(changeNickNameActivity.nickname)
        component.title.set(getString(R.string.change_nickname))
        component.setCallBack(this)
        changeNickNameActivity.nickname_et.setSelection(0)
    }

    var component = TitleComponent()


    fun onClick(view: View) {
        NickName.set("")
    }


}