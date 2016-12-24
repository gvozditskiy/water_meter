package com.gvozditskiy.watermeter.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gvozditskiy.watermeter.Indication;

import static com.gvozditskiy.watermeter.database.DbSchema.*;

/**
 * Created by Alexey on 24.12.2016.
 */

public class IndicationCursorWrapper extends CursorWrapper {
    public IndicationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Indication getIndication(){
        Indication indication = new Indication();
        indication.setYear(getInt(getColumnIndex(IndTable.Cols.YEAR)));
        indication.setMonth(getInt(getColumnIndex(IndTable.Cols.MONTH)));
        indication.setCold(getInt(getColumnIndex(IndTable.Cols.COLD)));
        indication.setHot(getInt(getColumnIndex(IndTable.Cols.HOT)));
        return indication;
    }
}
