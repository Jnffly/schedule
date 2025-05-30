package com.adstb.schedule.helper;

import android.content.Context;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ShiftsHelper {
    private final Context context;
    private final SharedPreferencesHelper sp;
    private final MySQLiteOpenHelper sql;

    public ShiftsHelper (Context context){
        this.context = context;
        sp = new SharedPreferencesHelper(context);
        sql = new MySQLiteOpenHelper(context);
    }

    public String queryShift(String userName1, String dateString){
        String userName = (userName1==null) ? sp.getSP(SPKeys.SELECT_USER) : userName1;
        int mod = sql.queryFromUserByName(userName).getMod();
        String configDateString = sql.queryFromUserByName(userName).getConfigDate();

        String[] shifts;

        switch (mod){
            case 12:
                shifts = context.getResources().getStringArray(R.array.mod12);
                break;
            case 8:
                shifts = context.getResources().getStringArray(R.array.mod8);
                break;
            case 6:
                shifts = context.getResources().getStringArray(R.array.mod6);
                break;
            default:
                shifts = context.getResources().getStringArray(R.array.mod12);
        }


        LocalDate configDate = LocalDate.parse(configDateString);
        LocalDate date = LocalDate.parse(dateString);

        int diffInDays = (int) configDate.until(date, ChronoUnit.DAYS);

        if (diffInDays % mod<0){return shifts[diffInDays % mod+mod];}
        else {return shifts[diffInDays % mod];}
    }//计算日期对应的班次。



}
