package com.app.fhm.category;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

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
public class CategoryForm extends MainFragment {

    private DatabaseHandler _db;
    private EditText etTitle;
    private RatingBar rbFirma;
    private Button bDate;
    private SharedPreferences _sp;
    private int msg;
    private Category category = null;
    private View v;
    private View.OnClickListener save, remove;
    /**/
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_send).setVisible(true);
        menu.findItem(R.id.action_delete).setVisible((MainActivity._category != null));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                save.onClick(item.getActionView());
                break;
            case R.id.action_delete:
                msg = R.string.sConfirmremove;
                Dialogs d = new Dialogs(getActivity());
                d.Alert(remove, String.format(getString(msg), category.getTitle()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.category_form, container, false);
        setHasOptionsMenu(true);


        _sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        bDate = (Button) v.findViewById(R.id.bDate);
        etTitle = (EditText) v.findViewById(R.id.etCategory);
        rbFirma = (RatingBar) v.findViewById(R.id.rbPosition);

        category = MainActivity._category;

        setToolbar(v, getString((category != null) ? R.string.sEdit : R.string.sNew), R.color.colorPrimary, View.GONE);


        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        if (category != null) {
            etTitle.setText(category.getTitle());
            category.setCreateDate(category.getCreateDate());
            rbFirma.setRating(category.getRating());
        } else {
            category = new Category();
            category.setUser(_sp.getString("_active_user", "default"));
            category.setCreateDate(calendar.getTimeInMillis());
        }

        bDate.setText(DateFormat.format(category.getCreateDate()));

        remove = new View.OnClickListener() {
            @Override
            public void onClick(View bv) {
                _db = new DatabaseHandler(getActivity());
                if (_db.deleteCategory(category) == 1) {
                    msg = R.string.sRemovesuccessfully;
                } else {
                    msg = R.string.sRemovefaild;
                }
                MainActivity._category = null;
                getFragmentManager().popBackStack();

                Snackbar.make(v, String.format(getString(msg), category.getTitle()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        save = new View.OnClickListener() {

            @Override
            public void onClick(View bv) {

                if (HasStringNoError(etTitle)) {
                    _db = new DatabaseHandler(getActivity());
                    category.setTitle(String.valueOf(etTitle.getText()));
                    category.setRating((int) rbFirma.getRating());
                    Date _date = _db.getDateFromString(String.valueOf(bDate.getText()));
                    category.setCreateDate(_date.getTime());
                    int i = _db.saveCategory(category);
                    getFragmentManager().popBackStack();
                    if (i > 0) {
                        Snackbar.make(v, String.format(getString(R.string.sSavesuccessfully), category.getTitle()), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    MainActivity._category = null;

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
