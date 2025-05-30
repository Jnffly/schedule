package com.adstb.schedule.bean;

import java.time.LocalDate;

public class User {
    private String name;
    private String configDate;
    private int mod;
    private int isMainer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigDate() {
        return configDate;
    }

    public void setConfigDate(LocalDate configDate) {
        this.configDate = String.valueOf(configDate);
    }

    public int getMod() {
        return mod;
    }

    public int getMainer() {
        return isMainer;
    }

    public void setMainer(int mainer) {
        isMainer = mainer;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", configDate='" + configDate + '\'' +
                ", configDateToShift=" +
                ", mod=" + mod +
                ", isMainer=" + isMainer +
                '}';
    }
}
