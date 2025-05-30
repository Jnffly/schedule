package com.adstb.schedule.bean;

public class Shift {
    private String name;
    private String date;
    private String shiftChanged;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShiftChanged() {
        return shiftChanged;
    }

    public void setShiftChanged(String shiftChanged) {
        this.shiftChanged = shiftChanged;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", shiftChanged='" + shiftChanged + '\'' +
                '}';
    }
}
