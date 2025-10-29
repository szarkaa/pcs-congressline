package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import hu.congressline.pcs.repository.ChargeableItemInvoiceHistoryRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.RegistrationTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegistrationRegistrationTypeService {

    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RegistrationTypeRepository rtRepository;
    private final RegistrationRepository registrationRepository;
    private final ChargeableItemInvoiceHistoryRepository ciihRepository;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public RegistrationRegistrationType save(RegistrationRegistrationType registrationRegistrationType) {
        log.debug("Request to save RegistrationRegistrationType : {}", registrationRegistrationType);
        return rrtRepository.save(registrationRegistrationType);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void save(List<RegistrationRegistrationType> rrtList) {
        rrtRepository.saveAll(rrtList);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegistrationRegistrationType> findAllByRegistrationId(Long id) {
        log.debug("Request to get all RegistrationRegistrationType by registration id: {}", id);
        return rrtRepository.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegistrationRegistrationType> findAllByCongressId(Long id) {
        log.debug("Request to get all RegistrationRegistrationType by congress id: {}", id);
        return rrtRepository.findAllByRegistrationCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RegistrationRegistrationType findOne(Long id) {
        log.debug("Request to get RegistrationRegistrationType : {}", id);
        return rrtRepository.findById(id).orElse(null);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RegistrationRegistrationType calculateRegFeeByRegistrationTypeId(Long registrationId, Long registrationTypeId) {
        log.debug("Request to get calculate reg fee by RegistrationType : {}", registrationTypeId);
        Registration reg = registrationRepository.findById(registrationId).orElse(null);
        RegistrationType rt = rtRepository.findById(registrationTypeId).orElse(null);
        RegistrationRegistrationType rrt = new RegistrationRegistrationType();
        rrt.setRegistrationType(rt);
        rrt.setRegistration(reg);
        rrt.setCreatedDate(LocalDate.now());
        setRegFee(rrt);
        return rrt;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete RegistrationRegistrationType : {}", id);
        // if last invoice this item is on is a storno then it is deleteable
        List<ChargeableItemInvoiceHistory> ciihList = ciihRepository.findAllByChargeableItemIdOrderByIdDesc(id);
        final ChargeableItemInvoiceHistory lastHistoryItem = ciihList.stream().findFirst().orElse(null);
        if (lastHistoryItem != null && lastHistoryItem.getInvoice().getStorno()) {
            ciihRepository.deleteAll(ciihList);
        }

        // if last group invoice this item is on is a storno then it is deleteable
        List<GroupDiscountInvoiceHistory> gdihList = gdihRepository.findAllByChargeableItemIdOrderByIdDesc(id);
        final GroupDiscountInvoiceHistory lastGroupHistoryItem = gdihList.stream().findFirst().orElse(null);
        if (lastGroupHistoryItem != null && lastGroupHistoryItem.getInvoice().getStorno()) {
            gdihRepository.deleteAll(gdihList);
        }

        rrtRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void setRegFee(RegistrationRegistrationType rrt) {
        final RegistrationType registrationType = rrt.getRegistrationType();
        rrt.setRegFee(calculateRegFee(registrationType, rrt.getRegistration().getDateOfApp()));
        rrt.setCurrency(registrationType.getCurrency().getCurrency());

        if (!RegistrationTypeType.ACCOMPANYING_FEE.equals(registrationType.getRegistrationType())) {
            rrt.setAccPeople(1);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal calculateRegFee(RegistrationType registrationType, LocalDate createdDate) {
        if (registrationType.getSecondDeadline() != null && createdDate.isAfter(registrationType.getSecondDeadline())) {
            return registrationType.getThirdRegFee();
        } else if (registrationType.getFirstDeadline() != null && createdDate.isAfter(registrationType.getFirstDeadline())) {
            return registrationType.getSecondRegFee();
        } else {
            return registrationType.getFirstRegFee();
        }
    }

}
