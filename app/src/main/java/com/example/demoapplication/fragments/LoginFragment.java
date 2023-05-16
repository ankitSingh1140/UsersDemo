package com.example.demoapplication.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.FragmentLoginBinding;
import com.example.demoapplication.network.ApiCalls;
import com.example.demoapplication.network.GetDataService;
import com.example.demoapplication.network.NetworkObserver;
import com.example.demoapplication.network.ResponseCallback;
import com.example.demoapplication.network.RetrofitClientInstance;
import com.example.demoapplication.services.AuthServices;
import com.example.demoapplication.services.SharedPreferencesOperations;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


public class LoginFragment extends Fragment {
    private TextInputEditText emailMobile;
    private TextInputEditText pass;
    AuthServices auth = new AuthServices();
    SharedPreferencesOperations sh = null;

    FragmentLoginBinding binding;
    ActivityMainBinding activityMainBinding;

    Activity a;
    UserDao userDao;
    List<User> users;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GetDataService service;


    public LoginFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        userDao = DBInstance.getUserDao(a);
        users = userDao.getAll();
        try {
            sh = new SharedPreferencesOperations(a);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        emailMobile = binding.etEmailMobile;
        pass = binding.etPass;
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null);
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        emailMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForEmail(emailMobile);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForPass(pass);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.buttonLogin.setOnClickListener(v -> login());
        binding.buttonRegister.setOnClickListener(v -> fragmentTransaction.replace(activityMainBinding.fragment1.getId(), RegisterFragment.class, null).commit());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    // Login and saves the user info in SharedPreferences
    public void login() {
        if (auth.validationForEmailAndPassword(emailMobile, pass)){
            User getUser = userDao.getUserForLogin(String.valueOf(emailMobile.getText()));
            if (getUser == null){
                if (NetworkObserver.isConnected(a)){
                    new ApiCalls(a,binding.getRoot()).getAllUser(null, 0, new ResponseCallback() {
                        @Override
                        public void onSuccess() {
                            User getUser = userDao.getUserForLogin(String.valueOf(emailMobile.getText()));
                            if (getUser != null){
                                sh.saveUserInfo(true, getUser.email, getUser.uid, getUser.name);
                                fragmentTransaction.replace(activityMainBinding.fragment1.getId(), UserListFragment.class, null).commit();
                            } else {
                                Snackbar.make(binding.getRoot(), "User Not Exists.", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    Snackbar.make(binding.getRoot(), "User Not Exists", Snackbar.LENGTH_SHORT).show();
                }

            } else {
                sh.saveUserInfo(true, getUser.email, getUser.uid, getUser.name);
                fragmentTransaction.replace(activityMainBinding.fragment1.getId(), UserListFragment.class, null).commit();
            }
        }
    }



}