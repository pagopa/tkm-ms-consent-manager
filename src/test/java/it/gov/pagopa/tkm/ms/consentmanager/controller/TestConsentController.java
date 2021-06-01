package it.gov.pagopa.tkm.ms.consentmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.tkm.ms.consentmanager.config.ErrorHandler;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiEndpoints;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ApiParams;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.DefaultBeans;
import it.gov.pagopa.tkm.ms.consentmanager.controller.impl.ConsentControllerImpl;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.CardServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.service.impl.ConsentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TestConsentController {

    @InjectMocks
    private ConsentControllerImpl consentController;

    @Mock
    private ConsentServiceImpl consentService;

    private DefaultBeans testBeans;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
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

    //GET
    @Test
    void get_givenTaxCode_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    void get_givenTaxCodeAndHpan_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, testBeans.HPAN)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    void get_givenTaxCodeAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.SERVICES_QUERY_PARAM, testBeans.MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    void get_givenTaxCodeHpanAndServices_returnConsent() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)
                .param(ApiParams.HPAN_QUERY_PARAM, testBeans.HPAN)
                .param(ApiParams.SERVICES_QUERY_PARAM, testBeans.MULTIPLE_SERVICE_STRING_ARRAY)
                .header(ApiParams.TAX_CODE_HEADER, testBeans.TAX_CODE))
                .andExpect(status().isOk());
    }

    @Test
    void get_missingTaxCodeHeader_returnBadRequest() throws Exception {
        mockMvc.perform(get(ApiEndpoints.BASE_PATH_CONSENT)).andExpect(status().isBadRequest());
    }

    //POST
    @Test
    void givenValidConsentRequest_returnValidConsentResponse() throws Exception {
        for (Consent c : testBeans.VALID_CONSENT_REQUESTS) {
            Set<ServiceConsent> serviceConsents = (CollectionUtils.isEmpty(c.getServices()) ?
                    testBeans.CARD_SERVICES_FOR_ALL_SERVICES_SET
                    : testBeans.CARD_1_SERVICES
            ).stream().map(ServiceConsent::new).collect(Collectors.toSet());
            Set<CardServiceConsent> cardServiceConsents = serviceConsents.stream().map(cs ->
                    new CardServiceConsent(
                            c.getHpan(),
                            serviceConsents
                    )
            ).collect(Collectors.toSet());
            ConsentResponse consentResponse = new ConsentResponse(
                    c.isPartial() ? ConsentEntityEnum.Partial : ConsentEntityEnum.toConsentEntityEnum(c.getConsent()),
                    null,
                    c.isPartial() ? cardServiceConsents : null
            );
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
    void givenInvalidConsentRequest_returnBadRequest() throws Exception {
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
    void givenMissingHeaders_returnBadRequest() throws Exception {
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
