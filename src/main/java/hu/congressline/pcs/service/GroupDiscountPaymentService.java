package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.GroupDiscountPayment;
import hu.congressline.pcs.repository.GroupDiscountPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupDiscountPaymentService {

    private final GroupDiscountPaymentRepository groupDiscountPaymentRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public GroupDiscountPayment save(GroupDiscountPayment groupDiscountPayment) {
        log.debug("Request to save GroupDiscountPayment : {}", groupDiscountPayment);
        return groupDiscountPaymentRepository.save(groupDiscountPayment);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<GroupDiscountPayment> findByCongressId(Long id) {
        log.debug("Request to get all GroupDiscountPayment by congress id: {}", id);
        return groupDiscountPaymentRepository.findByCongressIdOrderByDateOfPaymentDesc(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<GroupDiscountPayment> findByPayingGroupId(Long id) {
        log.debug("Request to get all GroupDiscountPayment by paying group id: {}", id);
        return groupDiscountPaymentRepository.findByPayingGroupIdOrderByDateOfPaymentDesc(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<GroupDiscountPayment> findById(Long id) {
        log.debug("Request to find GroupDiscountPayment : {}", id);
        return groupDiscountPaymentRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public GroupDiscountPayment getById(Long id) {
        log.debug("Request to get GroupDiscountPayment : {}", id);
        return groupDiscountPaymentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Group discount payment not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete GroupDiscountPayment : {}", id);
        groupDiscountPaymentRepository.deleteById(id);
    }

}
