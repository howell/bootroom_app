package com.tactical_foul.bootroom;

import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: scaldwell
 * Date: 10/12/13
 * Time: 9:07 PM
 */
public class GameClock implements EditGametimeDialogFragment.UpdateTimeListener {

    private TextView mClockView;
    private Activity mActivity;
    private Timer mTimer;
    private int mSeconds;

    public GameClock(TextView tv, Activity a) {
        mClockView = tv;
        mActivity = a;
        mSeconds = 0;
        init();
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setTime(int minutes, int seconds) {
        mSeconds = minutes * 60 + seconds;
        timerUpdate();
    }

    private void init() {
        mClockView.setText(seconds_to_String(mSeconds));
        mClockView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogFragment dialog = new EditGametimeDialogFragment();
                dialog.show(mActivity.getFragmentManager(), "edit gametime");
                return true;
            }
        });
        mClockView.setOnClickListener(new View.OnClickListener() {
            private boolean running = false;

            @Override
            public void onClick(View v) {
                if (running) {
                    mTimer.cancel();
                    running = false;
                } else {
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            ++mSeconds;
                            timerUpdate();
                        }
                    }, 0, 1000);
                    running = true;
                }

            }
        });
    }

    private void timerUpdate() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mClockView.setText(seconds_to_String(mSeconds));
            }
        });
    }

    /**
     * formats seconds as mm:ss, including leading 0's
     */
    private String seconds_to_String(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        String fmt = "";
        if (minutes < 10)
            fmt += '0';
        fmt += minutes;
        fmt += ":";
        if (secs < 10)
            fmt += "0";
        fmt += secs;
        return fmt;
    }
}
