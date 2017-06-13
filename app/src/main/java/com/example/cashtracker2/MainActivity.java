package com.example.cashtracker2;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.cashtracker2.cash.Cash;
import com.example.cashtracker2.category.Category;
import com.example.cashtracker2.category.CategoryList;
import com.example.cashtracker2.setting.Setting;

public class MainActivity extends AppCompatActivity {

    public static Category _category = null;
    public static Cash _cash = null;
    public static Cash _cutcash = null;
    public static Boolean checkMonth = true;
    public static Boolean checkYear = true;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CategoryList list = new CategoryList();
        getFragmentManager().beginTransaction().add(R.id.content_frame, list).commit();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Setting setting = new Setting();
            this.getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.content_frame, setting).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }



}
