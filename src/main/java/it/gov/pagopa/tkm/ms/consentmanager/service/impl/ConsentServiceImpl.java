package it.gov.pagopa.tkm.ms.consentmanager.service.impl;

import it.gov.pagopa.tkm.ms.consentmanager.constant.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.entity.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.request.*;
import it.gov.pagopa.tkm.ms.consentmanager.model.response.*;
import it.gov.pagopa.tkm.ms.consentmanager.repository.*;
import it.gov.pagopa.tkm.ms.consentmanager.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;

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
        updateOrCreateUser(taxCode, clientId, consent);
        if (ConsentEnum.PARTIAL.equals(consent.getConsent())) {
            TkmCard card = cardRepository.findByHpan(consent.getHpan());
            List<TkmService> services = serviceRepository.findAllByNameIn(consent.getServices());
            List<TkmCardService> cardServices = cardServiceRepository.findAllByServiceInAndCardIn(services, Collections.singletonList(card));
            cardServices.forEach(s -> s.setConsentType(consent.getConsent()));
        }
        return new ConsentResponse(consent);
    }

    private void updateOrCreateUser(String taxCode, ClientEnum clientId, Consent consent) {
        TkmUser user = userRepository.findByTaxCode(taxCode);
        if (user == null) {
            user = new TkmUser()
                    .setTaxCode(taxCode)
                    .setConsentDate(new Date())
                    .setConsentType(consent.getConsent())
                    .setConsentLastClient(clientId);
        } else {
            user
                    .setConsentUpdateDate(new Date())
                    .setConsentType(consent.getConsent())
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
    }

    private TkmCard getOrCreateCard(String hpan, TkmUser user, Consent consent) {
        TkmCard card = cardRepository.findByHpan(hpan);
        if (card == null) {
            card = new TkmCard()
                    .setHpan(hpan)
                    .setUser(user);
        }
        return card;
    }

}
