package com.app.fhm.cash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.fhm.DatabaseHandler;
import com.app.fhm.MainActivity;
import com.app.fhm.MainFragment;
import com.app.fhm.R;

import java.util.Calendar;


/**
 * A placeholder fragment containing a simple view.
 */
public class CashList extends MainFragment {

    private DatabaseHandler _db;
    private RecyclerView _RecyclerView;
    private RecyclerView.LayoutManager _LayoutManager;
    private View.OnClickListener paste;
    private int msg;
    private View v;
    private ProgressBar pbYearLimit, pbMonthLimit;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_paste).setVisible((MainActivity._cutcash != null));
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
            setList(" AND content LIKE '%" + newText + "%'");
            return false;
        }
    };

    View.OnClickListener edit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CashForm form = new CashForm();
            Cash c = null;
            if (v.getTag() != null) {
                c = (Cash) v.getTag();
            }
            MainActivity._cash = c;
            Snackbar.make(v, "edit Click cash", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, form).commit();
        }
    };

    private void setList(String query) {
        new CashAsyncTask(_RecyclerView, getActivity(), edit).execute(MainActivity._category, query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_paste:
                paste.onClick(null);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.cash_list, container, false);

        SharedPreferences _sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        MainActivity._cash = null;
        /* color */
        String bl = MainActivity._category.getTitle().substring(0, 1).toLowerCase();
        String packageName = getActivity().getPackageName();
        int resId = getActivity().getResources().getIdentifier(bl.toLowerCase(), "color", packageName);
        Toolbar toolbar = setToolbar(v, MainActivity._category.getTitle(), resId, View.VISIBLE);
        setHasOptionsMenu(true);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        Double sumYear = db.getSum(Calendar.YEAR, _sp.getString("_active_user", "default"), MainActivity._category);
        Double sumMonth = db.getSum(Calendar.MONTH, _sp.getString("_active_user", "default"), MainActivity._category);
        TextView tvYear = (TextView) v.findViewById(R.id.tvYearLimit);
        tvYear.setText(String.format("Jahr %.2f €", sumYear));
        TextView tvMonth = (TextView) v.findViewById(R.id.tvMonthLimit);
        tvMonth.setText(String.format("Monat %.2f €", sumMonth));


        pbYearLimit = (ProgressBar) v.findViewById(R.id.pbYearLimit);
        pbMonthLimit = (ProgressBar) v.findViewById(R.id.pbMonthLimit);

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
        pbYearLimit.setProgress((int) progressstate);
        progressstate = 0;
        if (iLm < sumMonth) {
            progressstate = 100;
        } else {
            progressstate = sumMonth * 100 / iLm;
        }
        pbMonthLimit.setProgress((int) progressstate);
        /* limit end */


        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        _RecyclerView = (RecyclerView) v.findViewById(R.id.recvie);
        _RecyclerView.setHasFixedSize(true);
        _LayoutManager = new LinearLayoutManager(getActivity());
        _RecyclerView.setLayoutManager(_LayoutManager);

        paste = new View.OnClickListener() {
            @Override
            public void onClick(View bv) {

                Cash cash = MainActivity._cutcash;
                cash.setCategory(MainActivity._category.getCategoryID());
                _db = new DatabaseHandler(getActivity());
                if (_db.saveCash(cash) == 1) {
                    msg = R.string.sPastesuccessfully;
                    MainActivity._cutcash = null;
                } else {
                    msg = R.string.sPastefaild;
                }
                CashList list = new CashList();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, list).commit();

                Snackbar.make(v, String.format(getString(msg), cash.getContent()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabCash);
        fab.setOnClickListener(edit);

        setList("");

        return v;
    }
}
