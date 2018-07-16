package ru.iswt.weather.service;

import ru.iswt.weather.model.Subscription;

public interface SenderService {

    void send(Subscription subscription, String message);
}
