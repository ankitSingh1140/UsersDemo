package com.example.demoapplication.services;

import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.demoapplication.R;
import com.google.android.material.textfield.TextInputEditText;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class AuthServices {

    public String[] countries = new String[]{"India", "Australia", "United States"};
    public String[] stateIndia = new String[]{"Gujarat", "Maharashtra", "Delhi"};
    public String[] stateAustralia = new String[]{"Sydney", "Adelaide", "Melbourne"};
    public String[] stateUnitedStates = new String[]{"New York", "New Jersey", "Miami"};
    List<String> countriesList = Arrays.asList(countries);
    List<String> india = Arrays.asList(stateIndia);
    List<String> australia = Arrays.asList(stateAustralia);
    List<String> unitedStates = Arrays.asList(stateUnitedStates);

    public boolean validationForEmail(TextInputEditText email){
        String pattern = "^(.+)@(\\S+)$";
        String numberPattern = "\\d{10}";
        String emailText = String.valueOf(email.getText());
        if (emailText.length() == 0){
            email.setError("Please Enter Your Email-Id Or Phone Number");
            return false;
        } else if(Pattern.compile(numberPattern).matcher(emailText).matches()){
            return true;
        } else if(Pattern.compile(pattern).matcher(emailText).matches()){
            return true;
        } else {
            email.setError("Please Enter a Valid Email-Id Or Phone Number");
            return false;
        }
    }

    public boolean validationForPass(TextInputEditText pass){
        String passText = String.valueOf(pass.getText());
        if (passText.length()==0){
            pass.setError("Please Enter Your Password");
            return false;
        } else return true;
    }

    public boolean validationForCPass(TextInputEditText pass, TextInputEditText cPass){
        String passText = String.valueOf(pass.getText());
        String cPassText = String.valueOf(cPass.getText());
        if (!cPassText.equals(passText)){
            cPass.setError("Password and Confirm Password must be same");
            return false;
        } else return true;
    }

    public boolean validationForName(TextInputEditText name){
        String nameText = String.valueOf(name.getText());
        if (nameText.length() == 0){
            name.setError("Please Enter Your Name");
            return false;
        } else return nameText.contains(" ");
    }

    public boolean validationForEmailRegister(TextInputEditText email){
        String pattern = "^(.+)@(\\S+)$";
        String emailText = String.valueOf(email.getText());
        if (emailText.length() == 0){
            email.setError("Please Enter Your Email");
            return false;
        } else if (Pattern.compile(pattern).matcher(emailText).matches()){
            return true;
        } else {
            email.setError("Please Enter a Valid Email");
            return false;
        }
    }

    public boolean validationForPhoneRegister(TextInputEditText phone){
        String phoneText = String.valueOf(phone.getText());
        String numberPattern = "\\d{10}";
        if (Pattern.compile(numberPattern).matcher(phoneText).matches()){
            return true;
        } else {
            phone.setError("Please Enter a valid Phone number");
            return false;
        }
    }

    public boolean validationForCountry(AutoCompleteTextView country){
        String countryText = String.valueOf(country.getText());
        if (!(countriesList.contains(countryText))){
            country.setError("Please Enter Your Country Name");
            return false;
        } else return true;
    }

    public boolean validationForState(AutoCompleteTextView country, AutoCompleteTextView state){
        String countryText = String.valueOf(country.getText());
        String stateText = String.valueOf(state.getText());
        if (Objects.equals(countryText, "India")){
            if (!india.contains(stateText)){
                state.setError("Please Enter your State name");
                return false;

            } else return true;
        } else if (Objects.equals(countryText, "Australia")){
            if (!australia.contains(stateText)){
                state.setError("Please Enter your State name");
                return false;

            } else return true;
        } else {
            if (!unitedStates.contains(stateText)){
                state.setError("Please Enter your State name");
                return false;

            } else return true;
        }
    }

    public boolean validationForDob(TextInputEditText dob){
        String dobText = String.valueOf(dob.getText());
        if (dobText.length() == 0){
            dob.setError("Please Enter your Date Of Birth");
            return false;
        } else return true;
    }


    // validate Email and Password of the user
    public boolean validationForEmailAndPassword(TextInputEditText email, TextInputEditText pass){
        return validationForEmail(email) && validationForPass(pass);
    }



    // validate all fields of the registration screen
    public boolean validationForRegistration(TextInputEditText name, TextInputEditText email, TextInputEditText phone, AutoCompleteTextView country, AutoCompleteTextView state, TextInputEditText dob, RadioGroup genderText, TextView genderErrorText, CheckBox readingText, CheckBox dancingText, CheckBox programmingText){
        int id = genderText.getCheckedRadioButtonId();
        String gender = (id == -1)? "" : (id == 1)? "Male" : "Female";
        Log.d("registration", gender);
        List<String> hobbies = new ArrayList<>();
        if (readingText.isChecked()){
            hobbies.add("Reading");
        }
        if (dancingText.isChecked()){
            hobbies.add("Dancing");
        }
        if (programmingText.isChecked()){
            hobbies.add("Programming");
        }
        Log.d("registration", hobbies.toString());
        if (validationForName(name) && validationForEmailRegister(email) && validationForPhoneRegister(phone) && validationForCountry(country) && validationForState(country, state) && validationForDob(dob)){
            return true;
        } else if (id == -1){
            genderErrorText.setText(R.string.errorText);
            return false;
        } else {
            genderErrorText.setText("");
            return true;
        }
    }

    public boolean validationForPassword(TextInputEditText pass, TextInputEditText cPass){
        return validationForPass(pass) && validationForCPass(pass,cPass);
    }

    public boolean validationForAddUser(TextInputEditText name, TextInputEditText email, TextInputEditText phone, RadioGroup genderText, TextView genderErrorText){
        int id = genderText.getCheckedRadioButtonId();
        if (validationForName(name) && validationForEmailRegister(email) && validationForPhoneRegister(phone)){
            return true;
        } else if (id == -1){
            genderErrorText.setText(R.string.errorText);
            return false;
        } else {
            genderErrorText.setText("");
            return true;
        }
    }


}
