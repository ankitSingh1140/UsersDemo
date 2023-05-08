package com.example.demoapplication.model;

import java.util.List;

public class ResultModel {
    public int status;
    public List<UserModel> data;
    public String message;

    public int getStatus() {
        return status;
    }


    public List<UserModel> getData() {
        return data;
    }
}
