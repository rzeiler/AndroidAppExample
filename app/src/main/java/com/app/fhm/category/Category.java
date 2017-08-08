package com.app.fhm.category;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {

    // private variables
    int _Id = -1;
    String _Title;
    long _CreateDate;
    int _isDeleted;
    String _User;
    int _Rating;
    double _Total;


    // Empty constructor
    public Category() {

    }

    // constructor
    public Category(String Title, long CreateDate, int isDeleted,
                    String User, int Rating, double Total) {
        this._Title = Title;
        this._CreateDate = CreateDate;
        this._isDeleted = isDeleted;
        this._User = User;
        this._Rating = Rating;
        this._Total = Total;
    }

    // constructor
    public Category(int Id, String Title, long CreateDate, int isDeleted,
                    String User, int Rating, double Total) {
        this._Id = Id;
        this._Title = Title;
        this._CreateDate = CreateDate;
        this._isDeleted = isDeleted;
        this._User = User;
        this._Rating = Rating;
        this._Total = Total;
    }

    // constructor
    public Category(JSONObject obj) throws JSONException {
        this._Id = obj.getInt("id");
        this._Title =  obj.getString("title");
        this._CreateDate = obj.getLong("createdate");
        this._isDeleted = obj.getInt("isdeleted");
        this._User = obj.getString("user");
        this._Rating  = obj.getInt("rating");
     }

    // getting CreateDate
    public String toString() {
        return String.format("ID:%1$s, Title:%2$s, CreateDate:%3$s, isDeleted:%4$s, User:%5$s, Rating:%6$s, Total:%7$s ",
                this._Id, this._Title, this._CreateDate, this._isDeleted, this._User, this._Rating, this._Total);
    }


    public JSONObject ToJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("title", this._Title);
        obj.put("createdate", this._CreateDate);
        obj.put("isdeleted", this._isDeleted);
        obj.put("user",this._User);
        obj.put("rating",this._Rating);
        return obj;
    }

    // getting ID
    public int getCategoryID() {
        return this._Id;
    }

    // setting id
    public void setCategoryID(int id) {
        this._Id = id;
    }

    // getting Title
    public String getTitle() {
        return this._Title;
    }

    // setting Title
    public void setTitle(String Title) {
        this._Title = Title;
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
    public void setisDeleted(int isDeleted) {
        this._isDeleted = isDeleted;
    }

    // getting User
    public String getUser() {
        return this._User;
    }

    // setting User
    public void setUser(String User) {
        this._User = User;
    }

    // getting Rating
    public int getRating() {
        return this._Rating;
    }

    // setting id
    public void setRating(int Rating) {
        this._Rating = Rating;
    }

    // getting Rating
    public double getTotal() {
        return this._Total;
    }

    // setting id
    public void setTotal(double Total) {
        this._Total = Total;
    }


}
