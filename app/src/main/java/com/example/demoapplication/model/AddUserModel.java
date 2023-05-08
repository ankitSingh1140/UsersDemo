package com.example.demoapplication.model;

public class AddUserModel {
    public AddUserModel(String name, int gender, String email, String mobile) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.mobile = mobile;
    }

    public AddUserModel(int id, String name, int gender, String email, String mobile) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.mobile = mobile;
    }

    public int id;
    public String name;
    public int gender;
    public String email;
    public String mobile;
}
