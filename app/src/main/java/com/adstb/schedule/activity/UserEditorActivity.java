package com.adstb.schedule.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.bean.User;
import com.adstb.schedule.helper.MySQLiteOpenHelper;
import com.adstb.schedule.helper.SharedPreferencesHelper;
import com.adstb.schedule.GlobalConstants.GlobalConstants;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Pattern;

public class UserEditorActivity extends AppCompatActivity {

    private TextView titleTV,nameCheck;
    private ImageButton backBN,delBN;
    private Button confBN;
    private Spinner modesSP,shiftsSP;
    private EditText nameET;
    private String titleValue, userName;
    private String[] shiftChoose;
    private int deleteIsVisit,isMainer;
    private Boolean isFirst, isEdit, isCreate;
    private MySQLiteOpenHelper mMySQLiteOpenHelper;
    private final SharedPreferencesHelper sp = new SharedPreferencesHelper(this);
    private final String
            NONE = GlobalConstants.NONE,
            FIRST_TIME_TO_USE = SPKeys.FIRST_TIME_TO_USE,
            SELECT_USER = SPKeys.SELECT_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_editor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mMySQLiteOpenHelper = new MySQLiteOpenHelper(this);

        Intent intent = getIntent();

        userName = intent.getStringExtra("userName");
        if (userName != null){//初始化 是否为第一次使用 和 是否为创建新用户。
            isEdit = !Objects.equals(userName, "添加用户");
            isCreate = Objects.equals(userName, "添加用户");
        }else {
            isEdit = false;
            isCreate = false;
        }

        //初始化 是否为编辑状态。
        isFirst = !sp.getSP(FIRST_TIME_TO_USE).equals("0");

        StateJudgement();//判断进入状态。

        initConfig();//控件获取和配置。



        if (isEdit) {//如果接收到username的参数才继续执行。
            initData(userName);
        }


    }



    private void StateJudgement() {//判断进入该页面的状态。
        titleValue = "未知状态：发生了错误";
        deleteIsVisit = View.GONE;
        isMainer = 0;

        if (isFirst){
            titleValue = "首次配置";
            isMainer = 1;

        }
        if (isCreate){
            titleValue = "创建用户";
            isMainer = 0;

        }
        if (isEdit){
            titleValue = "编辑用户";
            deleteIsVisit = View.VISIBLE;
            isMainer = mMySQLiteOpenHelper.queryFromUserByName(userName).getMainer();
        }

    }

    private void initData(String userName){//编辑模式加载用户信息。
            User user = mMySQLiteOpenHelper.queryFromUserByName(userName);
            nameET.setText(user.getName());

            int modePosition;
            String[] shiftList;
            switch (user.getMod()){
                case 8:
                    modePosition = 2;
                    shiftList = getResources().getStringArray(R.array.mod8);
                    break;
                case 6:
                    modePosition = 1;
                    shiftList = getResources().getStringArray(R.array.mod6);
                    break;
                default:
                    modePosition = 0;
                    shiftList = getResources().getStringArray(R.array.mod12);
            }
            modesSP.setSelection(modePosition);

            /*** 班次模式选择的监听事件在修改今日班次的列表项时，似乎强制修改了今日班次的默认选择项为0，
             * 从而导致无法由此方法来展示用户当初配置的班次...
            String target = ShiftsConfig.shiftCompute(userName,LocalDate.now().toString(),this);
             for (int i = 0; i < shiftList.length; i++){
                 //for循环遍历，找到属于今天班次的对应下标，设置shiftSP的选择项同时跳出循环。
                 if (Objects.equals(shiftList[i], target)){
                     shiftsSP.setSelection(i);
                 }
             }
             ***/
    }



    private void initConfig() {//控件获取和配置。
        titleTV = findViewById(R.id.UserEditorTitleTV);
        backBN = findViewById(R.id.UserEditorBackButton);
        delBN = findViewById(R.id.UserEditorDeleteButton);
        confBN = findViewById(R.id.UserEditorConfirmButton);
        modesSP = findViewById(R.id.UserEditorModeSP);
        shiftsSP = findViewById(R.id.UserEditorShiftChooseSP);
        nameCheck = findViewById(R.id.UserEditorNameChecker);
        nameET = findViewById(R.id.UserEditorNameET);

        titleTV.setText(titleValue);//配置标题的文字内容。
        delBN.setVisibility(deleteIsVisit);//配置删除按钮的显示状态。

        backBN.setOnClickListener(v -> {
            finish();
        });//返回键点击事件。

        delBN.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(GlobalConstants.DIALOG_TITLE)
                    .setMessage("你确定要删除这个用户吗？\n“"+userName+"”将会永久消失！（真的很久！）")
                    .setPositiveButton("删除", (dialog1, which) -> {
                        if (mMySQLiteOpenHelper.deleteDataFromUserByName(userName)==-1){
                            //删除用户信息。
                            Toast.makeText(this, "删除时发生了错误（DB)", Toast.LENGTH_SHORT).show();
                            confBN.setText("完成配置");
                        } else {
                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", (dialog12, which) -> {
                        dialog12.dismiss();
                    }).create();
            dialog.show();


        });//删除键点击事件。

        modesSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        shiftChoose = getResources().getStringArray(R.array.mod12);
                        shiftsSP.setAdapter(new ArrayAdapter<>(UserEditorActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, shiftChoose));
                        break;
                    case 1:
                        shiftChoose = getResources().getStringArray(R.array.mod6);
                        shiftsSP.setAdapter(new ArrayAdapter<>(UserEditorActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, shiftChoose));
                        break;
                    case 2:
                        shiftChoose = getResources().getStringArray(R.array.mod8);
                        shiftsSP.setAdapter(new ArrayAdapter<>(UserEditorActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, shiftChoose));
                        break;
                }}@Override public void onNothingSelected(AdapterView<?> parent) {}
        });//班次模式标签选择事件。

        nameET.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String nameInputChanged = s.toString().replaceAll(" ","");
