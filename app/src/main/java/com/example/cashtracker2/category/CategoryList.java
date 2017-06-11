package com.example.cashtracker2.category;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

import com.example.cashtracker2.DatabaseHandler;
import com.example.cashtracker2.MainActivity;
import com.example.cashtracker2.MainFragment;
import com.example.cashtracker2.R;
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
        tvYear.setText(String.format("%.2f €", sumYear));
        TextView tvMonth = (TextView) v.findViewById(R.id.tvMonthLimit);
        tvMonth.setText(String.format("%.2f €", sumMonth));

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

        Calendar c, b;
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
                b.setTime(new Date(cash.getCreateDate()));
                if (i == 1) {
                    b.set(Calendar.WEEK_OF_YEAR, b.get(Calendar.WEEK_OF_YEAR) + 1);
                }
                if (i == 2) {
                    b.set(Calendar.MONTH, b.get(Calendar.MONTH) + 1);
                }
                if (i == 3) {
                    b.set(Calendar.YEAR, b.get(Calendar.YEAR) + 1);
                }
                /*
                cash.setIsCloned(1);
                db.saveCash(cash);
                Cash nc = new Cash();
                nc = cash;
                nc.setCashID(-1);
                nc.setCreateDate(b.getTimeInMillis());
                db.saveCash(nc);
                */

            }

        }


        return v;
    }


}
