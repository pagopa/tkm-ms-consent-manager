package it.gov.pagopa.tkm.ms.consentmanager.controller;

import org.junit.Test;

import com.fasterxml.jackson.databind.*;
import it.gov.pagopa.tkm.ms.consentmanager.config.*;
import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.controller.impl.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.TestBeans.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsentControllerTests {

    @InjectMocks
    private ConsentControllerImpl consentController;

    @Mock
    private ConsentServiceImpl consentService;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(consentController).setControllerAdvice(new ErrorHandler()).build();
    }

//GET
    @Test
    public void get_givenTaxCode_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .header(ApiParams.TAX_CODE_HEADER, TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    public void get_givenTaxCodeAndHpan_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, HPAN)
                .header(ApiParams.TAX_CODE_HEADER, TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    public void get_givenTaxCodeAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.SERVICES_QUERY_PARAM, MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, TAX_CODE))
                .andExpect(status().isOk());
    }


    //OK
    @Test
    public void get_givenTaxCodeHpanAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, HPAN)
                .param(ApiParams.SERVICES_QUERY_PARAM, MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, TAX_CODE))
                .andExpect(status().isOk());
    }

    //DA RIVEDERE, RESTITUISCE 200-OK NONOSTANTE IL TAX-CODE NON CORRETTO
    @Test
    public void get_givenInvalidTaxCode_returnNotFound() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
               .header(ApiParams.TAX_CODE_HEADER, INVALID_TAX_CODE))
                .andExpect(status().isNotFound());

    }

    @Test
    public void get_givenInvalidHpan_returnNotFound() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, INVALID_HPAN)
                .header(ApiParams.TAX_CODE_HEADER, TAX_CODE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_missingTaxCodeHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)).andExpect(status().isBadRequest());
    }

   //POST

    @Test
    public void givenValidConsentRequest_returnValidConsentResponse() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ApiParams.TAX_CODE_HEADER, TAX_CODE);
        headers.set(ApiParams.CLIENT_ID_HEADER, String.valueOf(CLIENT_ID));
        for (Consent c : VALID_CONSENT_REQUESTS) {
            ConsentResponse consentResponse = new ConsentResponse(c);
            given(consentService.postConsent(TAX_CODE, CLIENT_ID, c)).willReturn(consentResponse);
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
        for (Consent c : INVALID_CONSENT_REQUESTS) {
            mockMvc.perform(
                    post(ApiEndpoints.BASE_PATH_CONSENT)
                            .header(ApiParams.TAX_CODE_HEADER, TAX_CODE)
                            .header(ApiParams.CLIENT_ID_HEADER, String.valueOf(CLIENT_ID))
                            .content(mapper.writeValueAsString(c))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void givenMissingHeaders_returnBadRequest() throws Exception {
        mockMvc.perform(
                post(ApiEndpoints.BASE_PATH_CONSENT)
                        .header(ApiParams.TAX_CODE_HEADER, TAX_CODE)
                        .content(mapper.writeValueAsString(GLOBAL_ALLOW_CONSENT_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(
                post(ApiEndpoints.BASE_PATH_CONSENT)
                        .header(ApiParams.CLIENT_ID_HEADER, String.valueOf(CLIENT_ID))
                        .content(mapper.writeValueAsString(GLOBAL_ALLOW_CONSENT_REQUEST))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
