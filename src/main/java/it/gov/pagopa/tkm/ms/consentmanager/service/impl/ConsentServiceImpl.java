package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
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
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardServiceRepository cardServiceRepository;

    @Override
    public ConsentResponse postConsent(String taxCode, String clientId, Consent consent) throws ConsentException {
        TkmUser user = updateOrCreateUser(taxCode, clientId, consent);
        if (consent.isPartial()) {
            TkmCard card = getOrCreateCard(user, consent.getHpan());
            List<TkmService> services = CollectionUtils.isEmpty(consent.getServices()) ?
                    serviceRepository.findAll() :
                    serviceRepository.findByNameIn(consent.getServices());
            updateOrCreateCardServices(services, card, consent.getConsent());
        } else {
            List<TkmService> allServices = serviceRepository.findAll();
            user.getCards().forEach(c -> updateOrCreateCardServices(allServices, c, consent.getConsent()));
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

    private TkmUser updateOrCreateUser(String taxCode, String clientId, Consent consent) {
        TkmUser user = userRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (user == null) {
            user = new TkmUser()
                    .setTaxCode(taxCode)
                    .setConsentDate(Instant.now())
                    .setConsentType(consent.isPartial() ?
                            PARTIAL : toConsentEntityEnum(consent.getConsent()))
                    .setConsentLastClient(clientId)
                    .setDeleted(false);
        } else {
            checkNotFromAllowToPartial(user.getConsentType(), consent);
            checkNotSameConsentType(user.getConsentType(), consent);
            user
                    .setConsentUpdateDate(Instant.now())
                    .setConsentType(consent.isPartial() ?
                            PARTIAL : toConsentEntityEnum(consent.getConsent()))
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
        return user;
    }

    private void checkNotFromAllowToPartial(ConsentEntityEnum userConsent, Consent requestedConsent) {
        if (ALLOW.equals(userConsent) && requestedConsent.isPartial()) {
            throw new ConsentException(CONSENT_TYPE_NOT_CONSISTENT);
        }
    }

    private void checkNotSameConsentType(ConsentEntityEnum userConsent, Consent requestedConsent) {
        if (!requestedConsent.isPartial() && userConsent.equals(toConsentEntityEnum(requestedConsent.getConsent()))) {
            throw new ConsentException(CONSENT_TYPE_ALREADY_SET);
        }
    }

    private TkmCard getOrCreateCard(TkmUser user, String hpan) {
        TkmCard card = cardRepository.findByHpanAndUser(hpan, user);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setUser(user)
                    .setDeleted(false);
            cardRepository.save(card);
        }
        return card;
    }

}
