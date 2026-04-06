package com.sarrawi.mymaps.entities;

import java.util.ArrayList;

public class Address {
    String formatted_address;
    Geometry geometry;
    String name;
    String place_id;
    ArrayList<Photos> photos;

    public Address(String formatted_address, String name, String place_id, ArrayList<Photos> photos, Geometry geometry) {
        this.formatted_address = formatted_address;
        this.name = name;
        this.place_id = place_id;
        this.photos = photos;
        this.geometry = geometry;
    }

    public FavouriteLocation getFavouriteLocation(){
        return new FavouriteLocation(name, getLongitudes(), getLongitudes(), formatted_address, getFirstImage(), place_id);
    }

    public String getLatitudes() {
        return this.geometry.location.lat;
    }

    public String getLongitudes() {
        return this.geometry.location.lng;
    }

    public String getFirstImage(){
        return photos.get(0).photo_reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public ArrayList<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photos> photos) {
        this.photos = photos;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}

class Geometry {
    public Location location;

    public Geometry(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

class Location {
    String lat;
    String lng;

    public Location(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}

class Photos {
    int height;
    String photo_reference;

    public Photos(int height, String photo_reference) {
        this.height = height;
        this.photo_reference = photo_reference;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }
}