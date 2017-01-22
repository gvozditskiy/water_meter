package com.gvozditskiy.watermeter.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gvozditskiy.watermeter.Flat;

import java.util.UUID;

/**
 * Created by Alexey on 27.12.2016.
 */

public class FlatCursorWrapper extends CursorWrapper {
    public FlatCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Flat getFlat(){
        Flat flat = new Flat();
        flat.setName(getString(getColumnIndex(DbSchema.FlatsTable.Cols.NAME)));
        UUID uuid = UUID.fromString(getString(getColumnIndex(DbSchema.FlatsTable.Cols.UUID)));
        flat.setUuid(uuid);
        return flat;
    }
}
