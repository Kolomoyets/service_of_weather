package ru.iswt.weather.service;

import org.springframework.stereotype.Service;
import ru.iswt.weather.model.Subscription;

import java.util.List;


public interface ExecutorService {

    void registration(Subscription entity);

    void unRegistration(Subscription entity);

    long count();

    List<Subscription> getSubscriptions();

    void init();
}
