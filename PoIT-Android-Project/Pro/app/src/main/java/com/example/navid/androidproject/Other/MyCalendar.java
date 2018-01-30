package com.example.navid.androidproject.Other;

import java.util.Calendar;

public class MyCalendar  {
    Calendar calendar;
    public MyCalendar() {
        calendar = Calendar.getInstance();
    }
    public long getMilliSecondTime(){
        return calendar.getTimeInMillis();
    }
}
