package com.adstb.schedule.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.GlobalConstants.GlobalConstants;

import java.time.LocalTime;

public class AlarmEditorActivity extends AppCompatActivity {
    private ImageButton backBN;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch alarmSwitch, restSwitch;
    private TextView shiftTimeTV, dayTV, nightTV, dayHint, nightHint;
    private LocalTime shiftTime;
    private final String
            DAY_TIME = SPKeys.ALARM_DAY_TIME,
            NIGHT_TIME = SPKeys.ALARM_NIGHT_TIME,
            ALARM_SWITCH = SPKeys.ALARM_SWITCH,
            ALARM_REST = SPKeys.ALARM_REST_SWITCH,
            SHIFT_TIME = SPKeys.SHIFT_TIME,
            NONE = GlobalConstants.NONE,
            TIME_HINT = "配置时间可能有误";
    private final SharedPreferencesHelper sp = new SharedPreferencesHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_editor);

        initView();
        initOnClick();
        initData();


    }

    private void initView() {
        backBN = findViewById(R.id.AlarmEditorBackButton);
        alarmSwitch = findViewById(R.id.AlarmEditorSwitch);
        restSwitch = findViewById(R.id.AlarmEditorRest);
        shiftTimeTV = findViewById(R.id.AlarmEditorShiftTime);
        dayTV = findViewById(R.id.AlarmEditorDay);
        nightTV = findViewById(R.id.AlarmEditorNight);

        dayHint = findViewById(R.id.AlarmEditorDayHint);
        nightHint = findViewById(R.id.AlarmEditorNightHint);
    }//获取各控件对象。

    private void initOnClick() {

        AlertDialog dialogChangeRemind = new AlertDialog.Builder(this)
                .setTitle(GlobalConstants.DIALOG_TITLE)
                .setPositiveButton("我知道了", (dialog, which) -> {
                    String dayTime =  LocalTime.of(7,0).toString();
                    String nightTime =  LocalTime.of(19,0).toString();

                    dayTV.setText(dayTime);
                    sp.setSP(DAY_TIME,dayTime);

                    nightTV.setText(nightTime);
                    sp.setSP(NIGHT_TIME,nightTime);
                })
                .create();


        backBN.setOnClickListener(v -> finish());//配置返回按钮的点击事件。

        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!sp.setSP(ALARM_SWITCH, String.valueOf(isChecked))){
                Toast.makeText(this, "配置时出现了问题，请重试", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isChecked){
                return;
            }

            if (sp.getSP(DAY_TIME).equals(NONE)||sp.getSP(NIGHT_TIME).equals(NONE)){
                dialogChangeRemind.setMessage("你还有设置闹钟时间未设置哦\n（默认将会使用7点）");
                dialogChangeRemind.show();
            }
        });//配置闹钟总开关的切换监听事件。

        restSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!sp.setSP(ALARM_REST, String.valueOf(isChecked))){
                Toast.makeText(this, "配置时出现了问题，请重试", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isChecked){
                dialogChangeRemind.setMessage("休假不开启自动关闭闹钟的话，可能会影响你的休息质量。");
                dialogChangeRemind.show();
            }
        });//配置休假是否关闭闹钟的切换监听事件。

        shiftTimeTV.setOnClickListener(v -> {

            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                shiftTime = LocalTime.of(hourOfDay,minute);
                sp.setSP(SHIFT_TIME,shiftTime.toString());
                shiftTimeTV.setText(shiftTime.toString());

            },shiftTime.getHour(),shiftTime.getMinute(),true);
            dialog.show();
        });//配置上班时间的点击事件。

        dayTV.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                LocalTime selectTime = LocalTime.of(hourOfDay,minute);
                if (selectTime.isAfter(LocalTime.of(shiftTime.getHour(), shiftTime.getMinute()))
                        ||selectTime.isBefore(LocalTime.of(shiftTime.minusHours(8).getHour(),shiftTime.getMinute()))){
                    dayHint.setText(TIME_HINT);
                } else dayHint.setText("");
                sp.setSP(SHIFT_TIME,selectTime.toString());
                dayTV.setText(selectTime.toString());

            },shiftTime.minusHours(1).getHour(),shiftTime.getMinute(),true);
            dialog.show();
        });//配置白班闹钟时间的点击事件。

        nightTV.setOnClickListener(v -> {

            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                LocalTime selectTime = LocalTime.of(hourOfDay,minute);
                if (selectTime.isAfter(LocalTime.of(shiftTime.plusHours(12).getHour(),shiftTime.getMinute()))
                        ||selectTime.isBefore(LocalTime.of(shiftTime.plusHours(4).getHour(),shiftTime.getMinute()))){
                    nightHint.setText(TIME_HINT);
                } else nightHint.setText("");
                sp.setSP(NIGHT_TIME,selectTime.toString());
                nightTV.setText(selectTime.toString());

            },shiftTime.plusHours(11).getHour(),shiftTime.getMinute(),true);
            dialog.show();
        });//配置夜班闹钟时间的点击事件。



    }//配置各控件的点击事件。

    private void initData() {
        boolean alarmSwitchBool, restSwitchBool;
        String dayTime,nightTime;
        if (!sp.getSP(ALARM_SWITCH).equals(NONE)){
            alarmSwitchBool = sp.getSP(ALARM_SWITCH).equals("true");
        } else alarmSwitchBool = false;

        if (!sp.getSP(ALARM_REST).equals(NONE)){
            restSwitchBool = sp.getSP(ALARM_REST).equals("true");
        } else restSwitchBool = true;

        if (!sp.getSP(SHIFT_TIME).equals(NONE)){
            shiftTime = LocalTime.parse(sp.getSP(SHIFT_TIME));
        } else shiftTime = LocalTime.of(8,0);

        if (!sp.getSP(DAY_TIME).equals(NONE)){
            //如果有配置过白班闹钟则设置参数，并将判断提示字段是否展示。
            dayTime = sp.getSP(DAY_TIME);
            if (LocalTime.parse(dayTime).isAfter(LocalTime.of(shiftTime.getHour(), shiftTime.getMinute()))
                    ||LocalTime.parse(dayTime).isBefore(LocalTime.of(shiftTime.minusHours(8).getHour(),shiftTime.getMinute()))){
                dayHint.setText(TIME_HINT);
            } else dayHint.setText("");
        } else dayTime = "点击设置";

        if (!sp.getSP(NIGHT_TIME).equals(NONE)){
            //如果有配置过夜班闹钟则设置参数，并将判断提示字段是否展示。
            nightTime = sp.getSP(NIGHT_TIME);
            if (LocalTime.parse(nightTime).isAfter(LocalTime.of(shiftTime.plusHours(12).getHour(),shiftTime.getMinute()))
                    ||LocalTime.parse(nightTime).isBefore(LocalTime.of(shiftTime.plusHours(4).getHour(),shiftTime.getMinute()))){
                nightHint.setText(TIME_HINT);
            } else nightHint.setText("");
        } else nightTime = "点击设置";

        alarmSwitch.setChecked(alarmSwitchBool);
        restSwitch.setChecked(restSwitchBool);
        shiftTimeTV.setText(shiftTime.toString());
        dayTV.setText(dayTime);
        nightTV.setText(nightTime);

    }//数据加载。


}
