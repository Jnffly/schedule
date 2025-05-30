package com.adstb.schedule.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.fragment.CalendarFragment;
import com.adstb.schedule.fragment.HomeFragment;
import com.adstb.schedule.fragment.SettingFragment;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.fragment.MyFragmentVPAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private TextView homeTV, calendarTV, settingTV;
    private final MySQLiteOpenHelper mMySQLiteOpenHelper = new MySQLiteOpenHelper(this);
    private final SharedPreferencesHelper sp = new SharedPreferencesHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //主方法。
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMySQLiteOpenHelper.queryUserList();

//        sp.setSP(7,"1");

//        mMySQLiteOpenHelper.deleteDataFromUserByName("唐");

        //暂时的代码，最终发布需要删除

        alarmBroadcast();

        if (first_judgement()){
            //当非首次加载时才加载界面。

            initView();

            initFragment();
        }

    }

    private void alarmBroadcast() {
//            Intent intent = new Intent();
//            intent.setAction("alarm_broadcast_receiver");
//            intent.setPackage(getPackageName());
//            sendBroadcast(intent);

//        Intent serviceIntent = new Intent(MainActivity.this, AlarmService.class);
//        startService(serviceIntent);

    }

    private boolean first_judgement() {//检查是否为第一次使用。
        if (!sp.getSP(SPKeys.FIRST_TIME_TO_USE).equals("0")){
            new AlertDialog.Builder(this).setMessage("检测到第一次使用本软件，点击前往进行初始化配置。")
                    .setTitle(GlobalConstants.DIALOG_TITLE).setPositiveButton("前往", (dialog, which) -> {
                        startActivity(new Intent(MainActivity.this, UserEditorActivity.class));
                        finish();
                    }).setCancelable(false)
                    .show();
            return false;
        }
        return true;
    }






    private void initFragment() {//初始化fragment方法。
        mFragmentList = new ArrayList<>();

        HomeFragment fragmentHome = HomeFragment.newInstance("首页","");
        CalendarFragment fragmentCalendar = CalendarFragment.newInstance(mMySQLiteOpenHelper.queryUserList(),"");
        SettingFragment fragmentSetting = SettingFragment.newInstance("设置","");

        mFragmentList.add(fragmentHome);
        mFragmentList.add(fragmentCalendar);
        mFragmentList.add(fragmentSetting);


        MyFragmentVPAdapter mVPAdapter = new MyFragmentVPAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mVPAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面控件切换监听事件。
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onPagerSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
                
    }

    private void initView() {//初始化控件绑定方法和点击事件配置。
        homeTV = findViewById(R.id.homeTV);
        calendarTV = findViewById(R.id.calendarTV);
        settingTV = findViewById(R.id.settingTV);
        mViewPager = findViewById(R.id.vp);

        homeTV.setOnClickListener(v -> {mViewPager.setCurrentItem(0);});
        calendarTV.setOnClickListener(v -> {mViewPager.setCurrentItem(1);});
        settingTV.setOnClickListener(v -> {mViewPager.setCurrentItem(2);});
        //配置顶部导航栏点击切换界面事件。


//        if (savedInstanceState == null){
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.fcv, MainFragment.class, null)
//                    .setReorderingAllowed(true)
//                    .commit();
//        }
        //进行初始化判定，当程序第一次运行的时候才会添加fragment，避免每次旋转时重复添加。
        //该方法为静态创建fragment服务，已废弃。

    }

    private void onPagerSelected(int position) {//配置页面滑动切换界面带动导航栏更新。
        switch (position) {
            case 0:
                homeTV.setTextColor(Color.WHITE);
                calendarTV.setTextColor(Color.parseColor("#ebebeb"));
                settingTV.setTextColor(Color.parseColor("#ebebeb"));

                homeTV.setTextSize(25f);
                calendarTV.setTextSize(20f);
                settingTV.setTextSize(20f);
                break;
            case 1:
                homeTV.setTextColor(Color.parseColor("#ebebeb"));
                calendarTV.setTextColor(Color.WHITE);
                settingTV.setTextColor(Color.parseColor("#ebebeb"));

                homeTV.setTextSize(20f);
                calendarTV.setTextSize(25f);
                settingTV.setTextSize(20f);
                break;
            case 2:
                homeTV.setTextColor(Color.parseColor("#ebebeb"));
                calendarTV.setTextColor(Color.parseColor("#ebebeb"));
                settingTV.setTextColor(Color.WHITE);

                homeTV.setTextSize(20F);
                calendarTV.setTextSize(20f);
                settingTV.setTextSize(25f);
                break;
        }
    }


}