package com.example.demoapplication.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.demoapplication.adapter.CustomUserAdapter;
import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.FragmentUserListBinding;
import com.example.demoapplication.network.ApiCalls;
import com.example.demoapplication.network.NetworkObserver;
import com.example.demoapplication.network.NetworkObserverCallback;
import com.example.demoapplication.network.ResponseCallback;
import com.example.demoapplication.services.Greetings;
import com.example.demoapplication.services.SharedPreferencesOperations;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;


public class UserListFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private TextView greetingText;
    private ImageView greetingIcon;
    private TextView userName;
    private AppCompatImageView profilePic;
    static Activity a;
    SharedPreferencesOperations sh = null;

    static FragmentUserListBinding binding;
    ActivityMainBinding activityMainBinding;
    private static CustomUserAdapter adapter;
    private static RecyclerView recyclerView;
    private AppCompatSpinner spinner;
    private static SearchView search;
    static RecyclerView.LayoutManager layoutManager;
    private static LottieAnimationView noDataFound;

    private static LinearLayoutCompat linearLayoutSortBy;
    private static LinearLayoutCompat linearLayoutSelectAll;
    private static AppCompatCheckBox selectAll;
    private static AppCompatImageButton deleteAll;
    String[] items = new String[]{"A-Z", "Z-A", "Last Modified", "Last Created"};
    static UserDao userDao;
    static List<User> users;
    static List<User> ids;
    User user;


    public UserListFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentUserListBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        greetingText = binding.greetingText;
        greetingIcon = binding.greetingIcon;
        userName = binding.tvName;
        profilePic = binding.ivImage;
        spinner = binding.sortBy;
        search = binding.search;
        noDataFound = binding.noDataFound;
        linearLayoutSortBy = binding.linearLayoutSortBy;
        linearLayoutSelectAll = binding.selectAllRow;
        selectAll = binding.selectAll;
        deleteAll = binding.buttonDelete;
    }

    public static void setIdsList(List<User> selectIds){
        ids = selectIds;
        if (!ids.isEmpty()){

        }
        if (ids.size() == users.size()){
            setSelectCheck(true);
        }
    }

    public static void setSelectCheck(boolean check){
        selectAll.setChecked(check);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        selectAll.setOnClickListener(v -> {
            adapter = new CustomUserAdapter(users, a, selectAll.isChecked(), String.valueOf(search.getQuery()), false);
            recyclerView.setAdapter(adapter);
        });

        a = getActivity();
        try {
            sh = new SharedPreferencesOperations(a);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        userDao = DBInstance.getUserDao(a);
        NetworkObserver.getConnect(a,new NetworkObserverCallback() {
            @Override
            public void onLost() {
                Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onAvailable() {
                Snackbar.make(binding.getRoot(), "Internet Available", Snackbar.LENGTH_SHORT).show();
                a.runOnUiThread(() -> getAllUsers());
            }
        });
        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(a, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapterSortBy);
        spinner.setSelection(sh.getSpinnerValue());
        spinner.setOnItemSelectedListener(this);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Objects.equals(newText, "")){
                    users = userDao.getAll(sh.getUid());
                    setUserList(users);
                    spinner.setSelection(0);
                } else  {
                    users = userDao.search(newText, sh.getUid());
                    setUserList(users);
                }
                return true;
            }
        });
        deleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(a).setTitle("Delete Selected").setMessage("Are You Sure?").setPositiveButton("Yes", (dialog, which) -> {
                adapter = new CustomUserAdapter(users, a, selectAll.isChecked(), String.valueOf(search.getQuery()), true, ids);
                recyclerView.setAdapter(adapter);
                Handler handler = new Handler(Looper.getMainLooper());
                a.runOnUiThread(() -> handler.postDelayed(this::getAllUsers, 2000));
            }).setNegativeButton("Cancel", (dialog, which) -> {
            }).show();
        });
        users = userDao.getAll(sh.getUid());
        setUserList(users);
        greetingText.setText(Greetings.greeting());
        greetingIcon.setImageResource(Greetings.greetingIcon());
        user = userDao.findByUid(sh.getUid());
        if (user != null){
            userName.setText(user.name);
        } else {
            signOut();
        }
        if (user.profilePic != null){
            profilePic.setImageBitmap(BitmapFactory.decodeByteArray(user.profilePic, 0, user.profilePic.length));
        }
        binding.ivImage.setOnClickListener(v -> goToUserDetails());
        binding.fab.setOnClickListener(this::moveToAddUser);
        binding.buttonSignOut.setOnClickListener(v -> signOut());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    public void getAllUsers(){
        new ApiCalls(a,binding.getRoot()).getAllUser(null, 0, new ResponseCallback() {
            @Override
            public void onSuccess() {
                users = userDao.getAll(sh.getUid());
                setUserList(users);
                if (sh.getUid() != 0){
                    if (userDao.findByUid(sh.getUid()) == null){
                        signOut();
                    }
                }
                Snackbar.make(binding.getRoot(), "Data sync completed", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Snackbar.make(binding.getRoot(), "Something Went Wrong...", Snackbar.LENGTH_SHORT).show();
            }
        });
        if (sh.getUid() != 0){
            if (userDao.findByUid(sh.getUid()) == null){
                signOut();
            }
        }
    }
    public void signOut() {
        sh.deleteUserInfo();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(false);
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.remove(this).replace(activityMainBinding.fragment1.getId(), LoginFragment.class, null).commit();
    }

    public void moveToAddUser(View view) {
        AddUserFragment detailsFragment = new AddUserFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(activityMainBinding.fragment1.getId(),detailsFragment).setReorderingAllowed(true).addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void goToUserDetails() {
        UserDetailsFragment userDetailsFragment = new UserDetailsFragment(sh.getUid(), false);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(activityMainBinding.fragment1.getId(), userDetailsFragment).setReorderingAllowed(true).addToBackStack(null);
        fragmentTransaction.commit();
    }
    public void setUserList(List<User> users){
        recyclerView = binding.userDataRecyclerView;
        adapter = new CustomUserAdapter(users, a,selectAll.isChecked(), String.valueOf(search.getQuery()), false);
        layoutManager = new LinearLayoutManager(a);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        setNoDataFound();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                sh.setSpinnerValue(0);
                users = userDao.sortAZ(sh.getUid());
                setUserList(users);
                recyclerView.smoothScrollToPosition(0);
                break;
            case 1:
                sh.setSpinnerValue(1);
                users = userDao.sortZA(sh.getUid());
                setUserList(users);
                recyclerView.smoothScrollToPosition(0);
                break;
            case 2:
                sh.setSpinnerValue(2);
                users = userDao.sortLastModifiedDate(sh.getUid());
                setUserList(users);
                recyclerView.smoothScrollToPosition(0);
                break;
            case 3:
                sh.setSpinnerValue(3);
                users = userDao.sortCreatedDate(sh.getUid());
                setUserList(users);
                recyclerView.smoothScrollToPosition(0);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinner.setSelection(sh.getSpinnerValue());
        spinner.setOnItemSelectedListener(this);
    }

    public static void setNoDataFound(){
        if (users.size() == 0){
            linearLayoutSortBy.setVisibility(View.GONE);
            noDataFound.setVisibility(View.VISIBLE);
            linearLayoutSelectAll.setVisibility(View.GONE);
        } else {
            linearLayoutSortBy.setVisibility(View.VISIBLE);
            noDataFound.setVisibility(View.GONE);
            linearLayoutSelectAll.setVisibility(View.VISIBLE);
        }
    }

}