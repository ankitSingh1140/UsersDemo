package com.example.demoapplication.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class User {

    public User(@NonNull int uid, String name, String email, String mobile, String gender, byte[] profilePic, String createdAt, String updatedAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.profilePic = profilePic;
        this.createdAt = createdAt;
        this.updatedAt =  updatedAt;
    }

    @PrimaryKey
    public @NonNull int   uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "mobile")
    public String mobile;


    @ColumnInfo(name = "gender")
    public String gender;


    @ColumnInfo(name = "profilePic")
    public byte[] profilePic;

    @ColumnInfo(name = "createdAt")
    public String createdAt;

    @ColumnInfo(name = "updatedAt")
    public String updatedAt;
}
