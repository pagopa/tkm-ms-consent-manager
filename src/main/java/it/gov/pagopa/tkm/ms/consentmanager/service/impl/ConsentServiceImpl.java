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
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
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

    public GetConsentResponse getConsentV3(String taxCode, String hpan, List<ServiceEnum> services) {

        TkmCitizen tkmCitizen = citizenRepository.findByTaxCodeAndDeletedFalse(taxCode);
        if (tkmCitizen == null)
            throw new ConsentDataNotFoundException(USER_NOT_FOUND);

        if (hpan != null) {
            TkmCard tkmCard = cardRepository.findByHpan(hpan);
            if (tkmCard == null) throw new ConsentDataNotFoundException(HPAN_NOT_FOUND);
        }

        GetConsentResponse getConsentResponse = new GetConsentResponse();

        switch (tkmCitizen.getConsentType()) {
            case Deny:
                getConsentResponse.setConsent(Deny);
                break;
            case Allow:
                getConsentResponse.setConsent(Allow);
                break;
            case Partial:

                List<TkmCardService> tkmCardServices = cardServiceRepository.findTkmCardServices(tkmCitizen, hpan, services);

                if (!CollectionUtils.isEmpty(tkmCardServices)) {
                    Map<TkmCard, ConsentResponse> cardsResponseMap = new HashMap<>();

                    List<ConsentResponse> details = new ArrayList<>();

                    for (TkmCardService tkmCs : tkmCardServices) {
                        if (cardsResponseMap.get(tkmCs.getCard()) == null) {
                            ConsentResponse cr = new ConsentResponse();
                            cr.setHpan(tkmCs.getCard().getHpan());
                            cr.setConsent(ConsentRequestEnum.Allow);
                            cr.setServices(new HashSet<>());
                            cardsResponseMap.put(tkmCs.getCard(), cr);
                        }

                        cardsResponseMap.get(tkmCs.getCard()).getServices().add(tkmCs.getService().getName());
                    }

                    cardsResponseMap.keySet().stream().forEach(c -> details.add(cardsResponseMap.get(c)));

                    if (!CollectionUtils.isEmpty(details)) {
                        getConsentResponse.setDetails(details);
                    }
                }

                getConsentResponse.setConsent(Partial);
                break;

            default:
                break;
        }

        return getConsentResponse;
    }
}


