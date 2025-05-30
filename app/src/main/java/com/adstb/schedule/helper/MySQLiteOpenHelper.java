package com.adstb.schedule.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adstb.schedule.GlobalConstants.SPKeys;
import com.adstb.schedule.R;
import com.adstb.schedule.bean.Compare;
import com.adstb.schedule.bean.Shift;
import com.adstb.schedule.bean.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Schedule.db";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SHIFTS = "shifts";
    private static final String TABLE_COMPARES = "compares";
    private final SharedPreferencesHelper sp;

    private static final String CREATE_TABLE_USER =
            "create table "+ TABLE_USERS +
                    " (id integer primary key autoincrement," +
                    " name text, configDate text," +
                    " mod integer," +
                    " isMainer integer)";

    private static final String CREATE_TABLE_SHIFT =
            "create table "+ TABLE_SHIFTS +
                    " (id integer primary key autoincrement," +
                    " name text, date text," +
                    " shift text)";

    private static final String CREATE_TABLE_COMPARES =
            "create table "+ TABLE_COMPARES +
                    " (id integer primary key autoincrement," +
                    " groupName text, month text," +
                    " memberAmount integer, members text)";

    public MySQLiteOpenHelper(Context context){
        super(context, DB_NAME, null, 1);
        sp = new SharedPreferencesHelper(context);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_SHIFT);
        db.execSQL(CREATE_TABLE_COMPARES);

    }//数据库创建时的事件。

    public long insertDataToUser(User user) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name",user.getName());
        values.put("configDate",user.getConfigDate());
        values.put("mod",user.getMod());
        values.put("isMainer",user.getMainer());

        return db.insert(TABLE_USERS,null,values);
    }//插入新用户到用户表。

    public long updateDataToUser(User user){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name",user.getName());
        values.put("configDate",user.getConfigDate());
        values.put("mod",user.getMod());
        values.put("isMainer",user.getMainer());

        return db.update(TABLE_USERS,values,"name like ?",new String[]{user.getName()});

    }//更新用户信息。

    public String[] queryUserList(){
        //查询用户列表。
        String[] result;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_USERS,null,null,null,
                null,null,null);

        result = new String[cursor.getCount()];
        for (int i =0;cursor.moveToNext();i++){
            result[i] = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        }
        cursor.close();

        return result;
    }//查询用户列表。

    public User queryFromUserByName(String name){

        SQLiteDatabase db = getWritableDatabase();

        User user = new User();

        Cursor cursor = db.query(TABLE_USERS,null,"name like ?",
                new String[]{name},null,null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                String name1 = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String configDate = cursor.getString(cursor.getColumnIndexOrThrow("configDate"));
                int mod = cursor.getInt(cursor.getColumnIndexOrThrow("mod"));
                int isMainer = cursor.getInt(cursor.getColumnIndexOrThrow("isMainer"));

                user.setName(name1);
                user.setConfigDate(LocalDate.parse(configDate));
                user.setMod(mod);
                user.setMainer(isMainer);
            }
            cursor.close();
        }
        return user;
    }//根据用户名查找用户信息。



    public int deleteDataFromUserByName(String name){

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SHIFTS,"name like ?", new String[]{name});

        return db.delete(TABLE_USERS,"name like ?", new String[]{name});
    }//根据用户名删除用户信息。


    public int deleteDataFromShiftByDate(String name, String date){
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_SHIFTS,"name like ? and date like ?",new String[]{name,date});
    }//根据日期删除其调班信息。



    public long setShiftByDate(Shift shift){
        String name = shift.getName();
        String date = shift.getDate();
        String changeShift = shift.getShiftChanged();

        deleteDataFromShiftByDate(name,date);
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        values.put("name",name);
        values.put("date",date);
        values.put("shift",changeShift);
        return db.insert(TABLE_SHIFTS,null,values);
    }//根据日期设置其调班信息。



    @SuppressLint("Recycle")
    public Shift queryShiftChangedByDate(String name, String date){
        Shift shift = new Shift();
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(TABLE_SHIFTS,null,"name like ? and date like ?",new String[]{name,date},
                null,null,null);

        String shiftChanged = (cursor!=null&&cursor.moveToNext()) ?
                cursor.getString(cursor.getColumnIndexOrThrow("shift")) :null;

        shift.setName(name);
        shift.setDate(date);
        shift.setShiftChanged(shiftChanged);

        return shift;
    }//根据日期查询是否有调班。

    public String[] queryCompareList(){
        //查询用户列表。
        String[] result;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_COMPARES,null,null,null,
                null,null,null);

        result = new String[cursor.getCount()];
        for (int i =0;cursor.moveToNext();i++){
            result[i] = cursor.getString(cursor.getColumnIndexOrThrow("groupName"));
        }
        cursor.close();

        return result;
    }//查询 班次调整配置 列表。

    public Compare queryFromCompareByGroupName(String groupName){
        SQLiteDatabase db = getWritableDatabase();
        Compare compare = new Compare();

        Cursor cursor = db.query(TABLE_COMPARES,null,"groupName like ?",
                new String[]{groupName},null,null,null);

        if (cursor != null){
            while (cursor.moveToNext()){
                String groupName1 = cursor.getString(cursor.getColumnIndexOrThrow("groupName"));
                String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                int memberAmount = cursor.getInt(cursor.getColumnIndexOrThrow("memberAmount"));
                String members = cursor.getString(cursor.getColumnIndexOrThrow("members"));
                String[] memberList = members.split(" ");
//                groupName text, startDate text, endDate text," +
//                " memberAmount integer, members text
                compare.setGroupName(groupName1);
                compare.setMonth(month);
                compare.setMemberAmount(memberAmount);

            }
            cursor.close();
        }
        return compare;
    }//根据配置名查找配置信息。

    public Boolean insertOrCreateGroupNameToCompare(Compare compare){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_COMPARES,"groupName like ?", new String[]{compare.getGroupName()});
        ContentValues values = new ContentValues();
        /*
        groupName text,
        startDate text,
        endDate text,
        memberAmount integer,
        members text
         */
        values.put("groupName",compare.getGroupName());
        values.put("month",compare.getMonth());
        values.put("memberAmount",compare.getMemberAmount());
        values.put("members",compare.getMembers());

        return db.insert(TABLE_COMPARES,null,values)!=-1;
    }

    public Boolean deleteGroupNameFromCompare(String groupName){
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_COMPARES,"groupName like ?", new String[]{groupName})==-1;
    }//根据 配置名 删除信息。




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
