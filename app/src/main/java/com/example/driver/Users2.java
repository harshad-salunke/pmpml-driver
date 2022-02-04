package com.example.driver;


public  class Users2{
    double latitude;
    double longitude;
    String title;
    float baring;

    public float getBaring() {
        return baring;
    }

    public void setBaring(float baring) {
        this.baring = baring;
    }

    public Users2(double latitude, double longitude, float baring, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.baring=baring;
    }

    public Users2() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}