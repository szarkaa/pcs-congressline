package hu.congressline.pcs.service.dto.online;

import java.util.ArrayList;
import java.util.List;

import hu.congressline.pcs.domain.Hotel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelDTO {
    private Long id;
    private String name;
    private String zipCode;
    private String city;
    private String street;
    private List<RoomDTO> singleList;
    private List<RoomDTO> doubleList;

    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.zipCode = hotel.getZipCode();
        this.city = hotel.getCity();
        this.street = hotel.getStreet();
        this.singleList = new ArrayList<>();
        this.doubleList = new ArrayList<>();
    }
}
