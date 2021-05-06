package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.exception.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import it.gov.pagopa.tkm.ms.consentmanager.utils.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import lombok.extern.log4j.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum.*;
import static it.gov.pagopa.tkm.ms.consentmanager.constant.ErrorCodeEnum.*;

@Service
@Log4j2
public class ConsentServiceImpl implements ConsentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardServiceRepository cardServiceRepository;

    @Override
    public ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent) throws ConsentException {
        TkmUser user = updateOrCreateUser(taxCode, clientId, consent);
        if (consent.isPartial()) {
            TkmCard card = getOrCreateCard(user, consent.getHpan());
            List<TkmService> services = CollectionUtils.isEmpty(consent.getServices()) ?
                    serviceRepository.findAll() :
                    serviceRepository.findByNameIn(consent.getServices());
            updateOrCreateCardServices(services, card, consent.getConsent());
        }
        return new ConsentResponse(consent);
    }

    private void updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentEnum consent) {
        List<TkmCardService> cardServicesList = cardServiceRepository.findByServiceInAndCard(services, card);
        List<TkmCardService> cardServices = CollectionUtils.isEmpty(cardServicesList) ? new ArrayList<>() : new ArrayList<>(cardServicesList);
        List<TkmService> existingServicesOnCard = cardServices.stream().map(TkmCardService::getService).collect(Collectors.toList());
        cardServices.addAll(services.stream().filter(s -> !existingServicesOnCard.contains(s)).map(
                s -> new TkmCardService()
                        .setCard(card)
                        .setService(s)
        ).collect(Collectors.toList()));
        cardServices.forEach(s -> s.setConsentType(consent));
        cardServiceRepository.saveAll(cardServices);
    }

    private TkmUser updateOrCreateUser(String taxCode, ClientEnum clientId, Consent consent) {
        TkmUser user = userRepository.findByTaxCode(taxCode);
        if (user == null) {
            user = new TkmUser()
                    .setTaxCode(taxCode)
                    .setConsentDate(DateUtils.now())
                    .setConsentType(consent.isPartial() ? PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        } else {
            checkNotFromAllowToPartial(user.getConsentType(), consent);
            user
                    .setConsentUpdateDate(DateUtils.now())
                    .setConsentType(consent.isPartial() ? PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
        return user;
    }

    private void checkNotFromAllowToPartial(ConsentEnum userConsent, Consent requestedConsent) {
        if (ALLOW.equals(userConsent) && ALLOW.equals(requestedConsent.getConsent()) && requestedConsent.isPartial()) {
            throw new ConsentException(CONSENT_TYPE_NOT_CONSISTENT);
        }
    }

    private TkmCard getOrCreateCard(TkmUser user, String hpan) {
        TkmCard card = cardRepository.findByHpan(hpan);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setUser(user);
            cardRepository.save(card);
        }
        return card;
    }



    public GetConsentResponse getGetConsentResponse(String taxCode, String hpan, String[] services) {

        TkmUser tkmUser = userRepository.findByTaxCode(taxCode);
        if (tkmUser == null)
            throw new ConsentDataNotFoundException(USER_NOT_FOUND);

        GetConsentResponse getConsentResponse = new GetConsentResponse();

        if (hpan != null) {
            TkmCard tkmCard = cardRepository.findByHpan(hpan);
            if (tkmCard == null) throw new ConsentDataNotFoundException(HPAN_NOT_FOUND);

            List<ConsentResponse> details = null;
            List<TkmService> tkmServices = getRequestedTkmServices(services);
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
                    List<TkmService> tkmServices = getRequestedTkmServices(services);
                    List<ConsentResponse> details=null;

                    List<TkmCard> tkmUserCards = cardRepository.findByUser(tkmUser);
                    for (TkmCard tkmCard : tkmUserCards) {
                        details=addDetail(tkmCard, tkmServices, details);
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
        List<TkmCardService> cardServices = cardServiceRepository.findByServiceInAndCard(tkmServices, tkmCard);
        if (CollectionUtils.isEmpty(cardServices)) return details;

        details=details==null?new ArrayList<>():details;

        List<TkmCardService> serviceByConsent;

        serviceByConsent = cardServices.stream().filter(b-> b.getConsentType().equals(ConsentEnum.ALLOW))
                    .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serviceByConsent)) return details;

        Consent consent = new Consent();
        createConsentFromTkmCard(consent, ConsentEnum.ALLOW, serviceByConsent, tkmCard.getHpan());
        details.add(new ConsentResponse(consent));
        return details;
    }


    private Consent createConsentFromTkmCard(Consent consent, ConsentEnum consentEnum, List<TkmCardService> services, String hpan) {

        Set<ServiceEnum> serviceEnumsSet =  services.stream().map(s-> s.getService().getName()).collect(Collectors.toSet());

        consent.setConsent(consentEnum);
        consent.setHpan(hpan);
        consent.setServices(serviceEnumsSet);

        return consent;
  }


}
