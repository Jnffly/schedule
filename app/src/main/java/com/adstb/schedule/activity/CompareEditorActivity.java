package com.adstb.schedule.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adstb.schedule.GlobalConstants.GlobalConstants;
import com.adstb.schedule.R;
import com.adstb.schedule.bean.Compare;
import com.adstb.schedule.bean.Shift;
import com.adstb.schedule.bean.User;
import com.adstb.schedule.calendar.CalendarAdapter;
import com.adstb.schedule.calendar.CalendarViewHolder;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.helper.ShiftsHelper;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class CompareEditorActivity extends AppCompatActivity {
    private ImageButton backBN;
    private String GROUP_NAME;
    private final String
            DEF_CONTENT = "点击选择";
    private TextView CheckedUsersTV, MonthTV, nameCheck;
    private EditText NameET;
    private TableLayout HeaderTL;
    private RecyclerView MainRV;
    private Button NameBN;
    private final MySQLiteOpenHelper sql = new MySQLiteOpenHelper(this);
    private final ShiftsHelper sh = new ShiftsHelper(this);
    private Compare compare = new Compare();
    private Boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compare_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;});


        Intent intent = getIntent();
        GROUP_NAME = intent.getStringExtra("groupName");

        assert GROUP_NAME != null;
        isEdit = !GROUP_NAME.equals("新建配置");

        initView();

        initData();

        initOnClick();

        initMainerTL();



    }



    private void initView() {
        backBN = findViewById(R.id.CompareEditorBackButton);
        CheckedUsersTV = findViewById(R.id.CompareEditorCheckedUsers);
        MonthTV = findViewById(R.id.CompareEditorMonth);
        NameET = findViewById(R.id.CompareEditorNameET);
        NameBN = findViewById(R.id.CompareEditorNameOperationButton);
        nameCheck = findViewById(R.id.CompareEditorNameNotice);
        MainRV = findViewById(R.id.CompareEditorMainerRV);
        HeaderTL = findViewById(R.id.CompareEditorHeaderTL);

    }//获取控件对象方法。

    @SuppressLint("SetTextI18n")
    private void initData() {
        if (!isEdit){
            CheckedUsersTV.setText(DEF_CONTENT);
            MonthTV.setText(DEF_CONTENT);
            NameET.setText(null);
            NameBN.setText("创建");
        }//如果是通过 新建配置 选项进入则设置以上参数。
        else {
            compare = sql.queryFromCompareByGroupName(GROUP_NAME);

            CheckedUsersTV.setText(compare.getMembers());
            MonthTV.setText(compare.getRealMonth());
            NameET.setText(compare.getGroupName());
            NameBN.setText("更新");

        }//如果是通过 某配置方法 进入则设置以上参数。

    }//加载数据方法。

    @SuppressLint("SetTextI18n")
    private void initOnClick() {
        backBN.setOnClickListener(v -> finish());//配置 返回按钮 点击事件。

        NameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String nameInputChanged = s.toString().replaceAll(" ","");
                compare = sql.queryFromCompareByGroupName(nameInputChanged);

                if (nameInputChanged.isEmpty()||nameInputChanged.equals("新建配置")){
                    //判断姓名是否为空 或非法。
                    nameCheck.setText("名字不能为空或非法名");
                    nameCheck.setVisibility(View.VISIBLE);
                    NameBN.setVisibility(View.GONE);
                    return;
                }


                if (isEdit){
                    if (compare.getGroupName()!=null&& !Objects.equals(compare.getGroupName(), GROUP_NAME)){
                        //判断是否占用别人的名字。
                        nameCheck.setText("已存在同名的其他用户");
                        nameCheck.setVisibility(View.VISIBLE);
                        NameBN.setVisibility(View.GONE);
                        return;
                    }
                }
                else if (compare.getGroupName()!=null) {
                    //判断姓名是否存在。
                    nameCheck.setText("该名字已存在");
                    nameCheck.setVisibility(View.VISIBLE);
                    NameBN.setVisibility(View.GONE);
                    return;
                }

                nameCheck.setVisibility(View.GONE);
                NameBN.setVisibility(View.VISIBLE);
                compare.setGroupName(nameInputChanged);
            }
        });//配置 配置名编辑框 更新监听事件。

        NameBN.setOnClickListener(v -> {
            if (CheckedUsersTV.getText().toString().equals(DEF_CONTENT)
                    || MonthTV.getText().toString().equals(DEF_CONTENT)
                    || NameET.getText().toString().isEmpty() ){
                Toast.makeText(this, "请完成：配置名输入、选择用户、起始和结束日期！",
                        Toast.LENGTH_SHORT).show();
                return;
            }//无论哪个操作状态，都要检查是否已选择 用户、起始和结束日期。



            if (sql.insertOrCreateGroupNameToCompare(compare)){
                Toast.makeText(this, "创建成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "创建失败（sql处的问题）", Toast.LENGTH_SHORT).show();
            }
        });//配置 配置创建（更新）按钮 点击事件。


        CheckedUsersTV.setOnClickListener(v -> {
            String[] userList = sql.queryUserList();
            int totalUser = userList.length;
            boolean[] checkedUser = new boolean[totalUser];

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("选择查阅的用户。").setMultiChoiceItems(userList, checkedUser, (dialog1, which, isChecked) -> {
                        checkedUser[which] = isChecked;
                    })
                    .setPositiveButton("完成", (dialog12, which) -> {

                        StringBuilder membersBuilder = new StringBuilder();

                        for (int i = 0; i < totalUser; i++){
                            if (checkedUser[i]){
                                membersBuilder.append(userList[i]).append(" ");
                            }
                        }

                        String members = membersBuilder.toString();
                        //写入compare内 用户成员、用户成员 数量的信息，并开始加载主体部分。
                        compare.setMemberAmount(members.split(compare.SPLIT).length);
                        compare.setMembers(members);
                        initMainerTL();
                        CheckedUsersTV.setText(members);
                    }).create();
            dialog.show();

        });//配置 选择用户 点击事件。

        MonthTV.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this);
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener( v1 -> {
                int year = dialog.getDatePicker().getYear();
                int monthOfYear = dialog.getDatePicker().getMonth()+1;
                LocalDate month = LocalDate.of(year,monthOfYear,1);
                compare.setMonth(month.toString());
                MonthTV.setText(year + "-" + monthOfYear);
                initMainerTL();
                dialog.dismiss();
            });


        });//配置 选择月份 点击事件。

    }//配置控件对象点击或监听事件方法。

    private void initMainerTL() {
        if (!compare.isReady()){
            Log.d(GlobalConstants.TAG,"compare数据未准备齐全。数据内容：" + compare.toString());
            return;
        }//如果数据未准备齐全则返回不再执行。

        //获取所需数据：
        String[] checkedUsers = compare.getMembersArray();
        int col = compare.getMemberAmount() + 1;
        LocalDate month = LocalDate.parse(compare.getMonth());

        MainRV.setLayoutManager(new GridLayoutManager(this,col));
        Adapter adapter = new

        MainRV.setStretchAllColumns(true);
        HeaderTL.setStretchAllColumns(true);

        //加载首行数据："用户名"
        TableRow Row0 = new TableRow(this);
        Row0.addView(createTV("用户名:"));
        for (String user : checkedUsers){
            Row0.addView(createTV(user));
        }
        HeaderTL.addView(Row0);

        //加载月份整体:
        for (int i = 0; i< month.lengthOfMonth()+1;i++){
            TableRow RowN = new TableRow(this);
            LocalDate date = month.plusDays(i);
            RowN.addView(createTV(date.getMonthValue()+"月"+date.getDayOfMonth()+"日"));
            for (int innerI = 0; innerI<col-1;innerI++){
                TextView item = createTV("");
                String presentShift;
                Shift shift = sql.queryShiftChangedByDate(checkedUsers[innerI],date.toString());
                if (shift.getShiftChanged()==null){
                    presentShift = sh.queryShift(checkedUsers[innerI],date.toString());
                }else {
                    presentShift = shift.getShiftChanged();
                    item.setTextColor(getColor(R.color.theme));
                }
                item.setText(String.valueOf(presentShift.charAt(0)));
                RowN.addView(item);
            }
            MainerTL.addView(RowN);
        }

        //加载末行数据："出勤数"
        TableRow Row1 = new TableRow(this);
        for (int i = 0; i<col;i++){
            String state = "";
            if (i==0){state = "出勤数：";}
            else {
                User user = sql.queryFromUserByName(checkedUsers[i]);
                int mod = user.getMod();

            }
            Row1.addView(createTV(state));
        }




    }//主体表格布局加载。

    private TextView createTV(String textContent){
        TextView textView = new TextView(this);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setHeight(100);
        textView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
        textView.setText(textContent);
        return textView;

    }




}