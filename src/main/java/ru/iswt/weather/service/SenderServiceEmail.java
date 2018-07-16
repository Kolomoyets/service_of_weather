package ru.iswt.weather.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.iswt.weather.model.Subscription;

import javax.mail.internet.MimeMessage;

@Service("senderService")
public class SenderServiceEmail implements SenderService {

    private static final Logger LOG = LoggerFactory.getLogger(SenderServiceEmail.class);

    private JavaMailSender sender;

    @Override
    public void send(Subscription subscription, String message) {

        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setTo(subscription.getEmail());
            helper.setText(message);
            helper.setSubject("Оповещение об изменении погоды");

            //   sender.send(mimeMessage);
            LOG.info("Сообщение отправлено " + subscription.getEmail() + " " + message);
        } catch (Exception e) {
            LOG.error("Ошибка при отправке=" + message, e);
        }
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        return mailSender;
    }
}
