package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.client.cardmanager.CardManagerClient;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentRequestEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum;
import it.gov.pagopa.tkm.ms.consentmanager.constant.ServiceEnum;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentDataNotFoundException;
import it.gov.pagopa.tkm.ms.consentmanager.exception.ConsentException;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCard;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCitizen;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmService;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.Consent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.CardServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ConsentResponse;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.ServiceConsent;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CardServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.CitizenRepository;
import it.gov.pagopa.tkm.ms.consentmanager.repository.ServiceRepository;
import it.gov.pagopa.tkm.ms.consentmanager.service.CircuitBreakerManager;
import it.gov.pagopa.tkm.ms.consentmanager.service.ConsentService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum.*;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum.*;

@Service
@Log4j2
public class ConsentServiceImpl implements ConsentService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardServiceRepository cardServiceRepository;

    @Autowired
    private CardManagerClient cardManagerClient;

    @Autowired
    private CircuitBreakerManager circuitBreakerManager;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
        log.info("Post consent for taxCode " + taxCode + " with value " + consent.getConsent() + (consent.isPartial() ? " for hpan " + consent.getHpan() : ""));
        TkmCitizen citizen = updateOrCreateCitizen(taxCode, clientId, consent);
        ConsentResponse consentResponse = new ConsentResponse();
        if (consent.isPartial()) {
            log.info("Services to update: " + (CollectionUtils.isEmpty(consent.getServices()) ? "all" : consent.getServices().stream().map(Enum::name).collect(Collectors.joining(", "))));
            TkmCard card = getOrCreateCard(citizen, consent.getHpan());
            List<TkmService> services = CollectionUtils.isEmpty(consent.getServices()) ?
                    serviceRepository.findAll() :
                    serviceRepository.findByNameIn(consent.getServices());
            Set<TkmCardService> cardServices = updateOrCreateCardServices(services, card, consent.getConsent());
            CardServiceConsent cardServiceConsents = new CardServiceConsent(
                    card.getHpan(),
                    cardServices.stream().map(ServiceConsent::new).collect(Collectors.toSet())
            );
            consentResponse.setConsent(Partial);
            consentResponse.setDetails(Collections.singleton(cardServiceConsents));
        } else {
            checkServicesNotPresentWithGlobalConsent(consent.getServices());
            List<TkmService> allServices = serviceRepository.findAll();
            citizen.getCards().forEach(c -> updateOrCreateCardServices(allServices, c, consent.getConsent()));
            consentResponse.setConsent(ConsentEntityEnum.toConsentEntityEnum(consent.getConsent()));
        }
        consentResponse.setTaxCode(taxCode);
        notifyCardManager(consentResponse);
        consentResponse.setLastUpdateDate(citizen.getLastConsentUpdateDate());
        consentResponse.setTaxCode(null);
        log.debug("Consent response: " + consentResponse);
        return consentResponse;
    }

    private void notifyCardManager(ConsentResponse consentResponse) {
        try {
            log.info("Notifying Card Manager of this consent update");
            circuitBreakerManager.cardManagerClientUpdateConsent(consentResponse);
        } catch (Exception e) {
            log.error(e);
            throw new ConsentException(CALL_TO_CARD_MANAGER_FAILED);
        }
    }

    private Set<TkmCardService> updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentRequestEnum consent) {
        log.info("Updating services for card with hpan " + card.getHpan());
        List<TkmCardService> cardServices = services.stream().map(
                s -> TkmCardService.builder()
                        .card(card)
                        .service(s)
                        .consentType(consent)
                .build()
        ).collect(Collectors.toList());
        return new HashSet<>(cardServiceRepository.saveAll(cardServices));
    }

    private TkmCitizen updateOrCreateCitizen(String taxCode, String clientId, Consent consent) {
        TkmCitizen citizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (citizen == null) {
            log.info("No citizen found for taxCode " + taxCode + ", creating new one");
            citizen = TkmCitizen.builder()
                    .taxCode(taxCode)
                    .consentDate(Instant.now())
                    .consentType(consent.isPartial() ? Partial : toConsentEntityEnum(consent.getConsent()))
                    .consentClient(clientId)
            .build();
        } else {
            log.info("Citizen with taxCode " + taxCode + " found, updating consent");
            checkNotFromAllowToPartial(citizen.getConsentType(), consent);
            checkNotSameConsentType(citizen.getConsentType(), consent);
            citizen.setConsentUpdateDate(Instant.now());
            citizen.setConsentType(consent.isPartial() ? Partial : toConsentEntityEnum(consent.getConsent()));
            citizen.setConsentUpdateClient(clientId);
        }
        citizenRepository.save(citizen);
        return citizen;
    }

    private void checkNotFromAllowToPartial(ConsentEntityEnum citizenConsent, Consent requestedConsent) {
        if (Allow.equals(citizenConsent) && requestedConsent.isPartial()) {
            throw new ConsentException(CONSENT_TYPE_NOT_CONSISTENT);
        }
    }

    private void checkNotSameConsentType(ConsentEntityEnum citizenConsent, Consent requestedConsent) {
        if (!requestedConsent.isPartial() && citizenConsent.equals(toConsentEntityEnum(requestedConsent.getConsent()))) {
            throw new ConsentException(CONSENT_TYPE_ALREADY_SET);
        }
    }

    private TkmCard getOrCreateCard(TkmCitizen citizen, String hpan) {
        log.info("Searching for card with taxCode " + citizen.getTaxCode() + " and hpan " + hpan);
        TkmCard card = cardRepository.findByHpanAndCitizenAndDeletedFalse(hpan, citizen);
        if (card == null) {
            log.info("Card not found, creating new one");
            card = TkmCard.builder()
                    .hpan(hpan)
                    .citizen(citizen)
            .build();
            cardRepository.save(card);
        }
        return card;
    }

    @Override
    public ConsentResponse getConsent(String taxCode, String hpan, Set<ServiceEnum> services) {
        log.info("Get consent for taxCode " + taxCode + (StringUtils.isNotBlank(hpan) ? " and hpan " + hpan : ""));
        checkServicesAllowed(hpan, services);
        TkmCitizen tkmCitizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (tkmCitizen == null) {
            throw new ConsentDataNotFoundException(CITIZEN_NOT_FOUND);
        }
        log.info("Citizen found for taxCode " + taxCode + " with consent type " + tkmCitizen.getConsentType());
        ConsentResponse consentResponse = new ConsentResponse();
        consentResponse.setLastUpdateDate(tkmCitizen.getLastConsentUpdateDate());
        switch (tkmCitizen.getConsentType()) {
            case Deny:
                consentResponse.setConsent(Deny);
                break;
            case Allow:
                consentResponse.setConsent(Allow);
                break;
            case Partial:
                consentResponse.setConsent(Partial);
                handlePartialConsent(tkmCitizen, hpan, services, consentResponse);
                break;
        }
        log.debug("Consent response: " + consentResponse);
        return consentResponse;
    }

    private void handlePartialConsent(TkmCitizen tkmCitizen, String hpan, Set<ServiceEnum> services, ConsentResponse consentResponse) {
        String servicesLog = services != null ? services.stream().map(Enum::name).collect(Collectors.joining(", ")) : null;
        log.info("Handling partial consent for services " + servicesLog + (StringUtils.isNotBlank(hpan) ? " and hpan " + hpan : ""));
        List<CardServiceConsent> cardServiceConsents = new ArrayList<>();
        if (StringUtils.isNotBlank(hpan)) {
            TkmCard tkmCard = cardRepository.findByHpanAndCitizenAndDeletedFalse(hpan, tkmCitizen);
            if (tkmCard == null) {
                throw new ConsentDataNotFoundException(HPAN_NOT_FOUND);
            }
            log.info("Returning card with hpan " + hpan);
            cardServiceConsents.add(createServiceConsents(tkmCard, services));
        } else {
            log.info("Returning all cards for taxCode " + tkmCitizen.getTaxCode());
            for (TkmCard tkmCard : tkmCitizen.getCards()) {
                cardServiceConsents.add(createServiceConsents(tkmCard, services));
            }
        }
        consentResponse.setDetails(new HashSet<>(cardServiceConsents));
    }

    private CardServiceConsent createServiceConsents(TkmCard tkmCard, Set<ServiceEnum> servicesToSearch) {
        List<TkmCardService> tkmCardServices = tkmCard.getTkmCardServices();
        if (servicesToSearch != null) {
            tkmCardServices = tkmCardServices.stream().filter(s -> servicesToSearch.contains(s.getService().getName())).collect(Collectors.toList());
        }
        List<ServiceConsent> serviceConsentList = tkmCardServices.stream().map(s -> new ServiceConsent(s.getConsentType(), s.getService().getName())).collect(Collectors.toList());

        CardServiceConsent cardServiceConsent = new CardServiceConsent();
        cardServiceConsent.setHpan(tkmCard.getHpan());
        cardServiceConsent.setServiceConsents(new HashSet<>(serviceConsentList));
        return cardServiceConsent;
    }

    private void checkServicesAllowed(String hpan, Set<ServiceEnum> services) {
        if (StringUtils.isBlank(hpan) && services != null) {
            throw new ConsentException(ErrorCodeEnum.HPAN_AND_SERVICES_PARAMS_NOT_COHERENT);
        } else if (services != null && services.isEmpty()) {
            throw new ConsentException(ErrorCodeEnum.EMPTY_CONSENT_SERVICE);
        }
    }

    private void checkServicesNotPresentWithGlobalConsent(Set<ServiceEnum> services) {
        if (CollectionUtils.isNotEmpty(services)) {
            throw new ConsentException(ErrorCodeEnum.HPAN_AND_SERVICES_PARAMS_NOT_COHERENT);
        }
    }
    
    @Override
    public void deleteUser(String taxCode, String clientId) throws ConsentException {
        TkmCitizen citizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (citizen == null) {
            throw new ConsentDataNotFoundException(CITIZEN_NOT_FOUND);
        }
        ConsentResponse consentResponse = new ConsentResponse(Deny, taxCode, citizen.getLastConsentUpdateDate(), null);
        notifyCardManager(consentResponse);
        citizen.setConsentUpdateClient(clientId);
        citizen.setDeleted(true);
        citizen.getCards().forEach(c -> c.setDeleted(true));
        citizenRepository.save(citizen);
    }

}
