package it.gov.pagopa.tkm.ms.consentmanager;

import it.gov.pagopa.tkm.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
@EnableCaching
@Import(CustomAnnotation.class)
public class ConsentManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsentManagerApplication.class, args);
	}

}
