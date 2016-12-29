package com.gvozditskiy.watermeter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alexey on 28.12.2016.
 */

public class Meter implements Serializable{
    public static final String TYPE_COLD="cold";
    public static final String TYPE_HOT="hot";
    private String name;
    private String type;
    private String flatUUID;
    private UUID uuid;

    public Meter() {}

    public Meter(String name, String type, String flatUUID) {
        this.name = name;
        this.type = type;
        this.flatUUID=flatUUID;
        createUuid();
    }

    public Map<String, String> meterToMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("type", type);
        map.put("flatUUID", flatUUID);
        map.put("uuid", uuid.toString());
        return map;
    }

    public Meter meterFromMap(Map<String, String> map) {
        Meter meter = new Meter();
        meter.setName(map.get("name"));
        meter.setType(map.get("type"));
        meter.setFlatUUID(map.get("flatUUID"));
        meter.setUuid(UUID.fromString(map.get("uuid")));
        return meter;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlatUUID() {
        return flatUUID;
    }

    public void setFlatUUID(String flatUUID) {
        this.flatUUID = flatUUID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public  void createUuid() {
        this.uuid = UUID.randomUUID();
    }
}
