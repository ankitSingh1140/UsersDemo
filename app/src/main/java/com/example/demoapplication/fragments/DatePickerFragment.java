package com.example.demoapplication.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    final Calendar calendar = Calendar.getInstance();
    int year, month, day;

    public DatePickerDialog.OnDateSetListener myDateListener;
    public DatePickerFragment(){

    }
    public DatePickerFragment(DatePickerDialog.OnDateSetListener myDateListener){
        this.myDateListener = myDateListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), myDateListener, year, month, day);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        calendar.set(1960, 1, 1);
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.set(year, month, day);
        return  dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

    }
}
