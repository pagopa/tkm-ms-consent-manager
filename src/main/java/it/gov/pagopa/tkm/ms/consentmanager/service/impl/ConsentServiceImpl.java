package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmUser;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCard;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmService;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.TkmCardService;

import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

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

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
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
                        .setConsentType(toConsentEntityEnum(consent))
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
        TkmCard card = cardRepository.findByHpanAndCitizen(hpan, citizen);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setCitizen(citizen);
            cardRepository.save(card);
        }
        return card;
    }


    public GetConsentResponse getConsent(String taxCode, String hpan, String[] services) {

        TkmUser tkmUser = userRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (tkmUser == null)
            throw new ConsentDataNotFoundException(USER_NOT_FOUND);

        GetConsentResponse getConsentResponse = new GetConsentResponse();
        List<TkmService> tkmServices = getRequestedTkmServices(services);

        if (hpan != null) {
            TkmCard tkmCard = cardRepository.findByHpan(hpan);
            if (tkmCard == null) throw new ConsentDataNotFoundException(HPAN_NOT_FOUND);

            List<ConsentResponse> details = null;
            details=addDetail(tkmCard, tkmServices, details);

            if (!CollectionUtils.isEmpty(details)){
                getConsentResponse.setDetails(details);
            }
            getConsentResponse.setConsent(tkmUser.getConsentType());

        } else {
            switch (tkmUser.getConsentType()) {
                case DENY:
                    getConsentResponse.setConsent(DENY);
                    break;
                case ALLOW:
                    getConsentResponse.setConsent(ALLOW);
                    break;
                case PARTIAL:
                    List<ConsentResponse> details=null;

                    List<TkmCard> tkmUserCards = cardRepository.findByUser(tkmUser);
                    for (TkmCard tkmCard : tkmUserCards) {
                        details=addDetail(tkmCard, null, details);
                    }

                    if (!CollectionUtils.isEmpty(details)) {
                        getConsentResponse.setDetails(details);
                    }
                    getConsentResponse.setConsent(PARTIAL);

                    break;

                default:
                    break;
            }
        }

        return getConsentResponse;

    }


    private List<TkmService> getRequestedTkmServices(String[] services){
        Set<ServiceEnum> servicesEnums;

        try {
            servicesEnums = Arrays.asList(Optional.ofNullable(services)
                    .orElse(new String[0])).stream()
                    .map(ServiceEnum::valueOf).collect(Collectors.toSet());
        } catch (IllegalArgumentException iae) {
            throw new ConsentException(ILLEGAL_SERVICE_VALUE);
        }

        return CollectionUtils.isEmpty(servicesEnums) ?
                serviceRepository.findAll() :
                serviceRepository.findByNameIn(servicesEnums);
    }


    private List<ConsentResponse>  addDetail (TkmCard tkmCard, List<TkmService> tkmServices, List<ConsentResponse> details){
        List<TkmCardService> cardServices = tkmServices==null||tkmServices.isEmpty()?
                cardServiceRepository.findByCard(tkmCard):
                cardServiceRepository.findByServiceInAndCard(tkmServices, tkmCard);

        if (CollectionUtils.isEmpty(cardServices)) return details;

        details=details==null?new ArrayList<>():details;

        List<TkmCardService> serviceByConsent;

        serviceByConsent = cardServices.stream().filter(b-> b.getConsentType().equals(ConsentEntityEnum.ALLOW))
                    .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serviceByConsent)) return details;

        Consent consent = new Consent();
        createConsentFromTkmCard(consent, ConsentRequestEnum.ALLOW, serviceByConsent, tkmCard.getHpan());
        details.add(new ConsentResponse(consent));

        return details;
    }


    private Consent createConsentFromTkmCard(Consent consent, ConsentRequestEnum consentEnum, List<TkmCardService> services, String hpan) {

        Set<ServiceEnum> serviceEnumsSet =  services.stream().map(s-> s.getService().getName()).collect(Collectors.toSet());

        consent.setConsent(consentEnum);
        consent.setHpan(hpan);
        consent.setServices(serviceEnumsSet);

        return consent;
  }


}
