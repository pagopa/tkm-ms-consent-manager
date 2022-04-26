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

        System.out.println(secretClient.getSecret("sit-consentMDbUsername"));

        System.out.println("Starting ConsentManagerApplication");

        SpringApplication.run(ConsentManagerApplication.class, args);
    }

//    public static void buildAsyncClient() {
//        URL buildEndpoint = new URL("https://kmn-tkm-pagopa-test.vault.azure.net");
//        SecretServiceVersion serviceVersion = SecretServiceVersion.getLatest();
//        List<HttpPipelinePolicy> policies = new ArrayList();
//        String clientName = (String)this.properties.getOrDefault("name", "UnknownName");
//        String clientVersion = (String)this.properties.getOrDefault("version", "UnknownVersion");
//        HttpLogOptions httpLogOptions = new HttpLogOptions();
//
//        policies.add(new UserAgentPolicy(CoreUtils.getApplicationId(this.clientOptions, this.httpLogOptions), clientName, clientVersion, buildConfiguration));
//        if (this.clientOptions != null) {
//            List<HttpHeader> httpHeaderList = new ArrayList();
//            this.clientOptions.getHeaders().forEach((header) -> {
//                httpHeaderList.add(new HttpHeader(header.getName(), header.getValue()));
//            });
//            policies.add(new AddHeadersPolicy(new HttpHeaders(httpHeaderList)));
//        }
//
//        policies.addAll(this.perCallPolicies);
//        HttpPolicyProviders.addBeforeRetryPolicies(policies);
//        policies.add(this.retryPolicy == null ? new RetryPolicy() : this.retryPolicy);
//        policies.add(new KeyVaultCredentialPolicy(this.credential));
//        policies.addAll(this.perRetryPolicies);
//        HttpPolicyProviders.addAfterRetryPolicies(policies);
//        policies.add(new HttpLoggingPolicy(this.httpLogOptions));
//        HttpPipeline pipeline = (new HttpPipelineBuilder()).policies((HttpPipelinePolicy[])policies.toArray(new HttpPipelinePolicy[0])).httpClient(this.httpClient).build();
//        new SecretAsyncClient(this.vaultUrl, pipeline, serviceVersion);
//    }
}
