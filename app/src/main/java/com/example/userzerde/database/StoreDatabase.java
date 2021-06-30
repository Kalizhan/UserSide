package com.example.userzerde.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tandir.db";
    private static final int DATABASE_VERSION = 7;

//    public static final String TABLE_FOOD = "food_store";
    public static final String TABLE_ORDER = "order_history";
    public static final String TABLE_VER = "versions";

//    public static final String COLUMN_FKEY = "fkey";
    public static final String COLUMN_PHOTO = "photo";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_QUANTITY = "quantity";
//    public static final String COLUMN_FDESC = "description";
//    public static final String COLUMN_FAVAILABLE = "true";
//    public static final String COLUMN_FTYPE = "foodType";
    public static final String COLUMN_FPRICE = "price";
    public static final String COLUMN_FCOUNT = "count";

    public static final String COLUMN_FOOD_VER = "food_ver";


    Context context;

    public StoreDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

//        db.execSQL("CREATE TABLE " + TABLE_FOOD + "(" +
//                COLUMN_FKEY + " TEXT, " +
//                COLUMN_PHOTO + " TEXT, " +
//                COLUMN_NAME + " TEXT, " +
//                COLUMN_FDESC + " TEXT, " +
//                COLUMN_FAVAILABLE + " TEXT, " +
//                COLUMN_FTYPE + " TEXT, " +
//                COLUMN_FPRICE + " INTEGER )");

        db.execSQL("CREATE TABLE " + TABLE_ORDER + "(" +
                COLUMN_PHOTO + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_FCOUNT + " INTEGER, " +
                COLUMN_CODE + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_FPRICE + " INTEGER )");

        db.execSQL("CREATE TABLE " + TABLE_VER + "(" +
                COLUMN_FOOD_VER + " TEXT)");

        addVersions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);

        onCreate(db);
    }

//    public void cleanFoods(SQLiteDatabase db) {
//        db.execSQL("delete from " + TABLE_FOOD);
//
//    }

    public void cleanVersions(SQLiteDatabase db) {
        db.execSQL("delete from " + TABLE_VER);

    }

    public void addVersions(SQLiteDatabase db) {
        ContentValues versionValues = new ContentValues();
        versionValues.put(COLUMN_FOOD_VER, "0");

        db.insert(TABLE_VER, null, versionValues);
    }
}