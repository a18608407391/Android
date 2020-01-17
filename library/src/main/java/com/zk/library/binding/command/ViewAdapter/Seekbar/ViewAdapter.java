package com.zk.library.binding.command.ViewAdapter.Seekbar;

import android.databinding.BindingAdapter;
import android.widget.SeekBar;

import org.cs.tec.library.binding.command.BindingCommand;

public class ViewAdapter {

    @BindingAdapter(value = {"onProgressChanged", "onStartTrackingTouch", "onStopTrackingTouch"}, requireAll = false)
    public static void onSeekbarChangeCommand(SeekBar seekBar,
                                              final BindingCommand<SeekbarDataWrapper> onProgressChanged,
                                              final BindingCommand<SeekBar> onStartTrackingTouch,
                                              final BindingCommand<SeekBar> onStopTrackingTouch) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (onProgressChanged != null) {
                    onProgressChanged.execute(new SeekbarDataWrapper(seekBar, progress, fromUser));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (onStartTrackingTouch != null) {
                    onStartTrackingTouch.execute(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (onStopTrackingTouch != null) {
                    onStopTrackingTouch.execute(seekBar);

                }
            }
        });
    }

    public static class SeekbarDataWrapper {
        public SeekBar seekBar;
        public int progress;
        public boolean fromUser;

        public SeekbarDataWrapper(SeekBar seekBar, int progress, boolean fromUser) {
            this.seekBar = seekBar;
            this.progress = progress;
            this.fromUser = fromUser;
        }
    }
}
