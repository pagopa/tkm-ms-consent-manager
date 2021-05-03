package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import java.util.*;
import java.util.stream.*;

@Service
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
    public ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent) {
        TkmUser user = updateOrCreateUser(taxCode, clientId, consent);
        if (consent.getHpan() != null) {
            TkmCard card = getOrCreateCard(user, consent.getHpan());
            List<TkmService> services = CollectionUtils.isEmpty(consent.getServices()) ?
                    serviceRepository.findAll() :
                    serviceRepository.findByNameIn(consent.getServices());
            updateOrCreateCardServices(services, card, consent.getConsent());
        }
        return new ConsentResponse(consent);
    }

    private void updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentEnum consent) {
        List<TkmCardService> cardServices = cardServiceRepository.findByServiceInAndCardIn(services, Collections.singletonList(card));

        if (CollectionUtils.isEmpty(cardServices)) {
            cardServices = services.stream().map(
                    s -> new TkmCardService()
                        .setCard(card)
                        .setService(s)
                        .setConsentType(consent)
            ).collect(Collectors.toList());
        } else {
            cardServices.forEach(s -> s.setConsentType(consent));
        }
        cardServiceRepository.saveAll(cardServices);
    }

    private TkmUser updateOrCreateUser(String taxCode, ClientEnum clientId, Consent consent) {
        TkmUser user = userRepository.findByTaxCode(taxCode);
        if (user == null) {
            user = new TkmUser()
                    .setTaxCode(taxCode)
                    .setConsentDate(new Date())
                    .setConsentType(consent.getHpan() != null ? ConsentEnum.PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        } else {
            user
                    .setConsentUpdateDate(new Date())
                    .setConsentType(consent.getHpan() != null ? ConsentEnum.PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
        return user;
    }

    private TkmCard getOrCreateCard(TkmUser user, String hpan) {
        TkmCard card = cardRepository.findByHpan(hpan);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setUser(user);
        }
        cardRepository.save(card);
        return card;
    }

    public GetConsentResponse getGetConsentResponse(String taxCode, String hpan, List<String> services){

        Set<ServiceEnum> servicesEnums= Optional.ofNullable(services)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(s->ServiceEnum.valueOf(s)).collect(Collectors.toSet());

        List<TkmService> tkmServices = CollectionUtils.isEmpty(servicesEnums) ?
                serviceRepository.findAll() :
                serviceRepository.findByNameIn(servicesEnums);

        GetConsentResponse getConsentResponse = new GetConsentResponse();
        List<ConsentResponse> details = new ArrayList<>();

        TkmUser tkmUser = userRepository.findByTaxCode(taxCode);

        if (hpan!=null) {
            TkmCard tkmCard = cardRepository.findByHpan(hpan);
            getConsentResponse.setConsent(tkmUser.getConsentType());
            addDetail(tkmCard, tkmServices, details);
        } else {
            switch (tkmUser.getConsentType()){
                case DENY:  //Ritorna risposta con DENY
                    getConsentResponse.setConsent(ConsentEnum.DENY);
                    break;
                case ALLOW:  //Ritorna risposta con ALLOW
                    getConsentResponse.setConsent(ConsentEnum.ALLOW);
                    break;
                case PARTIAL:
                    getConsentResponse.setConsent(ConsentEnum.PARTIAL);
                    List<TkmCard> tkmUserCards = cardRepository.findByUser(tkmUser);
                     for (TkmCard tkmCard : tkmUserCards) {
                         addDetail(tkmCard, tkmServices, details);
                     }
                  break;
            }
        }

        if (!CollectionUtils.isEmpty(details)){
            getConsentResponse.setDetails(details);
        }

    return getConsentResponse;

    }


    private List<ConsentResponse> addDetail (TkmCard tkmCard, List<TkmService> tkmServices, List<ConsentResponse> details){
        List<TkmCardService> cardServices = cardServiceRepository.findByServiceInAndCardIn(tkmServices, Collections.singletonList(tkmCard));
        List<TkmCardService> serviceByConsent;

        serviceByConsent = cardServices.stream().filter(b-> b.getConsentType().equals(ConsentEnum.ALLOW))
                    .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(serviceByConsent)) return null;

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
