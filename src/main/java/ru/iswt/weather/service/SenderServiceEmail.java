package ru.iswt.weather.service;


import org.springframework.stereotype.Service;

@Service("senderService")
public class SenderServiceEmail implements SenderService {
    @Override
    public void send(String message) {

    }
}
