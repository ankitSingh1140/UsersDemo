package com.example.demoapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.demoapplication.R;
import com.example.demoapplication.database.AppDatabase;
import com.example.demoapplication.database.DBInstance;
import com.example.demoapplication.database.User;
import com.example.demoapplication.database.UserDao;
import com.example.demoapplication.databinding.ActivityMainBinding;
import com.example.demoapplication.databinding.FragmentUserDetailsBinding;
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
import java.util.Objects;

public class UserDetailsFragment extends Fragment {
    int uid;
    boolean edit;

    FragmentUserDetailsBinding binding;
    ActivityMainBinding activityMainBinding;
    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText mobile;
    private RadioGroup gender;
    private RadioButton male;
    private RadioButton female;
    private TextView genderError;
    private AppCompatImageView profilePic;
    SharedPreferencesOperations sh = null;
    AuthServices auth = new AuthServices();
    Activity a;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    String TAG;
    UserDao userDao;
    User user;
    byte[] image = null;
    GetDataService service;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;



    public UserDetailsFragment() {
        // Required empty public constructor
    }

    public UserDetailsFragment(int uid, boolean edit){
        this.uid = uid;
        this.edit = edit;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = "User Details";
        a = getActivity();
        binding = FragmentUserDetailsBinding.inflate(getLayoutInflater());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        try {
            sh = new SharedPreferencesOperations(a);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        userDao = DBInstance.getUserDao(a);
        user = userDao.findByUid(uid);
        name = binding.userDetailsView.etName;
        email = binding.userDetailsView.etEmail;
        mobile = binding.userDetailsView.etMobile;
        gender = binding.userDetailsView.gender;
        male = binding.userDetailsView.male;
        female = binding.userDetailsView.female;
        genderError = binding.userDetailsView.genderError;
        profilePic = binding.ivImage;
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUserDetailsFields();
        if (edit) {
            editFields();
        }
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).addToBackStack(null);
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
        binding.buttonClose.setOnClickListener(v -> getParentFragmentManager().popBackStackImmediate());
        binding.changeImage.setOnClickListener(v -> openPhotoPicker());
        binding.buttonEdit.setOnClickListener(v -> editFields());
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    public void setUserDetailsFields() {
        name.setText(user.name);
        email.setText(user.email);
        mobile.setText(user.mobile);
        if (Objects.equals(user.gender, "Male")) {
            male.setChecked(true);
        } else {
            female.setChecked(true);
        }
        if (user.profilePic != null){
            profilePic.setImageBitmap(BitmapFactory.decodeByteArray(user.profilePic, 0, user.profilePic.length));
        }
        changeFields(false);
    }

    public void changeFields(boolean change){
        name.setEnabled(change);
        email.setEnabled(change);
        mobile.setEnabled(change);
        male.setEnabled(change);
        female.setEnabled(change);
        binding.changeImage.setVisibility(change? View.VISIBLE: View.INVISIBLE);
    }

    public void editFields() {
        changeFields(true);
        binding.buttonEdit.setImageResource(R.drawable.baseline_done_24);
        binding.buttonEdit.setOnClickListener(v -> saveFields());
    }

    public void saveFields(){
        if(auth.validationForAddUser(name, email, mobile, gender, genderError)){
            int id = gender.getCheckedRadioButtonId();
            int genderId = (id == male.getId())? 0 : 1;
            if (image == null){
                image = user.profilePic;
            }
            AddUserModel updatedUser = new AddUserModel(uid,String.valueOf(name.getText()), genderId, String.valueOf(email.getText()), String.valueOf(mobile.getText()));
            if (NetworkObserver.isConnected(a)){
                new ApiCalls(a, binding.getRoot()).updateUserApi(updatedUser, image, uid, new ResponseCallback() {
                    @Override
                    public void onSuccess() {
                        getParentFragmentManager().popBackStackImmediate();
                    }

                    @Override
                    public void onError() {

                    }
                });
            } else {
                userDao.updateUser(uid, String.valueOf(name.getText()), String.valueOf(email.getText()), String.valueOf(mobile.getText()), genderId == 0? "Male" : "Female", image, null);
                Snackbar.make(binding.getRoot(), "No Internet Available", Snackbar.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStackImmediate();
            }
            changeFields(false);
            user = userDao.findByUid(uid);
            binding.buttonEdit.setImageResource(R.drawable.baseline_edit_24);
            binding.buttonEdit.setOnClickListener(v -> editFields());
        }
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
                binding.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
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
                binding.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }

        }
    }
}