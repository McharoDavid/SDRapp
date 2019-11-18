package com.sdr.android.Model;

public class Users {

    private String name, phone, password, image, address;

    public Users() {

    }

    public Users(String name, String phone, String password, String image, String address ) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name1) {
        this.name = name1;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone1) {
        this.phone = phone1;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password1) {
        this.password = password1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
