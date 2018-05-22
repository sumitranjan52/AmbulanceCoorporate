/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Model;

/**
 * Created by sumit on 06-Apr-18.
 */

public class RideBooking {

    private String id, riderName, pickupAddress, destinationAddress, status;
    private long fare;

    public RideBooking() {
    }

    public RideBooking(String id, String riderName, String pickupAddress, String destinationAddress, String status, long fare) {
        this.id = id;
        this.riderName = riderName;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.fare = fare;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getFare() {
        return fare;
    }

    public void setFare(long fare) {
        this.fare = fare;
    }
}
