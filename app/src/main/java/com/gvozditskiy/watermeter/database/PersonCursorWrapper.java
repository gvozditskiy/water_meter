package com.gvozditskiy.watermeter.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gvozditskiy.watermeter.Person;

/**
 * Created by Alexey on 24.12.2016.
 */

public class PersonCursorWrapper extends CursorWrapper {
    public PersonCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Person getPerson() {
        Person person = new Person();
        person.setName(getString(getColumnIndex(DbSchema.UserTable.Cols.FIRSTNAME)));
        person.setSurname(getString(getColumnIndex(DbSchema.UserTable.Cols.SECONDNAME)));
        person.setPatronymic(getString(getColumnIndex(DbSchema.UserTable.Cols.PATRONYMIC)));
        person.setsType(getString(getColumnIndex(DbSchema.UserTable.Cols.STREET_TYPE)));
        person.setStreet(getString(getColumnIndex(DbSchema.UserTable.Cols.STREET)));
        person.setBuilding(getString(getColumnIndex(DbSchema.UserTable.Cols.BUILDING)));
        person.setFlat(getString(getColumnIndex(DbSchema.UserTable.Cols.FLAT)));
        person.setFlat_uuid(getString(getColumnIndex(DbSchema.UserTable.Cols.FLAT_UUID)));
        person.setPhone(getString(getColumnIndex(DbSchema.UserTable.Cols.PHONE)));
        return person;
    }
}
