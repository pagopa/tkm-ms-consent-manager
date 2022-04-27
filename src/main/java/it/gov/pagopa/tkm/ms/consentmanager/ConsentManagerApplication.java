package it.gov.pagopa.tkm.ms.consentmanager;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpHeader;
import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.policy.*;
import com.azure.core.util.Configuration;
import com.azure.core.util.CoreUtils;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.implementation.IdentityClientOptions;
import com.azure.security.keyvault.secrets.*;
import com.azure.security.keyvault.secrets.implementation.KeyVaultCredentialPolicy;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.spring.utils.ApplicationId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class ConsentManagerApplication {

    public static void main(String[] args) {
        retrieveSecret("sit-consentMDbUsername");
        retrieveSecret("sit-consentMDbPassword");
        retrieveSecret("sit-eventhubLogSaslJaasConfig");

        System.out.println("Starting ConsentManagerApplication");

        SpringApplication.run(ConsentManagerApplication.class, args);
    }

    private static void retrieveSecret(String name)
    {
        System.out.println("Creating client");

        String defaultAuthority = (new IdentityClientOptions()).getAuthorityHost();

        System.out.println("defaultAuthority " + defaultAuthority);

        TokenCredential credential = (new ClientSecretCredentialBuilder())
                .clientId("885fa44b-a84e-4b0a-8253-3483f5e70137")
                .clientSecret("Nnv~FQcU28pHFbmabD.jjnaa5K40L0g-~2")
                .tenantId("375b6ea7-a878-46bc-9a34-281c7b9e8d3d")
                .authorityHost(defaultAuthority).build();

        SecretClient secretClient = (new SecretClientBuilder())
                .vaultUrl("https://kmn-tkm-pagopa-test.vault.azure.net")
                .credential(credential)
                .serviceVersion(null)
                .httpLogOptions((new HttpLogOptions()).setApplicationId(ApplicationId.AZURE_SPRING_KEY_VAULT)).buildClient();

        System.out.println("Get secret");

        KeyVaultSecret secret = secretClient.getSecret(name);

        System.out.println(secret);
        if (secret != null) {
            System.out.println(secret.getValue());
        }
    }
}
