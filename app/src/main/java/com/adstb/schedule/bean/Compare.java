package com.adstb.schedule.bean;

import android.content.Context;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Compare {

    private String groupName,month,members;
    public final String SPLIT = " ";
    private int memberAmount;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(int memberAmount) {
        this.memberAmount = memberAmount;
    }


    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String[] getMembersArray(){
        return members.split(SPLIT);
    }

    public boolean isReady(){
        return groupName!=null&&month!=null&&members!=null;
    }

    public String getRealMonth(){
        LocalDate date = LocalDate.parse(month);
        return date.getYear() + "-" + date.getMonthValue();
    }

    @Override
    public String toString() {
        return "Compare{" +
                "groupName='" + groupName + '\'' +
                ", month='" + month + '\'' +
                ", members='" + members + '\'' +
                ", memberAmount=" + memberAmount +
                '}';
    }
}
