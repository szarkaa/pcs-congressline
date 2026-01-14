package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.repository.HotelRepository;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.HotelSummaryReportService;
import hu.congressline.pcs.service.dto.HotelSummaryDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class HotelSummaryReportResource {
    private static final String HOTEL_NOT_FOUND = "Hotel not found with id: ";

    private final HotelSummaryReportService service;
    private final CongressService congressService;
    private final HotelRepository hotelRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/hotel-summary/{meetingCode}/{hotelId}")
    public ResponseEntity<HotelSummaryDTO> getSummaryBy(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to get the HotelSummaryDTO : {}, {}", meetingCode, hotelId);
        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));
        return new ResponseEntity<>(service.findAll(congress, hotel), HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/hotel-summary/{meetingCode}/{hotelId}/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to download HotelSummaryDTO : {}, {}", meetingCode, hotelId);
        byte[] reportXlsx = new byte[0];

        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));

        try {
            reportXlsx = service.downloadReportXls(congress, hotel);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = ServiceUtil.normalizeForFilename(hotel.getName()) + "-hotel-summary-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating room reservation by rooms report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
