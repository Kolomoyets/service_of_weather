package ru.iswt.weather.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iswt.weather.model.Forecast;
import ru.iswt.weather.model.Subscription;
import ru.iswt.weather.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service("executorService")
public class ExecutorServiceImpl implements ExecutorService {

    private ConcurrentLinkedQueue<Subscription> list = new ConcurrentLinkedQueue<>();

    @Autowired
    SubscriptionRepository repository;

    @Autowired
    WeatherService weatherService;

    @Override
    public void registration(Subscription entity) {
        repository.save(entity);
        list.add(entity);
    }

    @Override
    public void unRegistration(Subscription entity) {
        repository.delete(entity);
        list.remove(entity);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<Subscription> getSubscriptions() {
        List<Subscription> list = new ArrayList<>();
        for (Subscription subscription : repository.findAll()) {
            list.add(subscription);
        }
        return list;
    }

    @Override
    public void init() {
        for (Subscription subscription : repository.findAll()) {
            list.add(subscription);
        }
        execute();
    }

    private void execute() {
        while (true) {
            for (Subscription subscription : list) {
                List<Forecast> list = weatherService.load(subscription);


            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
