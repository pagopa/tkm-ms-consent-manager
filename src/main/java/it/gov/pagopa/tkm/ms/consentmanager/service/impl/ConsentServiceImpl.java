package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

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
import it.gov.pagopa.tkm.ms.consentmanager.service.ConsentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
        ConsentResponse consentResponse = new ConsentResponse();
        if (consent.isPartial()) {
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
            consentResponse.setCardServiceConsents(new HashSet<>(Collections.singletonList(cardServiceConsents)));
        } else {
            List<TkmService> allServices = serviceRepository.findAll();
            citizen.getCards().forEach(c -> updateOrCreateCardServices(allServices, c, consent.getConsent()));
            consentResponse.setConsent(ConsentEntityEnum.toConsentEntityEnum(consent.getConsent()));
        }
        return consentResponse;
    }

    private Set<TkmCardService> updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentRequestEnum consent) {
        List<TkmCardService> cardServices = services.stream().map(
                s -> new TkmCardService()
                        .setCard(card)
                        .setService(s)
                        .setConsentType(consent)
        ).collect(Collectors.toList());
        cardServiceRepository.saveAll(cardServices);
        return cardServiceRepository.findByCard(card);
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
        checkLookingForNotNull(tkmCitizen == null, USER_NOT_FOUND);
        ConsentResponse consentResponse = new ConsentResponse();
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
            default:
                throw new ConsentException(INVALID_CONSENT_TYPE);
        }
        return consentResponse;
    }

    private void checkLookingForNotNull(boolean b, ErrorCodeEnum userNotFound) {
        if (b) {
            throw new ConsentDataNotFoundException(userNotFound);
        }
    }

    private void handlePartialConsent(TkmCitizen tkmCitizen, String hpan, Set<ServiceEnum> services, ConsentResponse consentResponse) {
        List<CardServiceConsent> cardServiceConsents = new ArrayList<>();
        if (hpan != null) {
            TkmCard tkmCard = cardRepository.findByHpanAndDeletedFalse(hpan);
            checkLookingForNotNull(tkmCard == null, HPAN_NOT_FOUND);
            cardServiceConsents.add(createServiceConsents(tkmCard, services));
        } else {
            for (TkmCard tkmCard : tkmCitizen.getCards()) {
                cardServiceConsents.add(createServiceConsents(tkmCard, services));
            }
        }
        consentResponse.setCardServiceConsents(new HashSet<>(cardServiceConsents));
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

    private void checkHpanAndServicesBothPresentOrBothAbsent(String hpan, Set<ServiceEnum> services) {
        if ((StringUtils.isNotBlank(hpan) && CollectionUtils.isEmpty(services)) || (StringUtils.isBlank(hpan) && CollectionUtils.isNotEmpty(services))) {
            throw new ConsentException(ErrorCodeEnum.HPAN_AND_SERVICES_PARAMS_NOT_COHERENT);
        }
    }

}