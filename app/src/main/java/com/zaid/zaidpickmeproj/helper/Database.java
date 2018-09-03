package com.zaid.zaidpickmeproj.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chatapp.db";

    public static final String table = "chatappdata";

    //Table Chat Infomation
    public static final String col1 = "message";
    public static final String col2 = "time_name";
    public static final String col3 = "type";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_String = "CREATE TABLE " + table + "(" + col1 + " TEXT," + col2 + " TEXT," + col3 + " TEXT" + ")";
        db.execSQL(SQL_String);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);

    }

    public long checkformessages(){
        SQLiteDatabase db = this.getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, table);
        db.close();
        return cnt;
    }

    public boolean insertChatInfo(String message,String time_name,String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col1, message);
        contentValues.put(col2, time_name);
        contentValues.put(col3, type);
        Long result = db.insert(table, null, contentValues);

        if (result == -1){
            return false;
        }
        else{
            return true;
        }

    }

    public Cursor getchatinfo() {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM chatappdata", null);

            return cursor;
        }finally {

        }
    }
}
