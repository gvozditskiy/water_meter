package com.gvozditskiy.watermeter;

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
