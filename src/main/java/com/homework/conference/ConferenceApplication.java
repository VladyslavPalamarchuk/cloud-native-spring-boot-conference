package com.homework.conference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConferenceApplication {
    // gateway чи це норм варіант для мепінгу в адаптор персістенса. Або як правильно робити. І чи норм що гейтвей буде класом, чи варто робити aka GatewayImpl
    //

    public static void main(String[] args) {
        SpringApplication.run(ConferenceApplication.class, args);
    }

}
