package com.example.richardsenyange.breedlocator2;

class VetLocation {
    private double latitude;
    private double longitude;

    public VetLocation(double latitude, double longitude) {
    this.longitude = latitude;
    this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean exists(){
        if(getLatitude() != 0 && getLongitude() != 0){
            return  true;
        }
        else{
            return false;
        }
    }
}
