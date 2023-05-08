package com.example.demoapplication.services;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferencesOperations {
    String masterKey;
    SharedPreferences sh;
    SharedPreferences.Editor editSh;

    public SharedPreferencesOperations(Context context) throws GeneralSecurityException, IOException {
        masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        sh = EncryptedSharedPreferences.create("MySharedPref", masterKey, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        editSh = sh.edit();
    }

    // Save user Info in SharedPreferences when user Logged in
    public void saveUserInfo(boolean isUserLoggedIn, String email, int uid, String name){
        editSh.putBoolean("userLoginStatus", isUserLoggedIn);
        editSh.putString("userEmail", email);
        editSh.putInt("uid", uid);
        editSh.putString("userName", name);
        editSh.apply();
    }

    // Delete user Info when user log out
    public void deleteUserInfo(){
        editSh.putBoolean("userLoginStatus", false);
        editSh.putString("userEmail", "");
        editSh.putInt("uid", 0);
        editSh.putString("userName", "Anon");
        editSh.putInt("spinnerValue", 0);
        editSh.apply();
    }

    // Get user Login status
    public boolean getUserLogInStatus(){
        return sh.getBoolean("userLoginStatus", false);
    }


    // Get userId
    public int getUid(){
        return sh.getInt("uid", 0);
    }

    public void setSpinnerValue(int spinnerValue){
        editSh.putInt("spinnerValue", spinnerValue);
        editSh.apply();
    }

    public int getSpinnerValue(){
        return sh.getInt("spinnerValue", 0);
    }

}
