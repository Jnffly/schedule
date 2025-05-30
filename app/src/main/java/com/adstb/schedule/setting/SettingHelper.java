package com.adstb.schedule.setting;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.R;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingHelper implements SettingAdapter.OnItemListener{

    private final RecyclerView settingRV;
    private final Context context;
    private final SharedPreferencesHelper sp;
    private final MySQLiteOpenHelper sql;


    public SettingHelper(RecyclerView settingRV, Context context){
        this.settingRV = settingRV;
        this.context = context;
        this.sp = new SharedPreferencesHelper(context);
        this.sql = new MySQLiteOpenHelper(context);
    }

    public void setSettingRV(){
        ArrayList<Integer> images = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        images.add(R.drawable.setting_userlist);
        titles.add("用户编辑");

        images.add(R.drawable.setting_alarm);
        titles.add("闹钟配置");

        images.add(R.drawable.setting_comparing);
        titles.add("班次调整");

        images.add(R.drawable.setting_about);
        titles.add("关于软件");

        SettingAdapter settingAdapter = new SettingAdapter(images,titles,this,context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,3);
        settingRV.setLayoutManager(layoutManager);
        settingRV.setAdapter(settingAdapter);

    }

    @Override
    public void onItemClick(int position, String titleText) {
        switch (titleText)
        {
            case "用户编辑":
                userListEdit();
                break;
            case "闹钟配置":
                alarmListEdit();
                break;
            case "班次调整":
                compareListEdit();
                break;
            case "关于软件":
                aboutListEdit();
                break;

        }


    }




    private void userListEdit(){
        String[] userList1 = sql.queryUserList();
        String[] userList = new String[userList1.length+1];
        System.arraycopy(userList1, 0, userList, 0, userList1.length);
        userList[userList.length-1] = "添加用户";

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("用户编辑").setItems(userList, (dialog1, which) -> {
                    Intent intent = new Intent();
                    intent.setClassName("com.adstb.schedule","com.adstb.schedule.activity.UserEditorActivity");
                    intent.putExtra("userName",userList[which]);
                    context.startActivity(intent);
                }).create();
        dialog.show();
    }//用户编辑 点击事件。

    private void alarmListEdit(){
        Intent intent = new Intent();
        intent.setClassName("com.adstb.schedule","com.adstb.schedule.activity.AlarmEditorActivity");
        context.startActivity(intent);
    }//闹钟编辑 点击事件。

    private void compareListEdit() {
        String[] compareList1 = sql.queryCompareList();
        String[] compareList = new String[compareList1.length+1];
        System.arraycopy(compareList1, 0, compareList, 0, compareList1.length);
        compareList[compareList.length-1] = "新建配置";

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("班次调整")
                .setItems(compareList, (dialog1, which) -> {
                    Intent intent = new Intent();
                    intent.setClassName("com.adstb.schedule","com.adstb.schedule.activity.CompareEditorActivity");
                    intent.putExtra("groupName",compareList[which]);
                    context.startActivity(intent);
                })
                .create();

        dialog.show();

    }//班次调整 点击事件。

    private void aboutListEdit() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Schedule III").setMessage(
                        "当前版本：3.0.1\n" +
                        "制作者：stb\n" +
                        "鸣谢以下担任单方面技术顾问：\n" +
                                " · CSDN\n" +
                        " · B站：子林ANDROID")
                .setPositiveButton("我知道了", (dialog1, which) -> dialog1.dismiss())
                .setNegativeButton("检查更新", ((dialog1, which) -> {
                    Toast.makeText(context, "软件不能从此侧检查更新", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                }))
                .create();
        dialog.show();
    }//关于软件 点击事件。


}
