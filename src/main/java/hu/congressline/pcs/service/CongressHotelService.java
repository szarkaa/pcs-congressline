package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.CongressHotel;
import hu.congressline.pcs.repository.CongressHotelRepository;
import hu.congressline.pcs.repository.HotelRepository;
import hu.congressline.pcs.web.rest.vm.CongressHotelVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CongressHotelService {

    private final CongressHotelRepository repository;
    private final HotelRepository hotelRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public CongressHotel save(CongressHotel congressHotel) {
        log.debug("Request to save congress hotel : {}", congressHotel);
        return repository.save(congressHotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public CongressHotel save(@NonNull CongressHotelVM viewModel) {
        CongressHotel congressHotel = viewModel.getId() != null ? getById(viewModel.getId()) : new CongressHotel();
        congressHotel.setHotel(viewModel.getHotelId() != null ? hotelRepository.findById(viewModel.getHotelId()).orElse(null) : null);
        if (congressHotel.getCongress() == null) {
            congressHotel.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(congressHotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<CongressHotel> findById(Long id) {
        log.debug("Request to find congress hotel : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public CongressHotel getById(Long id) {
        log.debug("Request to get congress hotel : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Congress hotel not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete congress hotel : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<CongressHotel> findByCongressId(Long id) {
        log.debug("Request to get all congress hotels by congress id: {}", id);
        return repository.findByCongressId(id);
    }

}
