package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCitizen;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCard;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmService;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;

import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEntityEnum.*;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum.*;

@Service
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
    private EntityManager entityManager;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
        checkHpanAndServicesBothPresentOrBothAbsent(consent.getHpan(), consent.getServices());
        TkmCitizen citizen = updateOrCreateCitizen(taxCode, clientId, consent);
        if (consent.isPartial()) {
            TkmCard card = getOrCreateCard(citizen, consent.getHpan());
            List<TkmService> services = CollectionUtils.isEmpty(consent.getServices()) ?
                    serviceRepository.findAll() :
                    serviceRepository.findByNameIn(consent.getServices());
            updateOrCreateCardServices(services, card, consent.getConsent());
        } else {
            List<TkmService> allServices = serviceRepository.findAll();
            citizen.getCards().forEach(c -> updateOrCreateCardServices(allServices, c, consent.getConsent()));
        }
        return new ConsentResponse(consent);
    }

    private void updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentRequestEnum consent) {
        List<TkmCardService> cardServices = services.stream().map(
                s -> new TkmCardService()
                        .setCard(card)
                        .setService(s)
                        .setConsentType(consent)
        ).collect(Collectors.toList());
        cardServiceRepository.saveAll(cardServices);
    }

    private TkmCitizen updateOrCreateCitizen(String taxCode, String clientId, Consent consent) {
        TkmCitizen citizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (citizen == null) {
            citizen = new TkmCitizen()
                    .setTaxCode(taxCode)
                    .setConsentDate(Instant.now())
                    .setConsentType(consent.isPartial() ? Partial : toConsentEntityEnum(consent.getConsent()))
                    .setConsentClient(clientId);
        } else {
            checkNotFromAllowToPartial(citizen.getConsentType(), consent);
            checkNotSameConsentType(citizen.getConsentType(), consent);
            citizen
                    .setConsentUpdateDate(Instant.now())
                    .setConsentType(consent.isPartial() ? Partial : toConsentEntityEnum(consent.getConsent()))
                    .setConsentUpdateClient(clientId);
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
        TkmCard card = cardRepository.findByHpanAndCitizenAndDeletedFalse(hpan, citizen);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setCitizen(citizen);
            cardRepository.save(card);
        }
        return card;
    }

    @Override
    public ConsentResponse getConsent(String taxCode, String hpan, Set<ServiceEnum> services) {
        checkHpanAndServicesBothPresentOrBothAbsent(hpan, services);
        TkmCitizen tkmCitizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (tkmCitizen == null) {
            throw new ConsentDataNotFoundException(USER_NOT_FOUND);
        }
        ConsentResponse consentResponse = new ConsentResponse();
        switch (tkmCitizen.getConsentType()) {
            case Deny:
                consentResponse.setConsent(Deny);
                break;
            case Allow:
                consentResponse.setConsent(Allow);
                break;
            case Partial:
                handlePartialConsent(tkmCitizen, hpan, services, consentResponse);
                break;
            default:
                throw new ConsentException(INVALID_CONSENT_TYPE);
        }
        return consentResponse;
    }

    private void handlePartialConsent(TkmCitizen tkmCitizen, String hpan, Set<ServiceEnum> services, ConsentResponse consentResponse) {
        consentResponse.setConsent(Partial);
        List<ServiceConsent> serviceConsents = new ArrayList<>();
        if (hpan != null) {
            TkmCard tkmCard = cardRepository.findByHpanAndDeletedFalse(hpan);
            if (tkmCard == null) {
                throw new ConsentDataNotFoundException(HPAN_NOT_FOUND);
            }
            consentResponse.setHpan(hpan);
            serviceConsents = createServiceConsents(tkmCard, services);
        } else {
            for (TkmCard tkmCard : tkmCitizen.getCards()) {
                serviceConsents.addAll(createServiceConsents(tkmCard, services));
            }
        }
        consentResponse.setServiceConsents(serviceConsents);
    }

    private List<ServiceConsent> createServiceConsents(TkmCard tkmCard, Set<ServiceEnum> services) {
        List<TkmService> tkmServices = serviceRepository.findByNameIn(services);
        Set<TkmCardService> tkmCardServices = cardServiceRepository.findByCardAndServiceIn(tkmCard, tkmServices);
        return tkmCardServices.stream().map(cs ->
                new ServiceConsent(
                        cs.getConsentType(),
                        cs.getService().getName()
                )
        ).collect(Collectors.toList());
    }

    private void checkHpanAndServicesBothPresentOrBothAbsent(String hpan, Set<ServiceEnum> services) {
        if ((StringUtils.isNotBlank(hpan) && CollectionUtils.isEmpty(services)) || (StringUtils.isBlank(hpan) && CollectionUtils.isNotEmpty(services))) {
            throw new ConsentException(ErrorCodeEnum.HPAN_AND_SERVICES_PARAMS_NOT_COHERENT);
        }
    }

}