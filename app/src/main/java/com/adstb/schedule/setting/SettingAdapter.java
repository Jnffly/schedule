package com.adstb.schedule.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.R;

import java.util.ArrayList;

public class SettingAdapter extends RecyclerView.Adapter<SettingViewHolder> {
    private final ArrayList<Integer> images;
    private final ArrayList<String> titles;
    private final OnItemListener onItemListener;
    private final Context context;

    public SettingAdapter(ArrayList<Integer> images, ArrayList<String> titles, OnItemListener onItemListener,Context context){
        this.images = images;
        this.titles = titles;
        this.onItemListener = onItemListener;
        this.context = context;
    }


    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.setting_cell,parent,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * 0.3);
        return new SettingViewHolder(view,onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        ImageView imageIV = holder.itemView.findViewById(R.id.settingIcon);
        TextView titleTV = holder.itemView.findViewById(R.id.settingTitle);
        LinearLayout background = holder.itemView.findViewById(R.id.settingBG);

        imageIV.setImageResource(images.get(position));
        titleTV.setText(titles.get(position));
    }

    public interface OnItemListener{
        void onItemClick(int position, String titleText);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
