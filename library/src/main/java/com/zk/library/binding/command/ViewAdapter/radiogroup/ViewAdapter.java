package com.zk.library.binding.command.ViewAdapter.radiogroup;

import android.databinding.BindingAdapter;
import android.support.annotation.IdRes;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.cs.tec.library.binding.command.BindingCommand;


/**
 * Created by goldze on 2017/6/18.
 */
public class ViewAdapter {
    @BindingAdapter(value = {"onRadioCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand) {
        if (radioGroup != null) {
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    if (radioButton != null && radioButton.getText() != null&&bindingCommand!=null) {
                        bindingCommand.execute(radioButton.getText().toString());
                    }
                }
            });
        }
    }
}
