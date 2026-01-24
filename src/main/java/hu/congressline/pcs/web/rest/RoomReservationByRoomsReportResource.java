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
import hu.congressline.pcs.service.RoomReservationByRoomsReportService;
import hu.congressline.pcs.service.dto.RoomReservationByRoomsDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoomReservationByRoomsReportResource {
    private static final String HOTEL_NOT_FOUND = "Hotel not found by id: ";

    private final RoomReservationByRoomsReportService service;
    private final CongressService congressService;
    private final HotelRepository hotelRepository;

    /**
     * GET  /room-reservation-by-rooms : get all the list of report items.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of report items in body
     */
    @RequestMapping("/room-reservation-by-rooms/{meetingCode}/{hotelId}")
    public List<RoomReservationByRoomsDTO> getAllRoomReservationByRoom(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to get all RoomReservationByRoomsReports");
        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));
        return service.findAll(congress, hotel);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/room-reservation-by-rooms/{meetingCode}/{hotelId}/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@PathVariable String meetingCode, @PathVariable Long hotelId) {
        log.debug("REST request to download RoomReservationByRoomsReport : {}, {}", meetingCode, hotelId);
        byte[] reportXlsx = new byte[0];
        Congress congress = congressService.getByMeetingCode(meetingCode);
        Hotel hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new IllegalArgumentException(HOTEL_NOT_FOUND + hotelId));

        try {
            reportXlsx = service.downloadReportXls(congress, hotel);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = ServiceUtil.normalizeForFilename(hotel.getName()) + "-room-reservation-by-rooms-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating room reservation by rooms report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
