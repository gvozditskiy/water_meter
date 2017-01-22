package com.gvozditskiy.watermeter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gvozditskiy.watermeter.database.DbSchema.FlatsTable;
import static com.gvozditskiy.watermeter.database.DbSchema.IndTable;
import static com.gvozditskiy.watermeter.database.DbSchema.MeterTable;
import static com.gvozditskiy.watermeter.database.DbSchema.UserTable;

/**
 * Created by Alexey on 23.12.2016.
 */

public class BaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "database.db";


    public BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);

        createIndTable(db);

        createFlatTable(db);

        createMeterTable(db);

    }

    private void createMeterTable(SQLiteDatabase db) {
        db.execSQL("create table " + MeterTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
               MeterTable.Cols.NAME + ", " +
               MeterTable.Cols.TYPE + ", " +
               MeterTable.Cols.UUID + ", " +
                MeterTable.Cols.FLAT_UUID + ")"
        );
    }

    private void createFlatTable(SQLiteDatabase db) {
        db.execSQL("create table " + FlatsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                FlatsTable.Cols.NAME + ", " +
                FlatsTable.Cols.UUID + ")"
        );
    }

    private void createIndTable(SQLiteDatabase db) {
        db.execSQL("create table " + IndTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                IndTable.Cols.UUID + ", " +
                IndTable.Cols.YEAR + ", " +
                IndTable.Cols.MONTH + ", " +
                IndTable.Cols.VALUE + ", " +
                IndTable.Cols.METER_UUID + ")"
        );
    }

    private void createUserTable(SQLiteDatabase db) {
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        //стираем старые таблицы
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FlatsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IndTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MeterTable.NAME);

        //создаем новые таблицы
        createUserTable(db);

        createIndTable(db);

        createFlatTable(db);

        createMeterTable(db);

    }
}
