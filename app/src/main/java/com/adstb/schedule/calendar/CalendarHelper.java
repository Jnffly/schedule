package com.adstb.schedule.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.bean.Shift;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.helper.ShiftsHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class CalendarHelper implements CalendarAdapter.OnItemListener{
    public final LocalDate selectDate;
    private final RecyclerView calendarRecyclerView;
    private final TextView monthText;
    private final FloatingActionButton backBN;
    private final Context context;
    private final SharedPreferencesHelper sp;
    private final MySQLiteOpenHelper sql;
    private final ShiftsHelper shiftsHelper;
    private final String SELECT_DATE = SPKeys.SELECT_DATE;

    public CalendarHelper(RecyclerView calendarRecyclerView,
                          TextView monthText, FloatingActionButton backBN, Context context) {
        this.calendarRecyclerView = calendarRecyclerView;
        this.monthText = monthText;
        this.backBN = backBN;
        this.context = context;
        sp = new SharedPreferencesHelper(context);
        sql = new MySQLiteOpenHelper(context);
        shiftsHelper = new ShiftsHelper(context);
        this.selectDate = LocalDate.parse(sp.getSP(SELECT_DATE));
    }

    public void setMonthView() {

        backBN.setVisibility((selectDate.toString().equals(LocalDate.now().toString())) ? View.GONE : View.VISIBLE);

        monthText.setText(monthYearFromDate(selectDate));

        ArrayList<LocalDate> dates = new ArrayList<>();
        //创建字符串组。

        int presentMonth = selectDate.getMonthValue();
        int presentYear = selectDate.getYear();
        LocalDate firstDayOfPresentMonth = LocalDate.of(presentYear,presentMonth,1);
        int differenceOfPreviousMonth = 7-firstDayOfPresentMonth.getDayOfWeek().getValue();


        for (int i =differenceOfPreviousMonth;i>0;i--){
            dates.add(firstDayOfPresentMonth.minusDays(i));
        }//上个月末加载。

        for (int i =0;i<42-differenceOfPreviousMonth;i++){
            dates.add(firstDayOfPresentMonth.plusDays(i));
        }//本月和次月加载

        CalendarAdapter calendarAdapter = new CalendarAdapter(dates,this,context);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        //月份列表配置。
    }//配置界面显示。

        private String monthYearFromDate(LocalDate date){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
            return date.format(formatter);
        }//日期格式转换。

    @Override
    public void onItemClick(int position, String dateText) {

        if (String.valueOf(sp.getSP(SELECT_DATE)).equals(dateText)){
            String selUser = sp.getSP(SPKeys.SELECT_USER);

            String changedShift = sql.queryShiftChangedByDate(sp.getSP(SPKeys.SELECT_USER),dateText).getShiftChanged();
            String defaultShift = String.valueOf(shiftsHelper.queryShift(null,dateText).charAt(0));

            String selShift = (changedShift==null) ? defaultShift : String.valueOf(changedShift.charAt(0));
            int checkItem;

            if (selShift.equals(defaultShift)){
                checkItem = 0;
            } else {
                switch (selShift){
                    case "白":
                        checkItem = 1;
                        break;
                    case "夜":
                        checkItem = 2;
                        break;
                    default:
                        checkItem = 3;
                }
            }
            String[] shiftList = {defaultShift+"(默认)","白","夜","休"};
            AlertDialog dialog = new AlertDialog.Builder(context).setTitle(dateText+"："+selShift)
                    .setSingleChoiceItems(shiftList, checkItem, (dialog1, which) -> {
                        if (which==0|| Objects.equals(shiftList[which], defaultShift)){
                            if (sql.deleteDataFromShiftByDate(selUser,String.valueOf(selectDate)) == -1){
                                Toast.makeText(context, "修改失败。", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Shift shift = new Shift();
                            shift.setName(selUser);
                            shift.setDate(String.valueOf(selectDate));
                            shift.setShiftChanged(shiftList[which]);
                            if (sql.setShiftByDate(shift) == -1){
                                Toast.makeText(context, "修改失败。", Toast.LENGTH_SHORT).show();
                            }
                        }

                        setMonthView();

                    }).setPositiveButton("完成", (dialog12, which) -> dialog12.dismiss())
                    .create();
            dialog.show();

        }

        else {
            sp.setSP(SPKeys.SELECT_DATE,dateText);
            setMonthView();}


        backBN.setVisibility((sp.getSP(SELECT_DATE).equals(LocalDate.now().toString()))
                ? View.GONE : View.VISIBLE);


    }//日期点击事件。


}
