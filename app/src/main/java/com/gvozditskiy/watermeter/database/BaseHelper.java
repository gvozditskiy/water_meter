package com.gvozditskiy.watermeter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gvozditskiy.watermeter.database.DbSchema.*;
import static com.gvozditskiy.watermeter.database.DbSchema.FlatsTable;
import static com.gvozditskiy.watermeter.database.DbSchema.IndTable;
import static com.gvozditskiy.watermeter.database.DbSchema.UserTable;

/**
 * Created by Alexey on 23.12.2016.
 */

public class BaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "database.db";


    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * создаем юзертэбйл
         */
        db.execSQL("create table " + UserTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UserTable.Cols.FIRSTNAME + ", " +
                UserTable.Cols.SECONDNAME + ", " +
                UserTable.Cols.PATRONYMIC + ", " +
                UserTable.Cols.STREET + ", " +
                UserTable.Cols.STREET_TYPE + ", " +
                UserTable.Cols.BUILDING + ", " +
                UserTable.Cols.PHONE + ", " +
                UserTable.Cols.FLAT_UUID + ", " +
                UserTable.Cols.FLAT + ")"
        );

        db.execSQL("create table " + IndTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                IndTable.Cols.UUID + ", " +
                IndTable.Cols.YEAR + ", " +
                IndTable.Cols.MONTH + ", " +
                IndTable.Cols.VALUE + ", " +
                IndTable.Cols.METER_UUID + ")"
        );

        db.execSQL("create table " + FlatsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FlatsTable.Cols.NAME + ", " +
                FlatsTable.Cols.UUID + ")"
        );

        db.execSQL("create table " + MeterTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
               MeterTable.Cols.NAME + ", " +
               MeterTable.Cols.TYPE + ", " +
               MeterTable.Cols.UUID + ", " +
                MeterTable.Cols.FLAT_UUID + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
/**
 * создаем юзертэбйл
 */

        db.execSQL("create table " + FlatsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FlatsTable.Cols.NAME + ", " +
                FlatsTable.Cols.UUID + ")"
        );

    }
}
