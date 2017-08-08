package com.example.cashtracker2.category;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cashtracker2.DatabaseHandler;
import com.example.cashtracker2.Dialogs;
import com.example.cashtracker2.MainActivity;
import com.example.cashtracker2.MainFragment;
import com.app.fmh.R;
import com.example.cashtracker2.cash.Cash;
import com.example.cashtracker2.cash.CashList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CategoryList extends MainFragment {

    private RecyclerView _RecyclerView;
    private RecyclerView.LayoutManager _LayoutManager;
    private View v;
    private SharedPreferences _sp;
    private ProgressDialog progressDialog;
    private ProgressBar pbYearLimit, pbMonthLimit;

    private SimpleDateFormat DeDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(queryTextListener);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            setList(newText);
            return false;
        }
    };

    private void setList(String query) {

        View.OnClickListener edit = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryForm form = new CategoryForm();
                Category c = null;
                if (v.getTag() != null) {
                    c = (Category) v.getTag();
                }
                MainActivity._category = c;
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, form).commit();
            }
        };

        View.OnClickListener open = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashList list = new CashList();
                Category c = (Category) v.getTag();
                MainActivity._category = c;
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, list).commit();
            }
        };
        new CategoryAsyncTask(_RecyclerView, getActivity(), edit, open).execute(_sp, query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.category_list, container, false);

        _sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        MainActivity._category = null;

        Toolbar toolbar = setToolbar(v, getString(R.string.sCategorys), R.color.colorPrimary, View.VISIBLE);
        setHasOptionsMenu(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        Double sumYear = db.getSum(Calendar.YEAR, _sp.getString("_active_user", "default"));
        Double sumMonth = db.getSum(Calendar.MONTH, _sp.getString("_active_user", "default"));
        TextView tvYear = (TextView) v.findViewById(R.id.tvYearLimit);
        tvYear.setText(String.format("Jahr %.2f €", sumYear));
        TextView tvMonth = (TextView) v.findViewById(R.id.tvMonthLimit);
        tvMonth.setText(String.format("Monat %.2f €", sumMonth));

        pbYearLimit = (ProgressBar) v.findViewById(R.id.pbYearLimit);
        pbMonthLimit = (ProgressBar) v.findViewById(R.id.pbMonthLimit);
        Dialogs d = new Dialogs(getActivity());
        /* limit  */
        double iLm = Double.parseDouble(_sp.getString("limitMonth",
                "1000"));
        double iLy = Double.parseDouble(_sp.getString("limitYear",
                "12000"));
        double progressstate = 0;
        if (iLy < sumYear) {
            progressstate = 100;
        } else {
            progressstate = sumYear * 100 / iLy;
        }
        if (_sp.getBoolean("cbxAlert", false) && progressstate == 100&& MainActivity.checkYear) {
            /* alert */
            d.Alert(null, "Jahresgrenze überschritten!", false);
            MainActivity.checkYear = false;
        }
        pbYearLimit.setProgress((int) progressstate);
        progressstate = 0;
        if (iLm < sumMonth) {
            progressstate = 100;
        } else {
            progressstate = sumMonth * 100 / iLm;
        }
        if (_sp.getBoolean("cbxAlert", false) && progressstate == 100 && MainActivity.checkMonth) {
            /* alert */
            d.Alert(null, "Monatsgrenze überschritten!", false);
            MainActivity.checkMonth = false;
        }
        pbMonthLimit.setProgress((int) progressstate);
        /* limit end */
        _RecyclerView = (RecyclerView) v.findViewById(R.id.recvie);
        _RecyclerView.setHasFixedSize(true);
        _LayoutManager = new LinearLayoutManager(getActivity());
        _RecyclerView.setLayoutManager(_LayoutManager);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabCategory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryForm form = new CategoryForm();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, form).commit();
            }
        });

        setList(null);

        new CheckRepeats().execute(db, v);

        return v;
    }

    private class CheckRepeats extends AsyncTask<Object, Integer, Integer> {

        private View v;

        @Override
        protected Integer doInBackground(Object... params) {
            v = (View) params[1];
            DatabaseHandler db = (DatabaseHandler) params[0];
            Calendar c, b;
            Integer r = 0;
            for (int i = 1; i < 4; i++) {
                c = Calendar.getInstance();
                if (i == 1) {
                    c.set(Calendar.WEEK_OF_YEAR, c.get(Calendar.WEEK_OF_YEAR) - 1);
                }
                if (i == 2) {
                    c.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
                }
                if (i == 3) {
                    c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
                }
                Log.w("Index", String.valueOf(i));
                List<Cash> lc = db.getCash(" iscloned=0 AND repeat=" + i + " AND int_create_date <= " + c.getTimeInMillis() + " ");
                for (Cash cash : lc) {
                    b = Calendar.getInstance();
                    cash.setIsCloned(1);

                    if (db.saveCash(cash) > 0) {
                        b.setTime(new Date(cash.getCreateDate()));
                        Log.w("OldDate", DeDateFormat.format(new Date(b.getTimeInMillis())));
                        if (i == 1) {
                            b.set(Calendar.WEEK_OF_YEAR, b.get(Calendar.WEEK_OF_YEAR) + 1);
                        }
                        if (i == 2) {
                            b.set(Calendar.MONTH, b.get(Calendar.MONTH) + 1);
                        }
                        if (i == 3) {
                            b.set(Calendar.YEAR, b.get(Calendar.YEAR) + 1);
                        }
                        Log.w("cash", cash.toString());
                        r++;
                        Cash nc = new Cash(cash.getContent(), b.getTimeInMillis(), 0, cash.getCategory(), cash.getRepeat(), cash.getTotal(), 0);
                        db.saveCash(nc);
                    }
                }
            }
            return r;
        }

        protected void onPostExecute(Integer b) {
            if (b > 0) {
                Snackbar.make(v, String.format(getString(R.string.sRepeatsfind), b), Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setList(null);
                    }
                }).show();
            }
        }

    }


}
