package com.adstb.schedule.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.calendar.CalendarHelper;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;


public class CalendarFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView monthText;
    private ImageButton previousMonth,nextMonth;
    private Spinner userListSP;
    private RecyclerView calendarRecyclerView;
    private GridLayout calendarTitleGrid;
    private FloatingActionButton backBN;

    private String[] mParam1;
    private String mParam2;
    private SharedPreferencesHelper sp;
    private final String
            SELECT_DATE = SPKeys.SELECT_DATE,
            SELECT_USER = SPKeys.SELECT_USER;



    public CalendarFragment() {
        
    }

    public static CalendarFragment newInstance(String[] param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getStringArray(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        sp = new SharedPreferencesHelper(requireContext());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initView(view);

        initTitleGrid();

        initOnClick();

        sp.setSP(SELECT_DATE,LocalDate.now().toString());
        initMonthVP();

        initUserList(requireContext());



        return view;
    }//首次进入主界面加载事件。


    private void initView(View view) {
        monthText = view.findViewById(R.id.CalendarMonthTV);
        calendarRecyclerView = view.findViewById(R.id.CalendarRecyclerView);
        calendarTitleGrid = view.findViewById(R.id.CalendarTitleGrid);
        previousMonth = view.findViewById(R.id.CalendarPreviousMonth);
        nextMonth = view.findViewById(R.id.CalendarNextMonth);
        userListSP = view.findViewById(R.id.userSelectSP);
        backBN = view.findViewById(R.id.CalendarBackBN);
    }

    private void initTitleGrid() {
        //标题星期数加载。
        for (int i=0;i<7;i++){
            TextView weekItem = new TextView(getContext());
            GridLayout.LayoutParams titleParams = new GridLayout.LayoutParams();
            titleParams.height = 100;
            titleParams.setMargins(5, 5, 5, 5);
            titleParams.columnSpec = GridLayout.spec(i, 1f);
            weekItem.setText(GlobalConstants.weekIntToCharacter[i]);
            weekItem.setTextSize(18f);
            weekItem.setTextColor(Color.BLACK);
            weekItem.setGravity(Gravity.CENTER);
            calendarTitleGrid.addView(weekItem,titleParams);}
    }

    private void initUserList(Context context) {//用户列表加载和切换监听。

        userListSP.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, mParam1));

        userListSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp.setSP(SELECT_USER,mParam1[position]);
                initMonthVP();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initOnClick() {
        previousMonth.setOnClickListener(v -> {
            sp.setSP(SELECT_DATE,
                    LocalDate.parse(sp.getSP(SELECT_DATE)).minusMonths(1).toString());

            initMonthVP();
        });//配置上个月点击按钮事件。

        nextMonth.setOnClickListener(v -> {
            sp.setSP(SELECT_DATE,
                    LocalDate.parse(sp.getSP(SELECT_DATE)).plusMonths(1).toString());

            initMonthVP();
        });//配置下个月点击按钮事件。

        backBN.setOnClickListener(v -> {
            sp.setSP(SELECT_DATE, LocalDate.now().toString());
            initMonthVP();
        });//配置返回今天点击按钮事件。

    }



    private void initMonthVP() {
        //日历主体加载。
        CalendarHelper calendarHelper = new CalendarHelper(calendarRecyclerView,monthText,backBN, requireContext());
        calendarHelper.setMonthView();
    }


}