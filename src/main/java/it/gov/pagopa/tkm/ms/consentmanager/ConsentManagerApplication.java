package it.gov.pagopa.tkm.ms.consentmanager;

import it.gov.pagopa.tkm.config.CustomAnnotation;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableCaching
@Import(CustomAnnotation.class)
@Log4j2
public class ConsentManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsentManagerApplication.class, args);
    }

}
