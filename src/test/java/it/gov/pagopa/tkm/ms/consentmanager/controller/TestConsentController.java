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
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc = MockMvcBuilders.standaloneSetup(consentController).setControllerAdvice(new ErrorHandler()).build();
        testBeans = new DefaultBeans();
    }

//GET
    @Test
    public void get_givenTaxCode_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    public void get_givenTaxCodeAndHpan_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, testBeans.HPAN)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    public void get_givenTaxCodeAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.SERVICES_QUERY_PARAM, testBeans.MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    public void get_givenTaxCodeHpanAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, testBeans.HPAN)
                .param(ApiParams.SERVICES_QUERY_PARAM, testBeans.MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

   /*@Test
    public void get_givenInvalidTaxCode_returnBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
               .header(ApiParams.TAX_CODE_HEADER, testBeans.INVALID_TAX_CODE))
               .andExpect(status().isBadRequest());

    }

    @Test
    public void get_givenInvalidHpan_returnBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
               .param(ApiParams.HPAN_QUERY_PARAM, testBeans.INVALID_HPAN)
               .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
               .andExpect(status().isBadRequest());
    } */

    @Test
    public void get_missingTaxCodeHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)).andExpect(status().isBadRequest());
    }

   //POST

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE);
        headers.set(ApiParams.CLIENT_ID_HEADER, String.valueOf(testBeans.CLIENT_ID));
        for (Consent c : testBeans.VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = new ConsentResponse(c);
            when(consentService.postConsent(testBeans.TAX_CODE, testBeans.CLIENT_ID, c)).thenReturn(consentResponse);
            mockMvc.perform(
                    post(ApiEndpoints.BASE_PATH_CONSENT)
                            .headers(headers)
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
                            .header(ApiParams.CLIENT_ID_HEADER, String.valueOf(testBeans.CLIENT_ID))
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
                        .header(ApiParams.CLIENT_ID_HEADER, String.valueOf(testBeans.CLIENT_ID))
                        .content(mapper.writeValueAsString(testBeans.GLOBAL_ALLOW_CONSENT_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
