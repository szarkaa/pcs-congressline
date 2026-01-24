package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.VatInfo;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.VatInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VatInfoService {

    private final VatInfoRepository vatInfoRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public VatInfo save(VatInfo vatInfo) {
        log.debug("Request to save VatInfo : {}", vatInfo);
        return vatInfoRepository.save(vatInfo);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<VatInfo> findAll() {
        log.debug("Request to get all VatInfos");
        return vatInfoRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<VatInfo> findById(Long id) {
        log.debug("Request to find VatInfo : {}", id);
        return vatInfoRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public VatInfo getById(Long id) {
        log.debug("Request to get Congress : {}", id);
        return vatInfoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("VatInfo not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<VatInfo> findAllByCongressId(Long id) {
        log.debug("Request to get all VatInfos by Congress id");
        List<VatInfo> result = null;
        if (id == null || id.equals(0L)) {
            result = vatInfoRepository.findByCongressIsNull();
        } else {
            result = vatInfoRepository.findByCongressId(id);
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<VatInfo> findAllForCongressId(Long id) {
        log.debug("Request to get all VatInfos by Congress id and the ones with congress null value");
        return vatInfoRepository.findAllForCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<VatInfo> findAllForCongressIdAndItemType(Long id, ChargeableItemType itemType) {
        log.debug("Request to get all VatInfos by itemType and Congress id and the ones with congress null value");
        return vatInfoRepository.findAllForCongressIdAndItemType(id, itemType);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete VatInfo : {}", id);
        vatInfoRepository.deleteById(id);
    }
}
