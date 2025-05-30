package com.adstb.schedule.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.R;

import java.time.LocalDate;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final TextView dayOfMonth;
    public final TextView shift;
    public final LinearLayout background;
    public final TextView date;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView,CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        date = itemView.findViewById(R.id.cellDate);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        shift = itemView.findViewById(R.id.cellShiftText);
        background = itemView.findViewById(R.id.cellBackground);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), (String) date.getText());
    }
}
