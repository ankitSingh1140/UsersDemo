package com.example.demoapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
