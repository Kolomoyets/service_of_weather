package ru.iswt.weather.repository;

import org.springframework.data.repository.CrudRepository;
import ru.iswt.weather.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    List<Subscription> findByCustomer(String customer);
}