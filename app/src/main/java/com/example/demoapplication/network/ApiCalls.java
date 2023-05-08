package com.example.demoapplication.network;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.model.AddUserModel;
import com.example.demoapplication.model.DeleteUserModel;
import com.example.demoapplication.model.ResponseModel;
import com.example.demoapplication.model.ResultModel;
import com.example.demoapplication.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCalls {
    GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    AppDatabase db;
    UserDao userDao;
    Activity a;
    View v;
    public ApiCalls(Activity a, View v){
        this.a = a;
        this.v = v;
        this.db = Room.databaseBuilder(a.getApplicationContext(),
                AppDatabase.class, "user").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        this.userDao = db.userDao();
    }

    // get all user from api
    public void getAllUser(byte[] image, int uid,ResponseCallback callback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResultModel> call = service.getAllUsers();
        call.enqueue(new Callback<ResultModel>() {
            @Override
            public void onResponse(@NonNull Call<ResultModel> call, @NonNull Response<ResultModel> response) {
                if (response.body() != null){
                    if (response.body().getStatus() == 200){
                        List<User> allUsers = userDao.getAll();
                        List<Integer> allId = userDao.getAllUid();
                        List<UserModel> users = response.body().getData();
                        List<Integer> allIdApi = new ArrayList<>();
                        // add user and update user in local database from response
                        allUsers.forEach(user -> {
                            if (user.createdAt == null){
                                AddUserModel newUser = new AddUserModel(user.name, Objects.equals(user.gender, "Male") ? 0 :1, user.email, user.mobile);
                                addUser(newUser, user.profilePic, new ResponseCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });

                            } else if (user.updatedAt == null) {
                                AddUserModel newUser = new AddUserModel(user.name, Objects.equals(user.gender, "Male") ? 0 :1, user.email, user.mobile);
                                updateUserApi(newUser, user.profilePic, user.uid, new ResponseCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            } else if (user.name == null) {
                                deleteUserApi(user.uid, new ResponseCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            }

                        });
                        users.forEach(userModel -> {
                            allIdApi.add(userModel.getId());
                            User user = userDao.findByUid(userModel.getId());
                            if (user != null){
                                if (user.uid == userModel.getId()){
                                    if (uid == userModel.getId()){
                                        userDao.updateUser(userModel.getId(), userModel.getName(), userModel.getEmail(), userModel.getMobile(), userModel.getGender() == 0 ? "Male": "Female", image == null? userDao.getImageByUid(uid) : image , userModel.getUpdatedAt());
                                    } else {
                                        userDao.updateUser(userModel.getId(), userModel.getName(), userModel.getEmail(), userModel.getMobile(), userModel.getGender() == 0 ? "Male": "Female", userDao.getImageByUid(userModel.getId()) , userModel.getUpdatedAt());
                                    }
                                }
                            } else {
                                User newUser;
                                if (uid == userModel.getId()){
                                    newUser = new User(userModel.getId(), userModel.getName(), userModel.getEmail(), userModel.getMobile(), userModel.getGender() == 0 ? "Male": "Female", image ,userModel.getCreatedAt(), userModel.getUpdatedAt());
                                } else {
                                    newUser = new User(userModel.getId(), userModel.getName(), userModel.getEmail(), userModel.getMobile(), userModel.getGender() == 0 ? "Male": "Female", null ,userModel.getCreatedAt(), userModel.getUpdatedAt());
                                }
                                userDao.insert(newUser);
                            }
                        });
                        allId.forEach(id -> {
                            if (!allIdApi.contains(id)){
                                userDao.deleteUser(id);
                            }
                        });
                    }
                    callback.onSuccess();
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultModel> call, @NonNull Throwable t) {
                callback.onError();
            }
        });
    }

    // call Add user api to add user in api database
    public void addUser(AddUserModel user, byte[] image,ResponseCallback callback){
        Call<ResponseModel> call = service.addUserApi(user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.body() != null){
                    if (response.body().getStatus() == 200){
                        Snackbar.make(v, "Registered Successfully", Snackbar.LENGTH_SHORT).show();
                        getAllUser(image, response.body().getData(), callback);
                    } else if (response.body().getStatus() == 400) {
                        Snackbar.make(v, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    callback.onError();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Snackbar.make(v, "Something Went Wrong...", Snackbar.LENGTH_SHORT).show();
                callback.onError();
            }
        });
    }

    // update the user from the api
    public void updateUserApi(AddUserModel user, byte[] image, int id, ResponseCallback callback){
        Call<ResponseModel> call = service.updateUserApi(user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.body() != null){
                    if (response.body().getStatus() == 200){
                        Snackbar.make(v ,"Updated Successfully", Snackbar.LENGTH_SHORT).show();
                        getAllUser(image, id, callback);
                    } else if (response.body().getStatus() == 400) {
                        callback.onError();
                        Snackbar.make(v, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                callback.onError();
            }
        });
    }


    // delete user from the api
    public void deleteUserApi(int id, ResponseCallback callback){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        DeleteUserModel params = new DeleteUserModel(id);
        Call<ResponseModel> call = service.deleteUserApi(params);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                if (response.body()!=null){
                    System.out.println("in Api call response body: " + id + " response status " + response.body().getMessage());
                    if (response.body().getStatus() == 200){
                        System.out.println(response.body().getMessage());
                        Snackbar.make(v, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        getAllUser(null, 0, callback);
                    } else {
                        Snackbar.make(v, response.body().getMessage(), Snackbar.LENGTH_SHORT).show();
                        callback.onError();
                    }
                } else {
                    Snackbar.make(v, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Snackbar.make(v, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                callback.onError();
            }
        });
    }
}
