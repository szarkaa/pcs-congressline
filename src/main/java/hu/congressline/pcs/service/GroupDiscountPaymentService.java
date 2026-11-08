package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.GroupDiscountPayment;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.GroupDiscountPaymentRepository;
import hu.congressline.pcs.repository.PayingGroupRepository;
import hu.congressline.pcs.service.dto.GroupDiscountPaymentDTO;
import hu.congressline.pcs.web.rest.vm.GroupDiscountPaymentVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupDiscountPaymentService {

    private final GroupDiscountPaymentRepository repository;
    private final PayingGroupRepository payingGroupRepository;
    private final CurrencyRepository currencyRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public GroupDiscountPayment save(GroupDiscountPayment groupDiscountPayment) {
        log.debug("Request to save GroupDiscountPayment : {}", groupDiscountPayment);
        return repository.save(groupDiscountPayment);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public GroupDiscountPayment save(GroupDiscountPaymentVM viewModel) {
        log.debug("Request to save GroupDiscountPaymentVM : {}", viewModel);
        GroupDiscountPayment groupDiscountPayment = viewModel.getId() != null ? getById(viewModel.getId()) : new GroupDiscountPayment();
        groupDiscountPayment.update(viewModel);
        groupDiscountPayment.setPayingGroup(viewModel.getPayingGroupId() != null ? payingGroupRepository.findById(viewModel.getPayingGroupId()).orElse(null) : null);
        groupDiscountPayment.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (groupDiscountPayment.getCongress() == null) {
            groupDiscountPayment.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(groupDiscountPayment);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<GroupDiscountPaymentDTO> findByCongressId(Long id) {
        log.debug("Request to get all GroupDiscountPayment by congress id: {}", id);
        return repository.findByCongressIdOrderByDateOfPaymentDesc(id).stream().map(GroupDiscountPaymentDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<GroupDiscountPaymentDTO> findByPayingGroupId(Long id) {
        log.debug("Request to get all GroupDiscountPayment by paying group id: {}", id);
        return repository.findByPayingGroupIdOrderByDateOfPaymentDesc(id).stream().map(GroupDiscountPaymentDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<GroupDiscountPaymentDTO> findById(Long id) {
        log.debug("Request to find GroupDiscountPayment : {}", id);
        return id != null ? repository.findById(id).map(GroupDiscountPaymentDTO::new) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public GroupDiscountPayment getById(Long id) {
        log.debug("Request to get GroupDiscountPayment : {}", id);
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Group discount payment not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete GroupDiscountPayment : {}", id);
        repository.deleteById(id);
    }

}
