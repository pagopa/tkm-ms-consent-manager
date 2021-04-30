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
        ConsentEnum consentType = consent.getConsent();
        TkmUser user = updateOrCreateUser(taxCode, clientId, consent);
        if (ConsentEnum.PARTIAL.equals(consentType)) {
            TkmCard card = getOrCreateCard(user, consent);
            Set<TkmService> services = serviceRepository.findByNameIn(consent.getServices());
            updateOrCreateCardServices(services, card, consent.getConsent());
        }
        return new ConsentResponse(consent);
    }

    private void updateOrCreateCardServices(Set<TkmService> services, TkmCard card, ConsentEnum consent) {
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
                    .setConsentType(consent.getConsent())
                    .setConsentLastClient(clientId);
        } else {
            user
                    .setConsentUpdateDate(new Date())
                    .setConsentType(consent.getConsent())
                    .setConsentLastClient(clientId);
        }
        userRepository.save(user);
        return user;
    }

    private TkmCard getOrCreateCard(TkmUser user, Consent consent) {
        String hpan = consent.getHpan();
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