//                nameET.setText(nameInputChanged);
                User user = mMySQLiteOpenHelper.queryFromUserByName(nameInputChanged);

                if (nameInputChanged.isEmpty()||nameInputChanged.equals("添加用户")){
                    //判断姓名是否为空 或非法。
                    nameCheck.setText("名字不能为空或非法名");
                    nameCheck.setVisibility(View.VISIBLE);
                    confBN.setVisibility(View.GONE);
                    return;
                }
                /**
                 * 不再采用创建单独用户表的方式，因此判断是否为数字开头已无意义，所以弃用。

                if (Pattern.matches("[0-9].*", nameInputChanged)){
                    //判断姓名是否为数字开头。
                    nameCheck.setText("名字不能以数字开头");
                    nameCheck.setVisibility(View.VISIBLE);
                    confBN.setVisibility(View.GONE);
                    return;
                }

                 */

                if (isEdit){
                    if (user.getConfigDate()!=null&& !Objects.equals(user.getName(), userName)){
                        //判断是否占用别人的名字。
                        nameCheck.setText("已存在同名的其他用户");
                        nameCheck.setVisibility(View.VISIBLE);
                        confBN.setVisibility(View.GONE);
                        return;
                    }
                }
                else if (user.getConfigDate()!=null) {
                        //判断姓名是否存在。
                        nameCheck.setText("该名字已存在");
                        nameCheck.setVisibility(View.VISIBLE);
                        confBN.setVisibility(View.GONE);
                        return;
                }//如果是编辑状态执行第一分支，反之执行else。


                nameCheck.setVisibility(View.GONE);
                confBN.setVisibility(View.VISIBLE);
            }
        });//监听姓名输入框的内容变化。


        confBN.setOnClickListener(v -> {
            confirm();
        });//完成按钮点击事件。
    }



    private void confirm() {//点击完成配置按钮的事件。
        confBN.setText("正在配置中，请稍后...");

        String nameInput = nameET.getText().toString();//获取输入的名字。

        int mod = shiftsSP.getAdapter().getCount();//获取选择的班次模式。
        LocalDate configDate = LocalDate.now().plusDays(mod-shiftsSP.getSelectedItemPosition());
        //计算配置日期为当前日期+选择的班次。

        User user = new User();
        user.setConfigDate(configDate);
        user.setMod(mod);
        user.setName(nameInput);
        user.setMainer(isMainer);
        //整合用户信息，经过名字审核合法后再写入数据库。(👈原方案，但名字审核环节已归纳至编辑框监听事件。)

        if (isEdit){
            if (mMySQLiteOpenHelper.updateDataToUser(user)==-1){
                //更新用户信息。
                Toast.makeText(this, "配置时发生了错误（DB)", Toast.LENGTH_SHORT).show();
                confBN.setText("完成配置");
                return;
            }
        }else if (mMySQLiteOpenHelper.insertDataToUser(user)==-1){
            //将用户信息写入数据库，并为其创建新表。
            Toast.makeText(this, "配置时发生了错误（DB)", Toast.LENGTH_SHORT).show();
            confBN.setText("完成配置");
            return;
        }

        /**  该方法已废弃。
                ShiftsConfig.shiftWriter(nameInput,configDate.toString(),this);
                执行写入班次的方法。
         **/



        if (isFirst){
            if (!sp.setSP(FIRST_TIME_TO_USE,"0")){
                //如果在写入配置参数时发生了错误,则会结束。
                Toast.makeText(this, "配置时发生了错误（SP)", Toast.LENGTH_SHORT).show();
                confBN.setText("完成配置");
                return;
            } else if (!sp.setSP(SELECT_USER,nameInput)){
                //如果在写入配置参数时发生了错误,则会结束。
                Toast.makeText(this, "配置时发生了错误（SP)", Toast.LENGTH_SHORT).show();
                confBN.setText("完成配置");
                return;

            }
        }//第一次使用的配置参数调整。

        Toast.makeText(this, "配置完成", Toast.LENGTH_SHORT).show();
        if(!isEdit){
            startActivity(new Intent(UserEditorActivity.this, MainActivity.class));
        }
        finish();
        //最终提示配置成功并结束界面。

    }


}