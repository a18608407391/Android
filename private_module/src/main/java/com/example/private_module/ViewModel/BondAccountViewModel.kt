package com.example.private_module.ViewModel

import android.databinding.ObservableField
import android.view.View
import com.example.private_module.R
import com.zk.library.Base.BaseViewModel
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class BondAccountViewModel : BaseViewModel() {
    var loginname = ObservableField<String>()
    var loginpass = ObservableField<String>()
    var passVisible = ObservableField<Boolean>(false)
    var verifyCode = ObservableField<String>()
    var text = ObservableField<String>(getString(R.string.send_again))
    var textColor = ObservableField<Int>(getColor(R.color.againSendColor))
    fun onClick(view: View) {

    }


    var phoneTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            loginname.set(t)
        }
    })


    var passwordTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            loginpass.set(t)
        }
    })
    var verifyTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            verifyCode.set(t)
        }
    })
}