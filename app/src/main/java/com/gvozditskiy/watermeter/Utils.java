package com.gvozditskiy.watermeter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;

import com.gvozditskiy.watermeter.database.BaseHelper;
import com.gvozditskiy.watermeter.database.DbSchema;
import com.gvozditskiy.watermeter.database.FlatCursorWrapper;
import com.gvozditskiy.watermeter.database.IndicationCursorWrapper;
import com.gvozditskiy.watermeter.database.MeterCursorWrapper;
import com.gvozditskiy.watermeter.database.PersonCursorWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static final String PREFS_PROFILE_STREET_TYPE = "street_type";
    public static final String PREFS_PROFILE_BUILDING = "building";
    public static final String PREFS_PROFILE_FLAT = "flat";
    public static final String PREFS_PROFILE_TELE = "telephone";
    private static SQLiteDatabase mDatabase;


    public static String getMessageBody(Context context, List<Indication> coldIndList,
                                        List<Indication> hotIndList, Flat flat) {
        StringBuilder sb = new StringBuilder();
        //получаем Person из базы
        Person person = null;
        for (Person p : getPersonList(context)) {
            if (p.getFlat_uuid().equals(flat.getUuid().toString())) {
                person = p;
            }
        }
        List<Meter> coldMeters = new ArrayList<>();
        List<Meter> hotMeters = new ArrayList<>();
        //получаем счетчики для квартиры из базы
        for (Meter meter : getMeterLsit(context)) {
            if (meter.getFlatUUID().equals(flat.getUuid().toString())) {
                if (meter.getType().equals(Meter.TYPE_COLD)) {
                    coldMeters.add(meter);
                } else {
                    hotMeters.add(meter);
                }
            }
        }

        String name = person.getName();
        String secName = person.getSurname();
        String otch = person.getPatronymic();
        String street = person.getStreet();
        String building = person.getBuilding();
        String flatS = person.getFlat();
        String[] array = context.getResources().getStringArray(R.array.streets);
        String type = person.getsType();
        String soc = "";
        switch (type) {
            case "Улица":
                soc = "Ул. ";
                break;
            case "Проспект":
                soc = "Пр-т. ";
                break;
            case "Переулок":
                soc = "Пер. ";
                break;
            case "Проезд":
                soc = "Пр. ";
                break;
            case "Бульвар":
                soc = "Б-р. ";
                break;

        }
        //Ул. Есенина, д.83, кв.117 / Недзьведь О.В. / хв - 55 / гв - 14
        sb.append(soc);
        sb.append(street);
        sb.append(", д.");
        sb.append(building);
        sb.append(", кв.");
        sb.append(flatS);
        sb.append(" / ");
        sb.append(secName + " ");
        sb.append(name.charAt(0) + ".");
        sb.append(otch.charAt(0) + ". / ");
        for (int i = 0; i < coldMeters.size(); i++) {
            for (Indication indication : coldIndList) {
                if (indication.getMeterUuid().equals(coldMeters.get(i).getUuid().toString())) {
                    sb.append(coldMeters.get(i).getName() + " - " + indication.getValue() + " / ");
                }
            }

        }
        for (int i = 0; i < hotMeters.size(); i++) {
            for (Indication indication : hotIndList) {
                if (indication.getMeterUuid().equals(hotMeters.get(i).getUuid().toString())) {
                    if (i != hotMeters.size() - 1) {
                        sb.append(hotMeters.get(i).getName() + " - " + indication.getValue() + " / ");
                    } else {
                        sb.append(hotMeters.get(i).getName() + " - " + indication.getValue());
                    }
                }
            }

        }

        return sb.toString();
    }

    public static String getPhone(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_PROFILE, Context.MODE_PRIVATE);
        return sp.getString(Utils.PREFS_PROFILE_TELE, "");
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

    private static FlatCursorWrapper queryFlat(Context context, String whereClaus, String[] whereArgs) {
        mDatabase = new BaseHelper(context).getWritableDatabase();
        Cursor cursor = mDatabase.query(
                DbSchema.FlatsTable.NAME,
                null,
                whereClaus,
                whereArgs,
                null,
                null,
                null
        );
        return new FlatCursorWrapper(cursor);
    }

    private static MeterCursorWrapper queryMeter(Context context, String whereClaus, String[] whereArgs) {
        mDatabase = new BaseHelper(context).getWritableDatabase();
        Cursor cursor = mDatabase.query(
                DbSchema.MeterTable.NAME,
                null,
                whereClaus,
                whereArgs,
                null,
                null,
                null
        );
        return new MeterCursorWrapper(cursor);
    }

    private static PersonCursorWrapper queryPerson(Context context, String whereClaus, String[] whereArgs) {
        mDatabase = new BaseHelper(context).getWritableDatabase();
        Cursor cursor = mDatabase.query(
                DbSchema.UserTable.NAME,
                null,
                whereClaus,
                whereArgs,
                null,
                null,
                null
        );
        return new PersonCursorWrapper(cursor);
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

        sortByMonth(indList);

        sortByYear(indList);


        return indList;
    }

    public static void sortByYear(List<Indication> indList) {
        Collections.sort(indList, new Comparator<Indication>() {
            @Override
            public int compare(Indication indication, Indication t1) {
                Integer i1 = indication.getYear();
                Integer i2 = t1.getYear();
                return i1.compareTo(i2);
            }
        });
    }

    public static void sortByMonth(List<Indication> indList) {
        Collections.sort(indList, new Comparator<Indication>() {
            @Override
            public int compare(Indication indication, Indication t1) {
                Integer i1 = indication.getMonth();
                Integer i2 = t1.getMonth();
                return i1.compareTo(i2);
            }
        });
    }

    /**
     * возвращает список квартир
     *
     * @param context
     * @return
     */
    public static List<Flat> getFlatList(Context context) {
        List<Flat> flats = new ArrayList<>();
        FlatCursorWrapper cursor = queryFlat(context, null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                flats.add(cursor.getFlat());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return flats;
    }

    /**
     * Возвращает список плательщиков
     *
     * @param context
     * @return
     */
    public static List<Person> getPersonList(Context context) {
        List<Person> persons = new ArrayList<>();
        PersonCursorWrapper cursor = queryPerson(context, null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                persons.add(cursor.getPerson());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return persons;
    }

    public static List<Meter> getMeterLsit(Context context) {
        List<Meter> meters = new ArrayList<>();
        MeterCursorWrapper cursor = queryMeter(context, null, null);
        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {
                meters.add(cursor.getMeter());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return meters;
    }

    public static void deleteIndications(Context context, String flatID, int year, int month) {
        List<Meter> meters = new ArrayList<>();
        for (Meter meter : getMeterLsit(context)) {
            if (meter.getFlatUUID().equals(flatID)) {
                meters.add(meter);
            }
        }
        List<Indication> fullIndList = getIndicationsList(0, context);
        List<Indication> inds = new ArrayList<>();
        for (Meter meter : meters) {
            for (Indication ind : fullIndList) {
                if (ind.getYear() == year &&
                        ind.getMonth() == month &&
                        ind.getMeterUuid().equals(meter.getUuid().toString())) {
                    inds.add(ind);
                }
            }
        }

        SQLiteDatabase database = new BaseHelper(context).getWritableDatabase();
        for (Indication ind : inds) {
            database.delete(DbSchema.IndTable.NAME, DbSchema.IndTable.Cols.UUID + " = "
                    , new String[]{ind.getUuid().toString()});
        }

    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
