package com.zk.library.binding.command.ViewAdapter.checkbox;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import org.cs.tec.library.binding.command.BindingCommand;


/**
 * Created by goldze on 2017/6/16.
 */

public class ViewAdapter {
    /**
     * @param bindingCommand //绑定监听
     */
    @SuppressWarnings("unchecked")
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void setCheckedChanged(final CheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bindingCommand.execute(b);
            }
        });
    }

    @BindingAdapter(value = {"onCheckedChangedInfoCommand", "checkboxPosition"}, requireAll = false)
    public static void setCheckedInfoChanged(final CheckBox checkBox, final BindingCommand<CheckBoxInfo> bindingCommand, final int position) {
        if (checkBox.getTag() == null ) {
            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("result", b + "" + position + "执行");
                    CheckBoxInfo info = new CheckBoxInfo();
                    info.box = checkBox;
                    info.flag = b;
                    info.position = position;
                    bindingCommand.execute(info);
                }
            });
        }else if((int)(checkBox.getTag()) != position){

        }
    }
}
