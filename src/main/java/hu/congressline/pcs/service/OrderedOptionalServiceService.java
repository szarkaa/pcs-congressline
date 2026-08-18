package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.repository.ChargeableItemInvoiceHistoryRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.web.rest.vm.OrderedOptionalServiceVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OrderedOptionalServiceService {

    private final OrderedOptionalServiceRepository repository;
    private final OptionalServiceRepository optionalServiceRepository;
    private final ChargeableItemInvoiceHistoryRepository ciihRepository;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;
    private final PayingGroupItemRepository pgiRepository;
    private final RegistrationRepository registrationRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public OrderedOptionalService save(OrderedOptionalService orderedOptionalService) {
        log.debug("Request to save OrderedOptionalService : {}", orderedOptionalService);
        return repository.save(orderedOptionalService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OrderedOptionalService save(@NonNull OrderedOptionalServiceVM viewModel) {
        OrderedOptionalService oos = viewModel.getId() != null ? getById(viewModel.getId()) : new OrderedOptionalService();
        oos.update(viewModel);
        oos.setOptionalService(viewModel.getOptionalServiceId() != null ? optionalServiceRepository.findById(viewModel.getOptionalServiceId()).orElse(null) : null);
        oos.setPayingGroupItem(viewModel.getPayingGroupItemId() != null ? pgiRepository.findById(viewModel.getPayingGroupItemId()).orElse(null) : null);
        if (oos.getRegistration() == null) {
            final Registration registration = registrationRepository.findById(viewModel.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registration not found by id: " + viewModel.getRegistrationId()));
            oos.setRegistration(registration);
        }
        return repository.save(oos);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OrderedOptionalService> findAllByRegistrationId(Long id) {
        log.debug("Request to get all OrderedOptionalServices by registration id: {}", id);
        return repository.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OrderedOptionalService> findById(Long id) {
        log.debug("Request to find OrderedOptionalService : {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OrderedOptionalService getById(Long id) {
        log.debug("Request to get OrderedOptionalService : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("OrderedOptionalService not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete OrderedOptionalService : {}", id);
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

        final OrderedOptionalService oos = getById(id);
        repository.delete(oos);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAllByRegistrationId(Long registrationId) {
        log.debug("Request to delete all OrderedOptionalService by registration id : {}", registrationId);
        repository.deleteAllByRegistrationId(registrationId);
    }

}
