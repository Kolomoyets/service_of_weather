package ru.iswt.weather.model;

import java.util.Date;
import java.util.Objects;

public class City {
    private String country;
    private String city;
    private Date date;


    public City(String country, String city) {
        this.country = country;
        this.city = city;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city1 = (City) o;
        return Objects.equals(country, city1.country) &&
                Objects.equals(city, city1.city);
    }

    @Override
    public int hashCode() {

        return Objects.hash(country, city);
    }
}
