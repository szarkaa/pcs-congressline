package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.web.rest.vm.PayingGroupItemVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PayingGroupItemService {

    private final PayingGroupItemRepository repository;
    private final PayingGroupService payingGroupService;

    @SuppressWarnings("MissingJavadocMethod")
    public PayingGroupItem save(PayingGroupItem payingGroupItem) {
        log.debug("Request to save paying group item: {}", payingGroupItem);
        return repository.save(payingGroupItem);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public PayingGroupItem save(@NonNull PayingGroupItemVM viewModel) {
        PayingGroupItem payingGroupItem = viewModel.getId() != null ? getById(viewModel.getId()) : new PayingGroupItem();
        payingGroupItem.update(viewModel);
        if (payingGroupItem.getPayingGroup() == null) {
            payingGroupItem.setPayingGroup(payingGroupService.getById(viewModel.getPayingGroupId()));
        }
        return repository.save(payingGroupItem);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PayingGroupItem> findById(Long id) {
        log.debug("Request to find paying group item: {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PayingGroupItem getById(Long id) {
        log.debug("Request to get paying group item: {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Paying group item not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete paying group item: {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<PayingGroupItem> findAllByPayingGroupId(Long id) {
        log.debug("Request to get all paying group items by paying group id: {}", id);
        return repository.findAllByPayingGroupId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<PayingGroupItem> findAllByItemTypeAndCongressId(ChargeableItemType itemType, Long congressId) {
        log.debug("Request to get all paying group items by congress id: {} and item type: {}", congressId, itemType);
        return repository.findAllByChargeableItemTypeAndPayingGroupCongressId(itemType, congressId);
    }

}
