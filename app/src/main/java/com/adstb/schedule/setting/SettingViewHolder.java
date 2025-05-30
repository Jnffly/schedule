package com.adstb.schedule.setting;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.R;

public class SettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final ImageView image;
    private final TextView title;
    private final SettingAdapter.OnItemListener onItemListener;

    public SettingViewHolder(@NonNull View itemView, SettingAdapter.OnItemListener onItemListener) {
        super(itemView);
        image = itemView.findViewById(R.id.settingIcon);
        title = itemView.findViewById(R.id.settingTitle);


        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition() , (String) title.getText());
    }
}
