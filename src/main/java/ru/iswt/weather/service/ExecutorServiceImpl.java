package ru.iswt.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iswt.weather.model.City;
import ru.iswt.weather.model.Forecast;
import ru.iswt.weather.model.Subscription;
import ru.iswt.weather.repository.SubscriptionRepository;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service("executorService")
public class ExecutorServiceImpl implements ExecutorService {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceImpl.class);

    private ConcurrentMap<City, List<Forecast>> cities = new ConcurrentHashMap<>();

    private int delay = 30;

    @Autowired
    SubscriptionRepository repository;

    @Autowired
    WeatherService weatherService;

    @Autowired
    SenderService senderService;


    @Override
    public boolean registration(Subscription entity) {
        boolean exists = repository.existsByCustomerAndCountryAndCityAndFrequency(entity.getCustomer(), entity.getCountry(), entity.getCity(), entity.getFrequency());
        if (!exists) {
            repository.save(entity);
            City city = new City(entity.getCountry(), entity.getCity());
            List<Forecast> forecasts = cities.putIfAbsent(city, new CopyOnWriteArrayList<>());
            LOG.info("Всего городов=" + cities.size() + ", Подписка " + entity.toString());
            return true;
        } else {
            LOG.info("Подписка уже зарегистрирована=" + entity.toString());
            return false;
        }
    }

    @Override
    public boolean unRegistration(Subscription entity) {
        List<Subscription> list = repository.findByCustomerAndCountryAndCityAndFrequency(entity.getCustomer(), entity.getCountry(), entity.getCity(), entity.getFrequency());
        if (list.size() > 0) {
            repository.deleteAll(list);
            if (!repository.existsByCountryAndCity(entity.getCountry(), entity.getCity())) {
                cities.remove(new City(entity.getCountry(), entity.getCity()));
            }
            return true;
        } else {
            LOG.info("Отписка " + entity.toString());
        }
        return false;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<Subscription> getSubscriptions() {
        List<Subscription> list = new CopyOnWriteArrayList<>();
        for (Subscription subscription : repository.findAll()) {
            list.add(subscription);
        }
        return list;
    }

    @Override
    public void init() {
        for (Subscription e : repository.findAll()) {
            City city = new City(e.getCountry(), e.getCity());
            cities.putIfAbsent(city, new CopyOnWriteArrayList<>());
        }
        execute();
    }

    private void execute() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadWeather();
            }
        });
        thread.setName("load");


        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                process();
            }
        });
        thread2.setName("process");


        thread.start();
        thread2.start();
    }

    private void process() {
        while (true) {
            for (Subscription subscription : repository.findAll()) {
                String sCountry = subscription.getCountry();
                String sCity = subscription.getCity();

                City city = new City(sCountry, sCity);
                List<Forecast> forecasts = cities.get(city);
                if (forecasts != null) {
                    Date date = new Date();
                    Iterator<Forecast> iterator = forecasts.iterator();
                    int a = 0;
                    while (iterator.hasNext()) {
                        a++;
                        Forecast forecast = iterator.next();
                        if (hours(date, forecast.getDate()) > subscription.getFrequency()) {
                            if (subscription.getLastWeather() == null) {
                                subscription.setLastWeather(forecast.getWeather());
                                repository.save(subscription);
                            } else if (subscription.getLastWeather().equals(forecast.getWeather())) {
                                LOG.info("Погода " + sCountry + "-" + sCity + " не изменилась, шаг" + a + "," + subscription.getLastWeather());
                            } else {
                                subscription.setLastWeather(forecast.getWeather());
                                repository.save(subscription);
                                String message = subscription.getCustomer() + ", в городе " + sCity + " в течение " + subscription.getFrequency() + " часа ожидается изменение погоды на \"" + forecast.getWeather() + "\"";
                                senderService.send(subscription, message);
                            }
                            break;
                        }
                    }
                } else {
                    LOG.info("Прогноз погоды для города" + city + " не загружен");
                }
            }
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadWeather() {
        while (true) {
            Iterator<City> iterator = cities.keySet().iterator();
            while (iterator.hasNext()) {
                City city = iterator.next();
                city.setDate(new Date());
                List<Forecast> forecasts = null;
                try {
                    forecasts = weatherService.load(city.getCountry(), city.getCity());
                } catch (Exception e) {
                    LOG.error("Ошибка при загрузке данных", e);
                }

                if (forecasts != null) {
                    List<Forecast> oldForecasts = cities.get(city);
                    if (oldForecasts != null) {
                        oldForecasts.clear();
                        oldForecasts.addAll(forecasts);
                        LOG.info("Загружены данные для " + city.getCountry() + "-" + city.getCity() + ", " + oldForecasts.size());
                    }
                }
            }
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int hours(Date date1, Date date2) {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        return Math.abs((int) (date1.getTime() - date2.getTime()) / MILLI_TO_HOUR);
    }
}
