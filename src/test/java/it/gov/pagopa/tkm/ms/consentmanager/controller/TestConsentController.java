package it.gov.pagopa.tkm.ms.consentmanager.controller;

import com.fasterxml.jackson.databind.*;
import it.gov.pagopa.tkm.ms.consentmanager.config.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.controller.impl.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.*;
import org.springframework.http.converter.xml.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TestConsentController {

    @InjectMocks
    private ConsentControllerImpl consentController;

    @Mock
    private ConsentServiceImpl consentService;

    private DefaultBeans testBeans;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(consentController)
                .setControllerAdvice(new ErrorHandler())
                .setMessageConverters(
                        new ByteArrayHttpMessageConverter(),
                        new StringHttpMessageConverter(),
                        new ResourceHttpMessageConverter(),
                        new FormHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(),
                        new Jaxb2RootElementHttpMessageConverter())
                .build();
        testBeans = new DefaultBeans();
    }

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() throws Exception {
        for (Consent c : testBeans.VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = new ConsentResponse(c);
            when(consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, c)).thenReturn(consentResponse);
            mockMvc.perform(
                    post(ApiEndpoints.BASE_PATH_CONSENT)
                            .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE)
                            .header(ApiParams.CLIENT_ID_HEADER, testBeans.CLIENT_ID)
                            .content(mapper.writeValueAsString(c))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(consentResponse)));
        }
    }

    @Test
    public void givenInvalidConsentRequest_returnBadRequest() throws Exception {
        for (Consent c : testBeans.INVALID_CONSENT_REQUESTS) {
            mockMvc.perform(
                    post(ApiEndpoints.BASE_PATH_CONSENT)
                            .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE)
                            .header(ApiParams.CLIENT_ID_HEADER, testBeans.CLIENT_ID)
                            .content(mapper.writeValueAsString(c))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void givenMissingHeaders_returnBadRequest() throws Exception {
        mockMvc.perform(
                post(ApiEndpoints.BASE_PATH_CONSENT)
                        .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE)
                        .content(mapper.writeValueAsString(testBeans.GLOBAL_ALLOW_CONSENT_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(
                post(ApiEndpoints.BASE_PATH_CONSENT)
                        .header(ApiParams.CLIENT_ID_HEADER, testBeans.CLIENT_ID)
                        .content(mapper.writeValueAsString(testBeans.GLOBAL_ALLOW_CONSENT_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
