package com.adstb.schedule.helper;

import android.content.Context;

import androidx.annotation.IntDef;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

import kotlin.annotation.Retention;

public class SharedPreferencesHelper {
    private final Context context;
    private final String CONFIG = "configuration";


    public SharedPreferencesHelper(Context context){
        this.context = context;
    }


    /***
     *字段选择
     * @return 返回你选择字段的内容。
     * 日历界面：
     * SELECT_USER = 选择的用户；
     * SELECT_DATE = 日选择的日期；
     * 闹钟编辑：
     * shiftTime = 上班时间；
     * ALARM_SWITCH =  闹钟总体开关；
     * ALARM_DAY_TIME = 白班闹钟时间；
     * ALARM_NIGHT_TIME = 夜班闹钟时间；
     * ALARM_REST_SWITCH = 休假是否关闭所有闹钟；
     * firstTimeToUse = 是否为第一次使用；
     */

    public String getSP(String mode){

        return context.getSharedPreferences(CONFIG,Context.MODE_PRIVATE)
                .getString(mode,"none");
    }

    /***
     *字段选择
     * @return 返回你选择字段的内容。
     * 日历界面：
     * SELECT_USER = 选择的用户；
     * SELECT_DATE = 日选择的日期；
     * 闹钟编辑：
     * shiftTime = 上班时间；
     * ALARM_SWITCH =  闹钟总体开关；
     * ALARM_DAY_TIME = 白班闹钟时间；
     * ALARM_NIGHT_TIME = 夜班闹钟时间；
     * ALARM_REST_SWITCH = 休假是否关闭所有闹钟；
     * firstTimeToUse = 是否为第一次使用；
     */
    public Boolean setSP(String mode, String content){
        return context.getSharedPreferences(CONFIG,Context.MODE_PRIVATE).edit()
                .putString(mode,content).commit();
    }

}
