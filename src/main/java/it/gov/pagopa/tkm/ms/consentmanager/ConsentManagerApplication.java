package it.gov.pagopa.tkm.ms.consentmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ConsentManagerApplication {

    public static void main(String[] args) {
        System.out.println("Starting ConsentManagerApplication version 2");
        SpringApplication.run(ConsentManagerApplication.class, args);
    }
}
