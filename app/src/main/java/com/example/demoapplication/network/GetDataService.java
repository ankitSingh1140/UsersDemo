package com.example.demoapplication.network;

import com.example.demoapplication.model.AddUserModel;
import com.example.demoapplication.model.DeleteUserModel;
import com.example.demoapplication.model.ResponseModel;
import com.example.demoapplication.model.ResultModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface GetDataService {
    @GET("/api/user/getAllUsers")
    Call<ResultModel> getAllUsers();

    @POST("/api/user/addUser")
    Call<ResponseModel> addUserApi(@Body AddUserModel params);

    @POST("/api/user/updateUser")
    Call<ResponseModel> updateUserApi(@Body AddUserModel params);

    @POST("/api/user/deleteUser")
    Call<ResponseModel> deleteUserApi(@Body DeleteUserModel params);

}
