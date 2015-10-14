package com.android.karl.saveaselfie;

/*
    Marker.java
    Marker class

    Copyright (c) 2015 Karl Jones. All rights reserved.
 */
public class Marker {

    private double longitude = 0.0;
    private double latitude = 0.0;
    private String message = null;
    private String type = null;
    private int id = 0;

    // Constructors, the name and the type cannot be null in the database, also needs a picture attached to it, saved as a bitmap.
    public Marker(String name, String type) {
        this.message = name;
        this.type = type;
    }
    public Marker(int id, double longitude, double latitude, String name, String type){
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.message = name;
        this.type = type;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
}
