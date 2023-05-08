package com.example.demoapplication.services;

import com.example.demoapplication.R;

import java.util.Calendar;

public class Greetings {
    static Calendar calendar = Calendar.getInstance();
    static int hour = calendar.get(Calendar.HOUR_OF_DAY);

    static public String greeting(){
        if (hour < 12) {
            return "Good Morning";
        }
        if (hour < 17) {
            return "Good Afternoon";
        }
        return "Good Evening";
    }

    static public int greetingIcon(){
        if (hour < 12) {
            return (R.drawable.baseline_cloud_24);
        }
        if (hour < 17) {
            return (R.drawable.baseline_wb_sunny_24);
        }
        return (R.drawable.baseline_nights_stay_24);
    }

}
