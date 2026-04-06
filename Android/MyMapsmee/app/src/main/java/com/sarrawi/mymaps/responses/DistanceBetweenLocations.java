package com.sarrawi.mymaps.responses;

import java.util.ArrayList;

public class DistanceBetweenLocations {
    ArrayList<String> destination_addresses;
    ArrayList<String> origin_addresses;
    ArrayList<Rows> rows;

    public DistanceBetweenLocations(ArrayList<String> destination_addresses, ArrayList<String> origin_addresses, ArrayList<Rows> rows) {
        this.destination_addresses = destination_addresses;
        this.origin_addresses = origin_addresses;
        this.rows = rows;
    }

    public ArrayList<String> getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(ArrayList<String> destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public ArrayList<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(ArrayList<String> origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public ArrayList<Rows> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Rows> rows) {
        this.rows = rows;
    }

    public class Elements {
        Info distance;
        Info duration;

        public Elements(Info distance, Info duration) {
            this.distance = distance;
            this.duration = duration;
        }

        public Info getDistance() {
            return distance;
        }

        public void setDistance(Info distance) {
            this.distance = distance;
        }

        public Info getDuration() {
            return duration;
        }

        public void setDuration(Info duration) {
            this.duration = duration;
        }
    }

    public class Info {
        String text;
        String value;

        public Info(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class Rows {
        ArrayList<Elements> elements;

        public Rows(ArrayList<Elements> elements) {
            this.elements = elements;
        }

        public ArrayList<Elements> getElements() {
            return elements;
        }

        public void setElements(ArrayList<Elements> elements) {
            this.elements = elements;
        }
    }
}
