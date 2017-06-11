package com.example.cashtracker2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Ralf on 12.05.2017.
 */

public class Dialogs {

    private View.OnClickListener yes;
    private Context context;
    private Button dateButton;

    public Dialogs(Context c) {
        context = c;
    }

    public void Alert(View.OnClickListener y, String m) {
        yes = y;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(m).setPositiveButton(R.string.sYes, dialogClickListener)
                .setNegativeButton(R.string.sNo, dialogClickListener).show();
    }

    public Dialog OpenDatePicker(Button button, Date datetime) {
        dateButton = button;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(datetime);
        DatePickerDialog dpd = new DatePickerDialog(context, DateSet, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
        return dpd;
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    yes.onClick(null);
                    break;
            }
        }
    };

    DatePickerDialog.OnDateSetListener DateSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            dateButton.setText(String.format("%3$02d.%2$02d.%1$04d", arg1, arg2 + 1, arg3).toString());
        }
    };


}
