package com.example.demoapplication.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.FragmentRegisterBinding;
import com.example.demoapplication.model.AddUserModel;
import com.example.demoapplication.network.ApiCalls;
import com.example.demoapplication.network.GetDataService;
import com.example.demoapplication.network.NetworkObserver;
import com.example.demoapplication.network.ResponseCallback;
import com.example.demoapplication.network.RetrofitClientInstance;
import com.example.demoapplication.services.AuthServices;
import com.example.demoapplication.services.SharedPreferencesOperations;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class RegisterFragment extends Fragment {
    private AutoCompleteTextView country;
    private AutoCompleteTextView state;
    private TextInputEditText dob;
    private TextInputLayout dobIn;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText mobile;
    private RadioGroup gender;
    private RadioButton male;
    private TextView genderError;
    private CheckBox reading;
    private CheckBox dancing;
    private CheckBox programming;
    private  TextInputEditText timeText;
    private TextInputLayout timeIn;
    private TextInputEditText pass;
    private  TextInputEditText cPass;
    SharedPreferencesOperations sh = null;
    AuthServices auth = new AuthServices();
    FragmentRegisterBinding binding;
    ActivityMainBinding activityMainBinding;
    Activity a;
    UserDao userDao;
    List<User> users;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GetDataService service;
    Random random;
    int uid;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        random = new Random();

        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        try {
            sh = new SharedPreferencesOperations(a);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        name = binding.userDetailsView.etName;
        email = binding.userDetailsView.etEmail;
        mobile = binding.userDetailsView.etMobile;
        country = binding.userDetailsView.country;
        state = binding.userDetailsView.state;
        dob = binding.userDetailsView.dob;
        gender = binding.userDetailsView.gender;
        genderError = binding.userDetailsView.genderError;
        reading = binding.userDetailsView.reading;
        dancing = binding.userDetailsView.dancing;
        programming = binding.userDetailsView.programming;
        pass = binding.etPass;
        cPass = binding.etCPass;
        timeText = binding.time;
        male = binding.userDetailsView.male;
        dobIn = binding.userDetailsView.dobIn;
        timeIn = binding.timeIn;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null);
        setValuesForState();
        setValuesForCountry();
        dobStyler();
        userDao = DBInstance.getUserDao(a);
        users = userDao.getAll(sh.getUid());
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForName(name);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForEmailRegister(email);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForPhoneRegister(mobile);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForDob(dob);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        timeIn.setEndIconOnClickListener(v -> {
            TimePickerDialog.OnTimeSetListener myTimeListener = ((view, hour, minute) -> showTime(hour, minute));
            TimePickerFragment timePickerFragment = new TimePickerFragment(myTimeListener);
            timePickerFragment.show(getParentFragmentManager(), "TimePicker");
        });
        dobIn.setEndIconOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener myDateListener = (view, year, month, day) -> showDate(year, month, day);
            DatePickerFragment datePickerFragment = new DatePickerFragment(myDateListener);
            datePickerFragment.show(getParentFragmentManager(), "DatePicker");
        });
        binding.buttonLogin.setOnClickListener(v -> fragmentTransaction.replace(activityMainBinding.fragment1.getId(), LoginFragment.class, null).commit());
        binding.buttonRegister.setOnClickListener(v -> register());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    // set the values for autocomplete text for country field
    public void setValuesForCountry(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(a, android.R.layout.select_dialog_item, auth.countries);
        country.setThreshold(0);
        country.setAdapter(adapter);
        country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForCountry(country);
                setValuesForState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                auth.validationForCountry(country);

            }
        });
    }

    // set the values for autocomplete text for state field
    public void setValuesForState(){
        state.setEnabled(false);
        state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                auth.validationForState(country, state);
            }

            @Override
            public void afterTextChanged(Editable s) {
                auth.validationForState(country, state);

            }
        });
        // If country is equals to India
        if (Objects.equals(country.getText().toString(), "India")){
            state.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(a, android.R.layout.select_dialog_item, auth.stateIndia);
            state.setThreshold(0);
            state.setAdapter(adapter);
        }
        // If country is equals to Australia
        if (Objects.equals(country.getText().toString(), "Australia")) {
            state.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(a, android.R.layout.select_dialog_item, auth.stateAustralia);
            state.setThreshold(0);
            state.setAdapter(adapter);
        }
        // If country is equals to United States
        if(Objects.equals(country.getText().toString(), "United States")) {
            state.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(a, android.R.layout.select_dialog_item, auth.stateUnitedStates);
            state.setThreshold(0);
            state.setAdapter(adapter);
        }
    }


    // style the date when user enters the date manually
    public void dobStyler(){
        dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int cursorPosition = dob.getSelectionEnd();
                Log.d("registration", String.valueOf(cursorPosition));
                Log.d("registration", s.toString());
                if (cursorPosition == 2){
                    String text = s + "-";
                    Log.d("registration", text);
                    dob.setText(text);
                    dob.setSelection(dob.length());
                }
                if (cursorPosition == 5){
                    String text = s + "-";
                    Log.d("registration", text);
                    dob.setText(text);
                    dob.setSelection(dob.length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void showDate(int year, int month, int day){
        String dayOfMonth = String.valueOf(day);
        String monthOfDate = String.valueOf(month);
        String yearOfDate = String.valueOf(year);
        if (dayOfMonth.length() == 1){
            dayOfMonth = "0"+dayOfMonth;
        }
        if (monthOfDate.length()==1){
            monthOfDate = "0"+monthOfDate;
        }
        String date = dayOfMonth + "-" + monthOfDate + "-" + yearOfDate;
        dob.setText(date);
    }
    // set the time field
    public void showTime(int hour, int minute){
        String hourOfTime = String.valueOf(hour);
        String minuteOfTime = String.valueOf(minute);
        String time = hourOfTime + ":" + minuteOfTime;
        timeText.setText(time);
    }
    // Register or create the user and save the user info in SharedPreferences
    public void register(){
        if (auth.validationForRegistration(name, email, mobile, country, state, dob, gender, genderError,reading, dancing, programming) && auth.validationForPassword(pass, cPass)){
            int id = gender.getCheckedRadioButtonId();
            int genderId = (id == male.getId())? 0 : 1;
            users = userDao.findByEmailOrPhone(String.valueOf(email.getText()), String.valueOf(mobile.getText()));
            if(users.size() == 0){
                AddUserModel user = new AddUserModel(String.valueOf(name.getText()), genderId, String.valueOf(email.getText()), String.valueOf(mobile.getText()));
                if (NetworkObserver.isConnected(a)){
                    new ApiCalls(a, binding.getRoot()).addUser(user, null ,new ResponseCallback() {
                        @Override
                        public void onSuccess() {
                            fragmentTransaction.replace(activityMainBinding.fragment1.getId(), LoginFragment.class, null).commit();
                        }
                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    if (isUidAvailable()){
                        register();
                    } else {
                        User newUser = new User(uid,String.valueOf(name.getText()), String.valueOf(email.getText()), String.valueOf(mobile.getText()), genderId== 0 ? "Male": "Female" ,null,null, null);
                        userDao.insert(newUser);
                    }
                    Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
                    fragmentTransaction.replace(activityMainBinding.fragment1.getId(), LoginFragment.class, null).commit();
                }

            } else {
                Snackbar.make(binding.getRoot(), "User already exists.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isUidAvailable(){
        uid = random.nextInt(1000);
        List<Integer> userUids = userDao.getAllUid();
        return userUids.contains(uid);
    }


}