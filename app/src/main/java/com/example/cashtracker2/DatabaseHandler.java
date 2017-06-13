package com.example.cashtracker2;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.cashtracker2.cash.Cash;
import com.example.cashtracker2.category.Category;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    private final static int DATABASE_VERSION = 4;
    private static final String KEY_cID = "id";
    private static final String KEY_cTITLE = "title";
    //public static final String KEY_cCREATEDATE = "createdate";
    private static final String KEY_cCREATEDATE = "int_create_date";
    private static final String KEY_cISDELETED = "isdeleted";
    private static final String KEY_cUSER = "user";
    private static final String KEY_cRATING = "rating";
    private static final String KEY_sID = "id";
    private static final String KEY_sCONTENT = "content";
    //public static final String KEY_sCREATEDATE = "createdate";
    private static final String KEY_sCREATEDATE = "int_create_date";
    private static final String KEY_sISDELETED = "isdeleted";
    private static final String KEY_sCATEGORY = "category";
    private static final String KEY_sREPEAT = "repeat";
    private static final String KEY_sTOTAL = "total";
    private static final String KEY_sISCLONED = "iscloned";
    private static final String DATABASE_NAME = "fmh";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_CASH = "cash";
    private Context _context;

    private SimpleDateFormat DeDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat EnDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ProgressDialog progressDoalog = null;

    /* logging */
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "(" + KEY_cID + " INTEGER PRIMARY KEY,"
                + KEY_cTITLE + " TEXT," + KEY_cCREATEDATE + " INTEGER," + KEY_cISDELETED + " INTEGER," + KEY_cUSER
                + " TEXT," + KEY_cRATING + " INTEGER" + ")";
        try {
            db.execSQL(CREATE_CATEGORY_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();

        }

        String CREATE_CASH_TABLE = "CREATE TABLE " + TABLE_CASH + "(" + KEY_sID + " INTEGER PRIMARY KEY," + KEY_sCONTENT
                + " TEXT," + KEY_sCREATEDATE + " INTEGER," + KEY_sISDELETED + " INTEGER," + KEY_sCATEGORY + " INTEGER,"
                + KEY_sREPEAT + " INTEGER," + KEY_sTOTAL + " DECIMAL(10,2)," + KEY_sISCLONED
                + " INTEGER DEFAULT 0, FOREIGN KEY(" + KEY_sCATEGORY + ") REFERENCES " + TABLE_CATEGORY + "(" + KEY_cID
                + "))";
        try {
            db.execSQL(CREATE_CASH_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();

        }
    }


    public Date getDateFromString(String s) {
        Date _date = null;
        try {
            _date = DeDateFormat.parse(s);
        } catch (ParseException e) {

        }
        if (_date == null) {
            try {
                _date = EnDateFormat.parse(s);
            } catch (ParseException e) {

            }
        }
        return _date;
    }

    public void AlterTable(SQLiteDatabase db) {

        String query = "SELECT id, createdate FROM category ORDER BY rating DESC, title ASC;";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Date _date = getDateFromString(cursor.getString(1));
                    if (_date != null) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_cCREATEDATE, _date.getTime());
                        db.update(TABLE_CATEGORY, cv, KEY_cID + " = ?", new String[]{cursor.getString(0)});
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        /* set date */
        query = "SELECT id, createdate FROM cash ORDER BY createdate DESC;";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Date _date = getDateFromString(cursor.getString(1));
                    if (_date != null) {
                        ContentValues cv = new ContentValues();
                        cv.put(KEY_sCREATEDATE, _date.getTime());
                        int i = db.update(TABLE_CASH, cv, KEY_sID + " = ?", new String[]{cursor.getString(0)});
                        Log.w("cursor.getString(1)", cursor.getString(1) + " - " + String.valueOf(_date.getTime()) + " - result " + String.valueOf(i));
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    public void ClearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CATEGORY);
        db.execSQL("DELETE FROM " + TABLE_CASH);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//        if (newVersion <= oldVersion && newVersion == 1) {
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASH);
//            onCreate(db);
//        }
//
//
//        Log.w("onUpgrade", "run");
//
//        if (newVersion > oldVersion && newVersion == 3) {
//
//			/* cerate temp */
//            String query = "CREATE TABLE temp (" + KEY_sID + " INTEGER PRIMARY KEY," + KEY_sCONTENT + " TEXT,"
//                    + KEY_sCREATEDATE + " INTEGER," + KEY_sISDELETED + " INTEGER," + KEY_sCATEGORY + " INTEGER,"
//                    + KEY_sREPEAT + " INTEGER," + KEY_sTOTAL + " DECIMAL(10,2)," + KEY_sISCLONED
//                    + " INTEGER DEFAULT 0, FOREIGN KEY(" + KEY_sCATEGORY + ") REFERENCES " + TABLE_CATEGORY + "("
//                    + KEY_cID + "))";
//
//            try {
//                db.execSQL(query);
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//
//            }
//            /* insert temp */
//            query = " INSERT INTO temp (" + KEY_sCONTENT + ", " + KEY_sCREATEDATE + "," + KEY_sISDELETED + ","
//                    + KEY_sCATEGORY + "," + KEY_sREPEAT + "," + KEY_sTOTAL + "," + KEY_sISCLONED + ") SELECT "
//                    + KEY_sCONTENT + ", " + KEY_sCREATEDATE + "," + KEY_sISDELETED + "," + KEY_sCATEGORY + ","
//                    + KEY_sREPEAT + "," + KEY_sTOTAL + "," + KEY_sISCLONED + " FROM " + TABLE_CASH;
//
//            try {
//                db.execSQL(query);
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//            }
//
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASH);
//
//            query = "CREATE TABLE " + TABLE_CASH + "(" + KEY_sID + " INTEGER PRIMARY KEY," + KEY_sCONTENT + " TEXT,"
//                    + KEY_sCREATEDATE + " INTEGER," + KEY_sISDELETED + " INTEGER," + KEY_sCATEGORY + " INTEGER,"
//                    + KEY_sREPEAT + " INTEGER," + KEY_sTOTAL + " DECIMAL(10,2)," + KEY_sISCLONED
//                    + " INTEGER DEFAULT 0, FOREIGN KEY(" + KEY_sCATEGORY + ") REFERENCES " + TABLE_CATEGORY + "("
//                    + KEY_cID + "))";
//
//            try {
//                db.execSQL(query);
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//
//            }
//
//			/* insert temp */
//            query = " INSERT INTO " + TABLE_CASH + " (" + KEY_sCONTENT + ", " + KEY_sCREATEDATE + "," + KEY_sISDELETED + ","
//                    + KEY_sCATEGORY + "," + KEY_sREPEAT + "," + KEY_sTOTAL + "," + KEY_sISCLONED + ") SELECT "
//                    + KEY_sCONTENT + ", " + KEY_sCREATEDATE + "," + KEY_sISDELETED + "," + KEY_sCATEGORY + ","
//                    + KEY_sREPEAT + "," + KEY_sTOTAL + "," + KEY_sISCLONED + " FROM temp";
//
//            try {
//                db.execSQL(query);
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//
//            }
//
//            db.execSQL("DROP TABLE IF EXISTS temp");
//        }
//
//        if (newVersion > oldVersion && newVersion == 4) {
//            String query;
//            /* alter */
//            db.execSQL("ALTER TABLE " + TABLE_CATEGORY + " ADD COLUMN int_create_date INTEGER DEFAULT 0");
//            /* set date */
//            query = "SELECT id, createdate FROM category ORDER BY rating DESC, title ASC;";
//            try {
//                Cursor cursor = db.rawQuery(query, null);
//                if (cursor.moveToFirst()) {
//                    do {
//                        Date _date = null;
//                        try {
//                            _date = DeDateFormat.parse(cursor.getString(1));
//                        } catch (ParseException e) {
//
//                        }
//                        if (_date == null) {
//                            try {
//                                _date = EnDateFormat.parse(cursor.getString(1));
//                            } catch (ParseException e) {
//
//                            }
//                        }
//                        if (_date != null) {
//                            ContentValues cv = new ContentValues();
//                            cv.put(KEY_cCREATEDATE, _date.getTime());
//                            db.update(TABLE_CATEGORY, cv, KEY_cID + " = ?", new String[]{cursor.getString(0)});
//                        }
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//            }
//            /* alter */
//            db.execSQL("ALTER TABLE " + TABLE_CASH + " ADD COLUMN int_create_date INTEGER DEFAULT 0");
//            /* set date */
//            query = "SELECT id, createdate FROM cash ORDER BY createdate DESC;";
//            try {
//                Cursor cursor = db.rawQuery(query, null);
//                if (cursor.moveToFirst()) {
//                    do {
//                        Date _date = null;
//                        try {
//                            _date = DeDateFormat.parse(cursor.getString(1));
//                        } catch (ParseException e) {
//
//                        }
//                        if (_date == null) {
//                            try {
//                                _date = EnDateFormat.parse(cursor.getString(1));
//                            } catch (ParseException e) {
//
//                            }
//                        }
//                        if (_date != null) {
//                            ContentValues cv = new ContentValues();
//                            cv.put(KEY_sCREATEDATE, _date.getTime());
//                            db.update(TABLE_CASH, cv, KEY_sID + " = ?", new String[]{cursor.getString(1)});
//                        }
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();
//            } catch (SQLiteException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public String tsd(String s) {
        try {
            if (s.contains("-")) {
                String[] d = s.split("-");
                s = String.format("%3$02d.%2$02d.%1$04d", Integer.parseInt(d[0]), Integer.parseInt(d[1]),
                        Integer.parseInt(d[2]));
            } else {
                String[] d = s.split("\\.");
                s = String.format("%3$04d-%2$02d-%1$02d", Integer.parseInt(d[0]), Integer.parseInt(d[1]),
                        Integer.parseInt(d[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return s;
    }

    public int saveCategory(Category _category) {
        int i = 0;
        if (_category.getCategoryID() < 0) {
            i = addCategory(_category);
        } else {
            i = updateCategory(_category);
        }
        return i;
    }

    private int addCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        int iId = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_cTITLE, _category.getTitle());
            values.put(KEY_cCREATEDATE, _category.getCreateDate());
            values.put(KEY_cUSER, _category.getUser());
            values.put(KEY_cRATING, _category.getRating());
            values.put(KEY_cISDELETED, _category.getisDeleted());
            db.insert(TABLE_CATEGORY, null, values);
            Cursor cursor = db.rawQuery("SELECT Last_Insert_Rowid();", null);
            if (cursor != null) {
                cursor.moveToFirst();
                iId = cursor.getInt(0);
                cursor.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return iId;
    }

    private int updateCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        /* fill */
        ContentValues values = new ContentValues();
        values.put(KEY_cTITLE, _category.getTitle());
        values.put(KEY_cCREATEDATE, _category.getCreateDate());
        values.put(KEY_cUSER, _category.getUser());
        values.put(KEY_cRATING, _category.getRating());
        /* ex */
        try {
            return db.update(TABLE_CATEGORY, values, KEY_cID + "=?",
                    new String[]{String.valueOf(_category.getCategoryID())});
        } catch (SQLiteException e) {
            Log.w("error", e.getMessage());
            return 0;
        }
    }

    public List<Cash> getCash(String filter) {
        return getCash(null, filter);
    }

    public List<Cash> getCash(Category _category, String search) {
        List<Cash> _list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        Cursor cursor = null;
        if (_category != null) {
            query = "SELECT id, content, int_create_date, category, repeat, total, iscloned FROM cash WHERE category=? " + search + " ORDER BY " + KEY_sCREATEDATE + " DESC;";
            cursor = db.rawQuery(query, new String[]{String.valueOf(_category.getCategoryID())});
        } else {
            query = "SELECT id, content, int_create_date, category, repeat, total, iscloned FROM cash WHERE " + search + " ORDER BY " + KEY_sCREATEDATE + " DESC;";
            /* current date smaller */
            cursor = db.rawQuery(query, null);
        }
        try
        {

            if (cursor.moveToFirst()) {
                do {
                    Cash _cash = new Cash();
                    _cash.setCashID(cursor.getInt(0));
                    _cash.setContent(cursor.getString(1));
                    _cash.setCreateDate(cursor.getLong(2));
                    _cash.setCategory(cursor.getInt(3));
                    _cash.setRepeat(cursor.getInt(4));
                    _cash.setTotal(cursor.getDouble(5));
                    _cash.setIsCloned(cursor.getInt(6));
                    _list.add(_cash);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (
                SQLiteException e)

        {
            e.printStackTrace();
        }
        return _list;
    }

    public Double getSum(int Range, String user, Category category) {
        Calendar c = Calendar.getInstance();
        switch (Range) {
            case Calendar.MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case Calendar.YEAR:
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, 0);
                break;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        Double result = 0.0;
        /* year and month */
        Cursor cursor = null;
        if (category == null) {
            query = "SELECT IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.int_create_date >= ? AND b.user = ?;";
            cursor = db.rawQuery(query, new String[]{String.valueOf(c.getTimeInMillis()), user});
        } else {
            query = "SELECT IFNULL(SUM(a.total),0) FROM cash AS a, category AS b WHERE a.category=b.id AND a.int_create_date >= ? AND b.user = ? AND b.id = ?;";
            cursor = db.rawQuery(query, new String[]{String.valueOf(c.getTimeInMillis()), user, String.valueOf(category.getCategoryID())});
        }
        if (cursor.moveToFirst()) {
            do {
                result = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return result;
    }

    public Double getSum(int Range, String user) {
        return getSum(Range, user, null);
    }

    public List<Category> getCategorys(String filter, String search) {
        List<Category> _list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        //AlterTable(db);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        if (filter != null) {
            filter = " WHERE a.user='" + filter + "'";
        } else {
            filter = "";
        }
        if (search != null) {
            if (filter == "") {
                filter += " WHERE ";
            } else {
                filter += " AND ";
            }
            filter += " a.title LIKE '%" + search + "%'";
        } else {
            filter += "";
        }
        String query = "SELECT a.id, a.title, SUM(IFNull(b.total,0.00)),  a." + KEY_cCREATEDATE + ", a.rating, a.user FROM category AS a LEFT OUTER JOIN cash AS b ON b.category=a.id AND b." + KEY_sCREATEDATE + " >= ? " + filter + " GROUP BY a." + KEY_cID + " ORDER BY a.rating DESC, a.title ASC;";
        Log.w("query", query);
        try {
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(c.getTimeInMillis())});
            if (cursor.moveToFirst()) {
                do {
                    try {
                        Category _category = new Category();
                        _category.setCategoryID(cursor.getInt(0));
                        _category.setTitle(cursor.getString(1));
                        _category.setTotal(cursor.getDouble(2));
                        _category.setCreateDate(cursor.getLong(3));
                        _category.setRating(cursor.getInt(4));
                        _category.setUser(cursor.getString(5));
                        _list.add(_category);
                        _category = null;
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();

        }
        return _list;
    }

    public int deleteCategory(Category _category) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CATEGORY, KEY_cID + " = ?", new String[]{String.valueOf(_category.getCategoryID())});
    }

    public long saveCash(Cash _cash) {
        long i = 0;
        if (_cash.getCashID() < 0) {
            i = addCash(_cash);
        } else {
            i = updateCash(_cash);
        }
        return i;
    }

    private long addCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_sCONTENT, _cash.getContent());
            values.put(KEY_sCREATEDATE, _cash.getCreateDate());
            values.put(KEY_sCATEGORY, _cash.getCategory());
            values.put(KEY_sREPEAT, _cash.getRepeat());
            values.put(KEY_sTOTAL, _cash.getTotal());
            values.put(KEY_sISDELETED, _cash.getisDeleted());
            values.put(KEY_sISCLONED, _cash.getIsCloned());
            Log.w("addCash", _cash.toString());
            return db.insert(TABLE_CASH, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int updateCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_sCONTENT, _cash.getContent());
        values.put(KEY_sCREATEDATE, _cash.getCreateDate());
        values.put(KEY_sCATEGORY, _cash.getCategory());
        values.put(KEY_sREPEAT, _cash.getRepeat());
        values.put(KEY_sTOTAL, _cash.getTotal());
        values.put(KEY_sISDELETED, _cash.getisDeleted());
        values.put(KEY_sISCLONED,_cash.getIsCloned());

        return db.update(TABLE_CASH, values, KEY_sID + " = ?", new String[]{String.valueOf(_cash.getCashID())});
    }

    public int deleteCash(Cash _cash) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CASH, KEY_sID + " = ?", new String[]{String.valueOf(_cash.getCashID())});
    }

}
