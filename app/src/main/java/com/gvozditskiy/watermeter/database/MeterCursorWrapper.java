package com.gvozditskiy.watermeter.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gvozditskiy.watermeter.Indication;
import com.gvozditskiy.watermeter.Meter;

import java.util.UUID;

import static com.gvozditskiy.watermeter.database.DbSchema.*;
import static com.gvozditskiy.watermeter.database.DbSchema.IndTable;

/**
 * Created by Alexey on 24.12.2016.
 */

public class MeterCursorWrapper extends CursorWrapper {
    public MeterCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Meter getMeter() {
        Meter meter = new Meter();
        meter.setName(getString(getColumnIndex(MeterTable.Cols.NAME)));
        meter.setType(getString(getColumnIndex(MeterTable.Cols.TYPE)));
        meter.setFlatUUID(getString(getColumnIndex(MeterTable.Cols.TYPE)));
        meter.setUuid(UUID.fromString(getString(getColumnIndex(MeterTable.Cols.UUID))));
        return meter;
    }
}
