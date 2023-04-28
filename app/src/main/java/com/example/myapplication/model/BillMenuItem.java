package com.example.myapplication.model;

public class BillMenuItem {


    private String id;
    private String name;
    private String info;
    private String place;
    private float ratedInfo;
    private int imageResource;
    private int favouritedCount;

    public BillMenuItem() {
    }

    public BillMenuItem(String name, String info, String place, float ratedInfo, int imageResource, int favouritedCount) {


        this.name = name;
        this.info = info;
        this.place = place;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.favouritedCount = favouritedCount;

    }



    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPlace() {
        return place;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public int getImageResource(){
        return imageResource;
    }

    public int getFavouritedCount() {
        return favouritedCount;
    }

    public String _getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
