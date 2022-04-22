package it.gov.pagopa.tkm.ms.consentmanager;

import it.gov.pagopa.tkm.config.CustomAnnotation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.*;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
@Import(CustomAnnotation.class)
public class ConsentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsentManagerApplication.class, args);
    }
}
