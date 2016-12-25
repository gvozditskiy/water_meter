package com.gvozditskiy.watermeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.database.IndicationCursorWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Вспомогательный класс
 * Created by Alexey on 25.12.2016.
 */

public class Utils {
    public static final String PREFS_PROFILE = "profile_preferences";
    public static final String PREFS_PROFILE_NAME = "name";
    public static final String PREFS_PROFILE_SECNAME = "secname";
    public static final String PREFS_PROFILE_OTCH = "otch";
    public static final String PREFS_PROFILE_STREET = "street";
    public static final String PREFS_PROFILE_BUILDING = "building";
    public static final String PREFS_PROFILE_FLAT = "flat";
    public static final String PREFS_PROFILE_TELE = "telephone";
    private static SQLiteDatabase mDatabase;


    public static String getMessageBody(Context context, int cold, int hot) {
        StringBuilder sb = new StringBuilder();
        SharedPreferences sp = context.getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        String name = (sp.getString(Utils.PREFS_PROFILE_NAME, ""));
        String secName = (sp.getString(Utils.PREFS_PROFILE_SECNAME, ""));
        String otch = (sp.getString(Utils.PREFS_PROFILE_OTCH, ""));
        String street = (sp.getString(Utils.PREFS_PROFILE_STREET, ""));
        String building = (sp.getString(Utils.PREFS_PROFILE_BUILDING, ""));
        String flat = (sp.getString(Utils.PREFS_PROFILE_FLAT, ""));
        //Ул. Есенина, д.83, кв.117 / Недзьведь О.В. / хв - 55 / гв - 14
        sb.append("Ул. ");
        sb.append(street);
        sb  .append(", д.");
        sb.append(building);
        sb.append(", кв.");
        sb.append(flat);
        sb.append(" / ");
        sb.append(secName+" ");
        sb.append(name.charAt(0)+".");
        sb.append(otch.charAt(0)+". / ");
        sb.append("хв - ");
        sb.append(cold);
        sb.append(" / гв - ");
        sb.append(hot);
        return sb.toString();
    }

    private static IndicationCursorWrapper queryIndication(Context context, String whereClaus, String[] whereArgs) {
        mDatabase = new BaseHelper(context).getWritableDatabase();
        Cursor cursor = mDatabase.query(
                DbSchema.IndTable.NAME,
                null,
                whereClaus,
                whereArgs,
                null,
                null,
                null
        );
        return new IndicationCursorWrapper(cursor);
    }

    /**
     * Возвращает список индикаций за год
     *
     * @param year
     * @return
     */
    public static List<Indication> getIndicationsList(int year, Context context) {
        List<Indication> indList = new ArrayList<>();
        IndicationCursorWrapper cursorWrapper = queryIndication(context, null,
                null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                indList.add(cursorWrapper.getIndication());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return indList;
    }
}
