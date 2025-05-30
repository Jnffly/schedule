package com.adstb.schedule.CompareList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.bean.Shift;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.helper.ShiftsHelper;

import java.time.LocalDate;
import java.util.ArrayList;

public class CompareListAdapter extends RecyclerView.Adapter<CompareListViewHolder> {
    private final ArrayList<String> items;
    private final OnItemListener onItemListener;
    private final Context context;

    private final MySQLiteOpenHelper sql;
    private final SharedPreferencesHelper sp;
    private final ShiftsHelper shiftsHelper;

    public CompareListAdapter(ArrayList<String> items, OnItemListener onItemListener, Context context) {
        this.items = items;
        this.onItemListener = onItemListener;
        this.context = context;
        sql = new MySQLiteOpenHelper(context);
        sp = new SharedPreferencesHelper(context);
        shiftsHelper = new ShiftsHelper(context);
    }

    @NonNull
    @Override
    public CompareListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (100);
        return new CompareListViewHolder(view,onItemListener);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull CompareListViewHolder holder, int position) {
        LocalDate date = dates.get(position);

        LocalDate selDate = LocalDate.parse(sp.getSP(SPKeys.SELECT_DATE));

        holder.date.setText(date.toString());
        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
        try{
            holder.shift.setText(String.valueOf(shiftsHelper.queryShift(null,date.toString()).charAt(0)));
        } catch (Exception e){
            Toast.makeText(context, "Exception:"+e, Toast.LENGTH_SHORT).show();
            Log.d(GlobalConstants.TAG, String.valueOf(e));
        }
        //按照默认值配置，后续有变动直接覆写。

        if (date.getMonthValue()!=selDate.getMonthValue()){
            //如果当前日期的月份 ≠ 选择日期的月份（上个月或次月），则文本颜色改为 灰色。
            holder.dayOfMonth.setTextColor(R.color.grey);
            holder.shift.setTextColor(R.color.grey);
        }

        if (date.equals(selDate)){
            //如果当前日期＝选择日期，则对其外观进行调整。
            holder.dayOfMonth.setTextColor(R.color.white);
            holder.shift.setTextColor(R.color.white);
            holder.background.setBackgroundColor(R.color.theme);
        }

        Shift shiftBean = sql.queryShiftChangedByDate(sp.getSP(SPKeys.SELECT_USER),String.valueOf(date));
        String shiftChanged = shiftBean.getShiftChanged();
        if (shiftChanged!=null){
            //通过当前日期查询是否有调班情况，如有则覆写班次的文本内容为调班后的文本内容，同时改变其字体颜色以便提醒。
            holder.shift.setText(shiftChanged);
            holder.shift.setTextColor(R.color.red);
        }


    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, String dateText);
    }
}
