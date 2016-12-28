package com.gvozditskiy.watermeter;

import java.util.UUID;

/**
 * Created by Alexey on 28.12.2016.
 */

public class Meter {
    public static final String TYPE_COLD="cold";
    public static final String TYPE_HOT="hot";
    private String name;
    private String type;
    private String flatUUID;
    private UUID uuid;

    public Meter(String name, String type, String flatUUID) {
        this.name = name;
        this.type = type;
        this.flatUUID=flatUUID;
        createUuid();
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
