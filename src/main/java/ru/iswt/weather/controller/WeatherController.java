package ru.iswt.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.iswt.weather.model.Subscription;
import ru.iswt.weather.service.ExecutorService;

import java.util.List;

@RestController
public class WeatherController {

    @Autowired
    ExecutorService service;

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        long a = service.count();
        return new ResponseEntity<>("Сервис погоды, количество подписчиков=" + a, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<Subscription>> subscriptionList() {
        List<Subscription> list = service.getSubscriptions();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/reg", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registration(@RequestBody Subscription entity) {
        if (service.registration(entity)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/unreg", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity unRegistration(@RequestBody Subscription entity) {
        if (service.unRegistration(entity)) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}