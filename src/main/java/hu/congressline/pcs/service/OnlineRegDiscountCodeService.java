package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegDiscountCode;
import hu.congressline.pcs.repository.OnlineRegDiscountCodeRepository;
import hu.congressline.pcs.web.rest.vm.OnlineRegDiscountCodeVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OnlineRegDiscountCodeService {

    private final OnlineRegDiscountCodeRepository repository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegDiscountCode save(OnlineRegDiscountCode discountCode) {
        log.debug("Request to save online reg discount code : {}", discountCode);
        return repository.save(discountCode);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegDiscountCode save(@NonNull OnlineRegDiscountCodeVM viewModel) {
        OnlineRegDiscountCode discountCode = viewModel.getId() != null ? getById(viewModel.getId()) : new OnlineRegDiscountCode();
        discountCode.update(viewModel);
        if (discountCode.getCongress() == null) {
            discountCode.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(discountCode);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OnlineRegDiscountCode> findById(Long id) {
        log.debug("Request to find online reg discount code : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OnlineRegDiscountCode getById(Long id) {
        log.debug("Request to get online reg discount code : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Online reg discount code not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete online reg discount code : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OnlineRegDiscountCode> findAllByCongressId(Long id) {
        log.debug("Request to get all online reg discount codes by congress id: {}", id);
        return repository.findAllByCongressId(id);
    }

}
