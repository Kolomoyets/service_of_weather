package ru.iswt.weather.service;

import ru.iswt.weather.model.Forecast;

import java.util.List;

public interface WeatherService {

    List<Forecast> load(String country, String city) throws Exception;


}
