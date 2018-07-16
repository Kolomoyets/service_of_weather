package ru.iswt.weather.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customer;
    private String email;
    private String city;
    private String country;
    private int frequency;

    //Последняя погода
    private String lastWeather;

    public Subscription() {

    }

    public Subscription(String customer, String city, int frequency) {
        this.customer = customer;
        this.city = city;
        this.frequency = frequency;
    }


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastWeather() {
        return lastWeather;
    }

    public void setLastWeather(String lastWeather) {
        this.lastWeather = lastWeather;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", customer='" + customer + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}

