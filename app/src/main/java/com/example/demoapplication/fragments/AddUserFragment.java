package com.example.demoapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.FragmentAddUserBinding;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Random;

public class AddUserFragment extends Fragment {

    FragmentAddUserBinding binding;
    ActivityMainBinding activityMainBinding;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText mobile;
    private RadioGroup gender;
    private RadioButton male;
    private TextView genderError;
    SharedPreferencesOperations sh = null;
    AuthServices auth = new AuthServices();
    Activity a;
    UserDao userDao;
    List<User> users;
    byte[] image = null;
    GetDataService service;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Random random;
    int uid;


    public AddUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = getActivity();
        random = new Random();
        binding = FragmentAddUserBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        try {
            sh = new SharedPreferencesOperations(a);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        userDao = DBInstance.getUserDao(a);
        users = userDao.getAll(sh.getUid());
        name = binding.userDetailsView.etName;
        email = binding.userDetailsView.etEmail;
        mobile = binding.userDetailsView.etMobile;
        gender = binding.userDetailsView.gender;
        genderError = binding.userDetailsView.genderError;
        male = binding.userDetailsView.male;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null);
        // Inflate the layout for this fragment
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
        binding.buttonClose.setOnClickListener(v -> close());
        binding.changeImage.setOnClickListener(v -> openPhotoPicker());
        binding.buttonAdd.setOnClickListener(v -> addUser());
        return binding.getRoot();
    }
    public void addUser() {
        if (auth.validationForAddUser(name, email, mobile, gender, genderError)){
            int id = gender.getCheckedRadioButtonId();
            int genderId = (id == male.getId())? 0 : 1;
            users = userDao.findByEmailOrPhone(String.valueOf(email.getText()), String.valueOf(mobile.getText()));
            if(users.size() == 0){
                AddUserModel user = new AddUserModel(String.valueOf(name.getText()), genderId, String.valueOf(email.getText()), String.valueOf(mobile.getText()));
                if (NetworkObserver.isConnected(a)){
                    new ApiCalls(a, binding.getRoot()).addUser(user, image, new ResponseCallback() {
                        @Override
                        public void onSuccess() {
                            getParentFragmentManager().popBackStackImmediate();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                } else {
                    if (isUidAvailable()){
                        addUser();
                    }
                        User newUser = new User(uid,String.valueOf(name.getText()), String.valueOf(email.getText()), String.valueOf(mobile.getText()), genderId== 0 ? "Male": "Female" ,image,null, null);
                        userDao.insert(newUser);
                    Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStackImmediate();
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

    public void close(){
        getParentFragmentManager().popBackStackImmediate();
    }
    public void openPhotoPicker() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePicture,pickIntent});
        startActivityForResult(chooserIntent, 0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (data.getData() == null){
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
                image = bytes.toByteArray();
                binding.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            else {
                Uri imageUri = data.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(a.getContentResolver(), imageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
                image = bytes.toByteArray();
                binding.profilePic.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }

        }
    }
}