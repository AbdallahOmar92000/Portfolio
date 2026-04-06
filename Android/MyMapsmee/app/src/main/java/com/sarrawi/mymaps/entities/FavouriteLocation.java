package com.sarrawi.mymaps.entities;

public class FavouriteLocation {
    String name;
    String longitudes;
    String latitudes;
    String createdAt;
    String formatted_address;
    String photo_reference;
    String place_id;

    public FavouriteLocation(String name, String longitudes, String latitudes, String formatted_address, String photo_reference, String place_id) {
        this.name = name;
        this.longitudes = longitudes;
        this.latitudes = latitudes;
        this.formatted_address = formatted_address;
        this.photo_reference = photo_reference;
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(String longitudes) {
        this.longitudes = longitudes;
    }

    public String getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(String latitudes) {
        this.latitudes = latitudes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
