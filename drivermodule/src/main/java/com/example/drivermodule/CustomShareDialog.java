package com.example.drivermodule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.MotionEvent;

public class CustomShareDialog extends BottomSheetDialog {
    public CustomShareDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        this.setContentView(R.layout.dialog_share_drag);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return super.onTouchEvent(event);
    }

}
