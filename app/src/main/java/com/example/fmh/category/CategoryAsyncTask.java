package com.example.cashtracker2.category;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cashtracker2.DatabaseHandler;

import java.util.List;

/**
 * Created by R.Zeiler on 12.05.2017.
 */

public class CategoryAsyncTask extends AsyncTask<Object, Void, List<Category>> {

    private RecyclerView rv;
    private Context context;
    private DatabaseHandler db;
    private View.OnClickListener edit, open;


    public CategoryAsyncTask(RecyclerView r, Context c, View.OnClickListener e, View.OnClickListener o) {
        db = new DatabaseHandler(c);
        rv = r;
        edit = e;
        open = o;
        context = c;
    }

    @Override
    protected List<Category> doInBackground(Object... params) {
        SharedPreferences prefs = (SharedPreferences) params[0];
        return db.getCategorys(prefs.getString("_active_user", "default"), (String) params[1]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Category> o) {
        CategoryAdapter ca = new CategoryAdapter(o, edit, open, context);
        ca.notifyDataSetChanged();
        rv.setAdapter(ca);
    }
}
