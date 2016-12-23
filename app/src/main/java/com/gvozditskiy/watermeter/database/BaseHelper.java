package com.gvozditskiy.watermeter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.gvozditskiy.watermeter.database.DbSchema.*;

/**
 * Created by Alexey on 23.12.2016.
 */

public class BaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
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
                UserTable.Cols.BUILDING + ", " +
                UserTable.Cols.FLAT + ")"
        );

        db.execSQL("create table "+IndTable.NAME+"("+
                " _id integer primary key autoincrement, " +
IndTable.Cols.YEAR+", "+
                IndTable.Cols.MONTH+", "+
                IndTable.Cols.COLD+", "+
                IndTable.Cols.HOT+")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
