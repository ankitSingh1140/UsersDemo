package com.example.demoapplication.database;

import android.app.Activity;

import androidx.room.Room;

public class DBInstance {
    public static UserDao userDao = null;
    public static UserDao getUserDao(Activity a){
        if (userDao == null){
            userDao = Room.databaseBuilder(a.getApplicationContext(),
                    AppDatabase.class, "user").allowMainThreadQueries().fallbackToDestructiveMigration().build().userDao();
        }
        return userDao;
    }
}
