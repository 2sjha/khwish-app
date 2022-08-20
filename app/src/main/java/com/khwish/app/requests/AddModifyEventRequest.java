package com.khwish.app.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

public class AddModifyEventRequest implements Serializable {

    @SerializedName("event_id")
    @Expose
    private UUID eventId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("event_date")
    @Expose
    private Long eventDate;

    @SerializedName("location_lat")
    @Expose
    private Float locationLat;

    @SerializedName("location_long")
    @Expose
    private Float locationLong;

    public AddModifyEventRequest(String name, String description, Long eventDate, Float locationLat, Float locationLong) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
    }

    public AddModifyEventRequest(UUID eventId, String name, String description, Long eventDate, Float locationLat, Float locationLong) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.locationLat = locationLat;
        this.locationLong = locationLong;
    }
}