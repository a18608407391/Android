package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.GET_NICKNAME
import com.elder.zcommonmodule.Utils.NameLengthFilter
import com.example.drivermodule.Activity.Team.TeamChangeNameActivity
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamSettingViewModel.Companion.REQUEST_TEAM_NAME
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_team_changename.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString


class ChangeTeamNameViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        teamChangeNameActivity.finish()
    }

    override fun onComponentFinish(view: View) {
        var nickname = teamChangeNameActivity.nickname_et.text.toString().trim()
        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(context, getString(R.string.teamname_no_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (String_length(nickname) < 4) {
            Toast.makeText(context, getString(R.string.teamname_not_enough), Toast.LENGTH_SHORT).show()
            return
        }
        var intent = Intent()
        intent.putExtra("info", teamChangeNameActivity.nickname_et.text.toString())
        teamChangeNameActivity.setResult(REQUEST_TEAM_NAME, intent)
        finish()
    }

    var NickName = ObservableField<String>()
    var component = TitleComponent()
    lateinit var teamChangeNameActivity: TeamChangeNameActivity
    fun inject(teamChangeNameActivity: TeamChangeNameActivity) {
        this.teamChangeNameActivity = teamChangeNameActivity
        component.arrowVisible.set(false)
        NickName.set(teamChangeNameActivity.info?.redisData?.teamName)
        component.title.set(getString(R.string.change_team_nickname))
        component.setCallBack(this)
        teamChangeNameActivity.nickname_et.filters = arrayOf(NameLengthFilter(20))
        teamChangeNameActivity.nickname_et.setSelection(0)
    }

    fun String_length(value: String): Int {
        var valueLength = 0
        val chinese = "[\u4e00-\u9fa5]"
        for (i in 0 until value.length) {
            val temp = value.substring(i, i + 1)
            if (temp.matches(chinese.toRegex())) {
                valueLength += 2
            } else {
                valueLength += 1
            }
        }
        return valueLength
    }
    fun onClick(view:View){
        NickName.set("")
    }
}