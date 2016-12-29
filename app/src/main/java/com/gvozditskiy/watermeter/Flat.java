package com.gvozditskiy.watermeter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alexey on 27.12.2016.
 */

public class Flat {
    String name;
    UUID uuid;

    public Flat() {
    }

    public Flat(String name) {
        this.name=name;
        this.uuid = UUID.randomUUID();
    }

    public Map<String, String> flatToMap() {
        Map <String, String> map = new HashMap<>();
        map.put("name",name);
        map.put("uuid", uuid.toString());
        return map;
    }

    public static Flat flatFromMap(Map<String, String> map) {
        Flat flat = new Flat();
        flat.setName(map.get("name"));
        flat.setUuid(UUID.fromString(map.get("uuid")));
        return flat;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void createUuid() {
        this.uuid = UUID.randomUUID();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


}
