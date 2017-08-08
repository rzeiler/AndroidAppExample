package com.app.fhm;


import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.app.fhm.R;

/**
 * Created by Ralf on 31.05.2017.
 */

public class MainFragment extends Fragment {


    public Toolbar setToolbar(View v, String title, int color, int visible) {

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(color));

        TextView _title = (TextView) toolbar.findViewById(R.id.ToolbarTitle);
        _title.setText(title);

        GridLayout gridLayout = (GridLayout) v.findViewById(R.id.ToolbarStatus);
        gridLayout.setBackgroundColor(getResources().getColor(color));
        gridLayout.setVisibility(visible);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return toolbar;
    }


    private NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);

    public boolean HasStringNoError(EditText _et) {
        if (_et.getText().toString().trim().equals("")) {
            _et.setError(getString(R.string.sEmpty));
            return false;
        } else {
            _et.setError(null);
            return true;
        }
    }

    public boolean HasNumberNoError(EditText _et) {
        try {
            Number n = nf.parse(gets(_et));
            _et.setError(null);
            return true;
        } catch (ParseException e) {
            _et.setError(getString(R.string.sError));
            return false;
        }
    }

    public boolean HasDateNoError(TextView _tv) {
        return (_tv.getText().length() == 10) ? true : false;
    }

    public String gets(EditText _et) {
        return _et.getText().toString();
    }


}
