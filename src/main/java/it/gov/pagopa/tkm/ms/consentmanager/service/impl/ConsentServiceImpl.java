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

import java.util.*;
import java.util.stream.*;

import static it.gov.pagopa.tkm.ms.consentmanager.constant.ConsentEnum.*;
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
    public ConsentResponse postConsent(String taxCode, ClientEnum clientId, Consent consent) throws ConsentException {
        checkConsentTypeNotPartial(consent.getConsent());
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

    private void checkConsentTypeNotPartial(ConsentEnum consent) {
        if (PARTIAL.equals(consent)) {
            throw new ConsentException(CONSENT_TYPE_NOT_PERMITTED);
        }
    }

    private void updateOrCreateCardServices(List<TkmService> services, TkmCard card, ConsentEnum consent) {
        List<TkmCardService> cardServices = cardServiceRepository.findByServiceInAndCard(services, card);
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
                    .setConsentType(consent.isPartial() ? PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        } else {
            checkNotAllowToPartial(user.getConsentType(), consent);
            user
                    .setConsentUpdateDate(new Date())
                    .setConsentType(consent.isPartial() ? PARTIAL : consent.getConsent())
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
        return user;
    }

    private void checkNotAllowToPartial(ConsentEnum userConsent, Consent consent) {
        if (ALLOW.equals(userConsent) && ALLOW.equals(consent.getConsent()) && consent.isPartial()) {
            throw new ConsentException(CONSENT_TYPE_NOT_CONSISTENT);
        }
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

}
