package com.example.cashtracker2.cash;

import org.json.JSONException;
import org.json.JSONObject;

public class Cash {

    // private variables
    long _Id = -1;
    String _Content;
    long _CreateDate;
    int _isDeleted = 0;
    int _Category;
    int _Repeat;
    int _IsCloned = 0;
    double _Total;

    // Empty constructor
    public Cash() {

    }

    // getting CreateDate
    public String toString() {
        return String.format("ID:%1$s, Title:%2$s, CreateDate:%3$s, isDeleted:%4$s, Category:%5$s, Repeat:%6$s, Total:%7$s, IsCloned:%8$s ",
                this._Id, this._Content, this._CreateDate, this._isDeleted, this._Category, this._Repeat, this._Total, this._IsCloned);
    }

    // constructor
    public Cash(String Title, long CreateDate, int isDeleted, int Category, int Repeat, double Total, int IsCloned) {
        this._Content = Title;
        this._CreateDate = CreateDate;
        this._isDeleted = isDeleted;
        this._Category = Category;
        this._Repeat = Repeat;
        this._Total = Total;
        this._IsCloned = IsCloned;
    }

    // constructor
    public Cash(long Id, String Title, long CreateDate, int isDeleted, int Category, int Rating, double Total,
                int IsCloned) {
        this._Id = Id;
        this._Content = Title;
        this._CreateDate = CreateDate;
        this._isDeleted = isDeleted;
        this._Category = Category;
        this._Repeat = Rating;
        this._Total = Total;
        this._IsCloned = IsCloned;
    }

    // constructor
    public Cash(JSONObject obj) throws JSONException {
        this._Id = obj.getInt("id");
        this._Content = obj.getString("content");
        this._CreateDate = obj.getLong("createdate");
        this._isDeleted = obj.getInt("isdeleted");
        this._Category = obj.getInt("category");
        this._Repeat = obj.getInt("repeat");
        this._Total = obj.getInt("total");
        this._IsCloned = obj.getInt("iscloned");
    }

    public JSONObject ToJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("content", this._Content);
        obj.put("createdate", this._CreateDate);
        obj.put("isdeleted", this._isDeleted);
        obj.put("category", this._Category);
        obj.put("repeat", this._Repeat);
        obj.put("total", this._Total);
        obj.put("iscloned", this._IsCloned);
        return obj;
    }

    // getting ID
    public long getCashID() {
        return this._Id;
    }

    // setting id
    public void setCashID(long id) {
        this._Id = id;
    }

    // getting Title
    public String getContent() {
        return this._Content;
    }

    // setting Title
    public void setContent(String Title) {
        this._Content = Title;
    }

    // getting CreateDate
    public long getCreateDate() {
        return this._CreateDate;
    }

    // setting CreateDate
    public void setCreateDate(long CreateDate) {
        this._CreateDate = CreateDate;
    }

    // getting isDeleted
    public int getisDeleted() {
        return this._isDeleted;
    }

    // setting isDeleted
    public void setIsDeleted(int isDeleted) {
        this._isDeleted = isDeleted;
    }

    // getting Category
    public int getCategory() {
        return this._Category;
    }

    // setting Category
    public void setCategory(int Category) {
        this._Category = Category;
    }

    // getting Rating
    public int getRepeat() {
        return this._Repeat;
    }

    // setting id
    public void setRepeat(int Rating) {
        this._Repeat = Rating;
    }

    // getting Rating
    public double getTotal() {
        return this._Total;
    }

    // setting id
    public void setTotal(double Total) {
        this._Total = Total;
    }

    // getting Category
    public int getIsCloned() {
        return this._IsCloned;
    }

    // setting Category
    public void setIsCloned(int IsCloned) {
        this._IsCloned = IsCloned;
    }

}
