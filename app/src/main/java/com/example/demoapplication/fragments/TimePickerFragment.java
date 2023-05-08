package com.example.demoapplication.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    int hour, minute;
    Calendar calendar = Calendar.getInstance();

    TimePickerDialog.OnTimeSetListener myTimeListener;

    public TimePickerFragment(){

    }

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener myTimeListener){
        this.myTimeListener = myTimeListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(requireContext(), myTimeListener, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
