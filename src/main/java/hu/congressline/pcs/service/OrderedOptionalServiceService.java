package hu.congressline.pcs.service;

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.web.rest.vm.OrderedOptionalServiceVM;
import hu.congressline.pcs.web.rest.vm.WorkplaceVM;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.repository.ChargeableItemInvoiceHistoryRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
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
        OrderedOptionalService result = repository.save(orderedOptionalService);
        OptionalService optionalService = result.getOptionalService();
        increaseOptionalServiceReservedNumber(optionalService, result.getParticipant());
        optionalServiceRepository.save(optionalService);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OrderedOptionalService save(@NonNull OrderedOptionalServiceVM viewModel) {
        OrderedOptionalService oos = viewModel.getId() != null ? getById(viewModel.getId()) : new OrderedOptionalService();
        oos.update(viewModel);
        oos.setOptionalService(viewModel.getOptionalServiceId() != null ? optionalServiceRepository.findById(viewModel.getOptionalServiceId()).orElse(null) : null);
        oos.setPayingGroupItem(viewModel.getPayingGroupItemId() != null ? pgiRepository.findById(viewModel.getPayingGroupItemId()).orElse(null) : null);
        if (oos.getRegistration() == null) {
            final Registration registration = registrationRepository.findById(viewModel.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registration not found with id: " + viewModel.getRegistrationId()));
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
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OrderedOptionalService getById(Long id) {
        log.debug("Request to get OrderedOptionalService : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("OrderedOptionalService not found with id: " + id));
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
        OptionalService optionalService = oos.getOptionalService();
        decreaseOptionalServiceReservedNumber(optionalService, oos.getParticipant());
        repository.delete(oos);
    }

    public void increaseOptionalServiceReservedNumber(OptionalService optionalService, Integer participants) {
        optionalService.setReserved(optionalService.getReserved() + participants);
        optionalServiceRepository.save(optionalService);
    }

    public void decreaseOptionalServiceReservedNumber(OptionalService optionalService, Integer participants) {
        optionalService.setReserved(optionalService.getReserved() - participants);
        optionalServiceRepository.save(optionalService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAllByRegistrationId(Long registrationId) {
        log.debug("Request to delete all OrderedOptionalService by registration id : {}", registrationId);
        final List<OrderedOptionalService> orderedOptionalServices = repository.findAllByRegistrationId(registrationId);
        Map<Long, Integer> participantsMap = new HashMap<>();
        orderedOptionalServices.forEach(oos -> {
            OptionalService optionalService = oos.getOptionalService();
            Integer participant = oos.getParticipant();
            if (participantsMap.get(optionalService.getId()) != null) {
                participant += participantsMap.get(optionalService.getId());
            }
            participantsMap.put(optionalService.getId(), participant);
        });

        Set<Long> alreadyUpdated = new HashSet<>();
        orderedOptionalServices.forEach(oos -> {
            OptionalService optionalService = oos.getOptionalService();
            if (!alreadyUpdated.contains(optionalService.getId())) {
                optionalService.setReserved(optionalService.getReserved() - participantsMap.get(optionalService.getId()));
                optionalServiceRepository.save(optionalService);
                alreadyUpdated.add(optionalService.getId());
            }
            repository.delete(oos);
        });
    }

}
