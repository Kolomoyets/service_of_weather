package ru.iswt.weather.service;

import ru.iswt.weather.model.Subscription;

import java.util.List;


public interface ExecutorService {

    boolean registration(Subscription entity);

    boolean unRegistration(Subscription entity);

    long count();

    List<Subscription> getSubscriptions();

    void init();
}
