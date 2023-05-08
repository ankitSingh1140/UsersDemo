package com.example.demoapplication.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("Select * from user Where uid Not Like :uid")
    List<User> getAll(int uid);

    @Query("Select * from user")
    List<User> getAll();

    @Query("Select * from user Where email Like :email Or mobile Like :mobile")
    List<User> findByEmailOrPhone(String email, String mobile);

    @Query("Select * from user Where uid Like :uid")
    User findByUid(int uid);

    @Query("Select * from user Where email Like :text Or mobile Like :text")
    User getUserForLogin(String text);

    @Query("Delete From user Where uid Like :uid")
    void deleteUser(int uid);

    @Insert
    void insert(User user);

    @Query("Update user Set name = null Where uid Like :uid")
    void deleteName(int uid);

    @Query("Update user Set name = :name, email = :email, mobile = :mobile, gender = :gender, profilePic = :pic, updatedAt = :updatedAt Where uid Like :uid")
    void updateUser(int uid, String name, String email, String mobile, String gender, byte[] pic, String updatedAt);

    @Query("Select * from user Where uid Not Like :uid order by name ASC")
    List<User> sortAZ(int uid);

    @Query("Select * from user Where uid Not Like :uid order by name DESC")
    List<User> sortZA(int uid);

    @Query("Select * from user Where uid Not Like :uid order by datetime(createdAt) DESC")
    List<User> sortCreatedDate(int uid);

    @Query("Select * from user Where uid Not Like :uid order by datetime(updatedAt) DESC")
    List<User> sortLastModifiedDate(int uid);

    @Query("Select * from user where (name Like '%'||:search||'%' Or email Like '%'||:search||'%' Or mobile Like '%'||:search||'%') And uid Not Like :uid")
    List<User> search(String search, int uid);

    @Query("Select profilePic from user Where uid Like :uid")
    byte[] getImageByUid(int uid);

    @Query("Select uid from user")
    List<Integer> getAllUid();
}
