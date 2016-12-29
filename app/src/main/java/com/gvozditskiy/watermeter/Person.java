package com.gvozditskiy.watermeter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexey on 29.12.2016.
 */

public class Person {
    String surname;
    String name;
    String patronymic;

    String street;
    String building;
    String flat;

    String phone;

    public Person() {
    }

    public Person(String surname, String name, String patronymic, String street, String building, String flat, String phone) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.street = street;
        this.building = building;
        this.flat = flat;
        this.phone = phone;
    }

    public  Map<String, String> personToMap() {
        Map <String, String> personMap = new HashMap<>();
        personMap.put("surname", this.surname);
        personMap.put("name", this.name);
        personMap.put("patronymic", this.patronymic);
        personMap.put("street", this.street);
        personMap.put("building", this.building);
        personMap.put("flat", this.flat);
        personMap.put("phone", this.phone);
        return personMap;
    }

    public static Person personFromMap(Map<String, String> map) {
        Person person = new Person();
        person.setSurname(map.get("surname"));
        person.setName(map.get("name"));
        person.setPatronymic(map.get("patronymic"));
        person.setStreet(map.get("street"));
        person.setBuilding(map.get("building"));
        person.setFlat(map.get("flat"));
        person.setPhone(map.get("phone"));
        return person;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
