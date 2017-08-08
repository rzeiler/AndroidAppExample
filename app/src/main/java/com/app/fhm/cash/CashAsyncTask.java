package com.app.fhm.cash;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.fhm.DatabaseHandler;
import com.app.fhm.category.Category;

import java.util.List;

/**
 * Created by R.Zeiler on 12.05.2017.
 */

public class CashAsyncTask extends AsyncTask<Object, Void, List<Cash>> {

    private RecyclerView rv;
    private DatabaseHandler db;
    private View.OnClickListener edit;
    private Context context;


    public CashAsyncTask(RecyclerView r, Context c, View.OnClickListener e) {
        db = new DatabaseHandler(c);
        rv = r;
        edit = e;
        context = c;
    }

    @Override
    protected List<Cash> doInBackground(Object... params) {
        return db.getCash((Category) params[0], (String) params[1]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Cash> o) {
        rv.setAdapter(new CashAdapter(o, edit, context));
    }
}
