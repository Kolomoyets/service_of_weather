package ru.iswt.weather.service;

import ru.iswt.weather.model.Forecast;
import ru.iswt.weather.model.Subscription;

import java.util.List;

public interface WeatherService {

    List<Forecast> load(Subscription subscription);


}
