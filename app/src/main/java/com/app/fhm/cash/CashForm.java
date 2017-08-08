package com.app.fhm.cash;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.app.fhm.DatabaseHandler;
import com.app.fhm.Dialogs;
import com.app.fhm.MainActivity;
import com.app.fhm.MainFragment;
import com.app.fhm.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class CashForm extends MainFragment {

    private DatabaseHandler _db;
    private EditText etDescription, etSum;
    private Spinner sRepeat;
    private Button bDate;
    private int msg;
    private Cash cash = null;
    private View v;
    private View.OnClickListener save, remove, cut;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_send).setVisible(true);
        menu.findItem(R.id.action_cut).setVisible((MainActivity._cash != null));
        menu.findItem(R.id.action_delete).setVisible((MainActivity._cash != null));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                save.onClick(null);
                break;
            case R.id.action_delete:
                msg = R.string.sConfirmremove;
                Dialogs d = new Dialogs(getActivity());
                d.Alert(remove, String.format(getString(msg), cash.getContent()));
                break;
            case R.id.action_cut:
                cut.onClick(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.cash_form, container, false);

        bDate = (Button) v.findViewById(R.id.bDate);
        etDescription = (EditText) v.findViewById(R.id.etDescription);
        etSum = (EditText) v.findViewById(R.id.etSum);
        sRepeat = (Spinner) v.findViewById(R.id.sRepeat);
        cash = MainActivity._cash;

        Toolbar toolbar = setToolbar(v, getString((cash != null) ? R.string.sEdit : R.string.sNew), R.color.colorPrimary, View.GONE);
        setHasOptionsMenu(true);

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        if (cash != null) {
            etDescription.setText(cash.getContent());
            etSum.setText(String.valueOf(cash.getTotal()));
        } else {
            cash = new Cash();
            cash.setTotal(0.00);
            cash.setCategory(MainActivity._category.getCategoryID());
            cash.setCreateDate(calendar.getTimeInMillis());
        }

        bDate.setText(DateFormat.format(cash.getCreateDate()));

        cut = new View.OnClickListener() {
            @Override
            public void onClick(View bv) {
                MainActivity._cutcash = MainActivity._cash;
                getFragmentManager().popBackStack();
            }
        };

        remove = new View.OnClickListener() {
            @Override
            public void onClick(View bv) {
                _db = new DatabaseHandler(getActivity());
                if (_db.deleteCash(cash) == 1) {
                    msg = R.string.sRemovesuccessfully;
                } else {
                    msg = R.string.sRemovefaild;
                }

                getFragmentManager().popBackStack();

                Snackbar.make(v, String.format(getString(msg), cash.getContent()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        save = new View.OnClickListener() {

            @Override
            public void onClick(View bv) {
                if (HasStringNoError(etDescription) && HasStringNoError(etSum) && HasNumberNoError(etSum)) {
                    _db = new DatabaseHandler(getActivity());
                    cash.setContent(String.valueOf(etDescription.getText()));
                    cash.setCategory(MainActivity._category.getCategoryID());
                    cash.setTotal(Double.parseDouble(String.valueOf(etSum.getText())));
                    cash.setRepeat((int) sRepeat.getSelectedItemId());
                    Date _date = _db.getDateFromString(String.valueOf(bDate.getText()));
                    cash.setCreateDate(_date.getTime());
                    long i = _db.saveCash(cash);
                    if (i > 0) {
                        Snackbar.make(v, String.format(getString(R.string.sSavesuccessfully), cash.getContent()), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    getFragmentManager().popBackStack();
                }
            }
        };

        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialogs d = new Dialogs(getActivity());
                try {
                    d.OpenDatePicker(bDate, DateFormat.parse(String.valueOf(bDate.getText())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }


}
