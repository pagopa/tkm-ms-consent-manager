package it.gov.pagopa.tkm.ms.consentmanager.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LogEventListener {

    @Value( "${keyvault.consentMDbUsernameAzure}" )
    private String consentMDbUsernameAzure;

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        String enableKafkaAppender = System.getenv("ENABLE_KAFKA_APPENDER");
        String azureKeyvaultProfile = System.getenv("AZURE_KEYVAULT_PROFILE");
        String kafkaAppenderTopic = System.getenv("KAFKA_APPENDER_TOPIC");
        log.info(String.format("Started Consent Manager with KEY VAULT profile '%s' and KAFKA APPENDER '%s' on topic '%s'",
                azureKeyvaultProfile, enableKafkaAppender, kafkaAppenderTopic));

        log.info("consentMDbUsernameAzure " + consentMDbUsernameAzure);
    }
}
