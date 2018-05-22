package com.ambulance.corporate.Model;

/**
 * Created by sumit on 22-Feb-18.
 */

public class Driver {

    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String dl;
    private String vehicleNo;

    public Driver() {
    }

    public Driver(String name, String username, String password, String email, String phone, String dl, String vehicleNo) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.dl = dl;
        this.vehicleNo = vehicleNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
