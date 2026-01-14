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
import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.repository.HotelRepository;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.RoomReservationByParticipantsReportService;
import hu.congressline.pcs.service.dto.RoomReservationByParticipantsDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoomReservationByParticipantsReportResource {
    private static final String HOTEL_NOT_FOUND = "Hotel not found with id: ";

    private final RoomReservationByParticipantsReportService service;
    private final CongressService congressService;
    private final HotelRepository hotelRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/room-reservation-by-participants/{meetingCode}/{hotelId}")
    public List<RoomReservationByParticipantsDTO> getAllParticipants(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to get all RoomReservationByParticipantsReports");
        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));
        return service.findAll(congress, hotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/room-reservation-by-participants/{meetingCode}/{hotelId}/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to download RoomReservationByParticipantsReport : {}, {}", meetingCode, hotelId);
        byte[] reportXlsx = new byte[0];

        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));
        try {
            reportXlsx = service.downloadReportXls(congress, hotel);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = ServiceUtil.normalizeForFilename(hotel.getName()) + "-room-reservation-by-participants-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating room reservation by participants report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
