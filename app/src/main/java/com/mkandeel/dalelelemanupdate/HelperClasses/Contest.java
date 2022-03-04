package com.mkandeel.dalelelemanupdate.HelperClasses;

public class Contest {
    private String userid;
    private String name;
    private String country;
    private String city;
    private int count;

    public Contest(){}

    public Contest(String userid, String name, String country, String city , int count) {
        this.userid = userid;
        this.name = name;
        this.country = country;
        this.city = city;
        this.count = count;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}