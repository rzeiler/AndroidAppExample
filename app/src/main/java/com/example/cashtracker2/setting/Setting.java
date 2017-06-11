package com.example.cashtracker2.setting;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashtracker2.DatabaseHandler;
import com.example.cashtracker2.R;
import com.example.cashtracker2.Services;
import com.example.cashtracker2.cash.Cash;
import com.example.cashtracker2.category.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by R.Zeiler on 12.05.2017.
 */

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private File root;
    private View view;
    private EditTextPreference etpLimitMonth, etpLimitYear, etpUser;
    private ListPreference _lp;
    private SharedPreferences _prefs;
    private DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
    private CharSequence entries[] = null, EntryValues[] = null;
    private ProgressDialog progressDialog;
    private Context context;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings, container, false);

        context = getActivity();
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView _title = (TextView) toolbar.findViewById(R.id.ToolbarTitle);
        _title.setText(R.string.sSettings);
        GridLayout gridLayout = (GridLayout) v.findViewById(R.id.ToolbarStatus);
        gridLayout.setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(getResources().getColor(R.color.colorGreylighten));

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.preferences);

        _prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        _prefs.registerOnSharedPreferenceChangeListener(this);

        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        etpLimitMonth = (EditTextPreference) findPreference("limitMonth");
        etpLimitMonth.setTitle(
                "Monatsgrenze (" + _prefs.getString("limitMonth", "0") + " " + dfs.getCurrencySymbol() + ")");

        etpLimitYear = (EditTextPreference) findPreference("limitYear");
        etpLimitYear.setTitle(
                "Jahresgrenze (" + _prefs.getString("limitYear", "0") + " " + dfs.getCurrencySymbol() + ")");

        etpUser = (EditTextPreference) findPreference("_active_user");
        etpUser.setTitle("Benutzer (" + _prefs.getString("_active_user", "default") + ")");

        if (root.exists()) {
            _lp = (ListPreference) findPreference("listRestore");
            ListDir(root);
        } else {
            Toast.makeText(getActivity(), "Documents Erro", Toast.LENGTH_LONG).show();
        }

        Preference btnBackup = findPreference("btnBackup");
        btnBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showProgressDialog();
                progressDialog.setTitle("Sicherung");
                new StoreDataBase().execute();
                return true;
            }
        });

        final CheckBoxPreference cbxAutoService = (CheckBoxPreference) findPreference("cbxAutoService");
        cbxAutoService
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(
                            Preference preference, Object newValue) {
                        if ((Boolean) newValue) {
                            Services.setReceiver(context);
                        } else {
                            Services.cancelReceiver(context);
                        }
                        return true;
                    }
                });
        this.view = view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("_active_user")) {
            etpUser.setTitle("Benutzer (" + sharedPreferences.getString("_active_user", " Unbekannt") + ")");
        }

        if (key.equals("limitYear")) {
            etpLimitYear.setTitle(
                    "Jahresgrenze (" + sharedPreferences.getString("limitYear", "0") + " " + dfs.getCurrencySymbol() + ")");
        }

        if (key.equals("limitMonth")) {
            etpLimitMonth.setTitle(
                    "Monatsgrenze (" + sharedPreferences.getString("limitMonth", "0") + " " + dfs.getCurrencySymbol() + ")");
        }

        if (key.equals("listRestore")) {
            String JSON = getDataFromFile(sharedPreferences.getString(key, ""));
            showProgressDialog();
            progressDialog.setTitle("Wiederherstellung");
            new RestoreDataBase().execute(JSON);
        }

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("");
        progressDialog.setTitle("Wiederherstellung");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Schließen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
    }

    private String getDataFromFile(String path) {
        String content = "";
        try {
            BufferedReader br;
            String sCurrentLine;
            br = new BufferedReader(new FileReader(path));
            while ((sCurrentLine = br.readLine()) != null) {
                content += sCurrentLine;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    void ListDir(File path) {

        String strDate;
        SimpleDateFormat fromdateFormat = new SimpleDateFormat("yyyy_MM_dd'T'HH_mm_ss");
        SimpleDateFormat todateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date date;

        if (path.exists()) {
                    /* Filter */
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    String lowercaseName = filename.toLowerCase();
                    return lowercaseName.startsWith("fmh_backup");
                }
            };
            File[] fList = path.listFiles(filter);
                    /* init */
            if (fList != null) {
                int c = fList.length;
                entries = new String[c];
                EntryValues = new String[c];
                for (int i = 0; i < c; i++) {
                    File f = fList[i];
                    strDate = f.getName().replace(".json", "").replace("fmh_backup_", "");
                    try {
                        date = fromdateFormat.parse(strDate);
                        entries[i] = todateFormat.format(date);
                        EntryValues[i] = f.getAbsolutePath();
                    } catch (ParseException e) {
                        entries[i] = strDate;
                        EntryValues[i] = f.getAbsolutePath();
                    }
                }
                _lp.setEntries(entries);
                _lp.setEntryValues(EntryValues);
                _lp.setEnabled(true);
            } else {
                _lp.setEnabled(false);

            }
        }
    }

    public class StoreDataBase extends AsyncTask<Object, Integer, String> {

        DatabaseHandler _db;

        @Override
        protected String doInBackground(Object... params) {
            _db = new DatabaseHandler(getActivity());
            List<Category> lcat = _db.getCategorys(null, null);
            JSONArray root = new JSONArray();
            JSONObject jcat, jcas;
            int index = 0, max = 0;
            max = lcat.size();
            for (Category category : lcat) {
                try {
                    /* state display */
                    double progressstate = (index + 1) * 100 / (max + 1);
                    publishProgress((int) progressstate);
                    index++;
                    jcat = category.ToJson();
                    List<Cash> lcas = _db.getCash(category, null);
                    for (Cash cash : lcas) {
                        jcas = cash.ToJson();
                        jcat.put("cash", jcas);
                    }
                    root.put(jcat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return root.toString();
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(String json) {
            progressDialog.setProgress(100);
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            progressDialog.setMessage("Erstellt");
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd'T'hh_mm_ss");
            File es = Environment.getExternalStoragePublicDirectory("Documents/");
            if (es.exists()) {
                String filename = Environment.getExternalStoragePublicDirectory("Documents/").getAbsolutePath() + "/fmh_backup_"
                        + ft.format(dNow) + ".json";
                boolean b = WriteFile(json, filename);
            } else {

                Toast.makeText(getActivity(), "Documents Erro", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RestoreDataBase extends AsyncTask<Object, Integer, Integer> {

        @Override
        protected Integer doInBackground(Object... params) {
            DatabaseHandler _db;
            String JSON = (String) params[0];
            JSONArray ja;
            try {
                ja = new JSONArray(JSON);
                JSONObject jo, sjo;
                _db = new DatabaseHandler(getActivity());
                _db.ClearTable();
                int max = ja.length();
                for (int index = 0; index < max; index++) {
                    /* state display */
                    double progressstate = (index + 1) * 100 / (max + 1);
                    publishProgress((int) progressstate);
                    jo = ja.getJSONObject(index);
                    /* add category */
                    Date _date = new Date();
                    try {
                        _date.setTime(0);
                        Object createdate = jo.get("createdate");
                        if (createdate instanceof Long) {
                            _date.setTime(jo.getLong("createdate"));
                        }
                        if (createdate instanceof String) {
                            _date = _db.getDateFromString(jo.getString("createdate"));
                        }
                        /* overwrite */
                        jo.put("createdate", _date.getTime());
                        jo.put("id", -1);
                        Category c = new Category(jo);
                        int iId = _db.saveCategory(c);
                        JSONArray cashArray = jo.getJSONArray("cash");
                        for (int cashIndex = 0; cashIndex < cashArray.length(); cashIndex++) {
                            sjo = cashArray.getJSONObject(cashIndex);
                            /* add cash */
                            try {
                                int ISCLONED = 1;
                                if (sjo.has("iscloned")) {
                                    ISCLONED = sjo.getInt("iscloned");
                                }
                                _date.setTime(0);
                                if (sjo.has("createdate")) {
                                    _date = _db.getDateFromString(sjo.getString("createdate"));
                                }
                                if (sjo.has("int_create_date")) {
                                    _date.setTime(sjo.getLong("int_create_date"));
                                }
                                sjo.put("createdate", _date.getTime());
                                sjo.put("id", -1);
                                sjo.put("iscloned", ISCLONED);
                                _db.saveCash(new Cash(sjo));
                            } catch (SQLiteException ex) {
                                //ex.printStackTrace();
                                Log.d("SQLiteException", ex.getMessage());
                            }
                        }
                    } catch (SQLiteException ex) {
                        //ex.printStackTrace();
                        Log.d("pro", String.valueOf(progressstate));
                    }
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.d("JSONException", e.getMessage());
            } catch (SQLiteException ex) {
                //ex.printStackTrace();
                Log.d("SQLiteException", ex.getMessage());
            }
            return 1;
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressDialog.setProgress(progress[0]);
        }

        protected void onPostExecute(Integer i) {
            progressDialog.setProgress(100);
            progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        }
    }

    private boolean WriteFile(String content, String path) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(path, false);
            outputStream.write(content.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}


