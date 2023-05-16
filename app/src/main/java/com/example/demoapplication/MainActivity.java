package com.example.demoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.content.Context;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.fragments.LoginFragment;
import com.example.demoapplication.fragments.UserListFragment;
import com.example.demoapplication.services.SharedPreferencesOperations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    public Context c;
    ActivityMainBinding binding;
    UserDao userDao;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = DBInstance.getUserDao(this);
        c = getApplicationContext();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true);
        SharedPreferencesOperations sh;
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sh = new SharedPreferencesOperations(c);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        user = userDao.findByUid(sh.getUid());
        if (user == null){
            sh.deleteUserInfo();
        }
        Log.d("Main", String.valueOf(sh.getUserLogInStatus()));
        if (sh.getUserLogInStatus()){
            UserListFragment userListFragment = new UserListFragment();
            fragmentTransaction.add(binding.fragment1.getId(), userListFragment, null).commit();
        } else {
            fragmentTransaction.add(binding.fragment1.getId(), LoginFragment.class, null).commit();
        }
    }


}