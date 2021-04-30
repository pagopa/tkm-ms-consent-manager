package it.gov.pagopa.tkm.ms.consentmanager.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class ApiBasePathConfiguration implements WebMvcConfigurer {

    @Value("${basePath}")
    private String basePath;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(basePath, (c) -> true);
    }

}

