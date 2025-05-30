package com.adstb.schedule.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.GlobalConstants.SPKeys;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private SharedPreferencesHelper SP;


    @Override
    public void onReceive(Context context, Intent intent) {
        //接受广播信息。
        this.SP = new SharedPreferencesHelper(context);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(GlobalConstants.TAG,"哈哈哈打印，");
            }
        };

        timer.schedule(task,0,5000);

        if (SP.getSP(SPKeys.ALARM_SWITCH).equals("true")){
            //检查是否总开关是否开启，开启才会执行。

        }

    }

}
