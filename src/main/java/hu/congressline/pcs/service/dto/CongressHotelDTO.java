package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.CongressHotel;
import hu.congressline.pcs.domain.Hotel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class CongressHotelDTO extends HotelDTO {

    protected Long hotelId;
    protected Long congressId;

    public CongressHotelDTO(@NonNull CongressHotel congressHotel) {
        Hotel hotel = congressHotel.getHotel();
        this.id = congressHotel.getId();
        this.hotelId = hotel.getId();
        this.name = hotel.getName();
        this.code = hotel.getCode();
        this.city = hotel.getCity();
        this.street = hotel.getStreet();
        this.zipCode = hotel.getZipCode();
        this.latitude = hotel.getLatitude();
        this.longitude = hotel.getLongitude();
        this.phone = hotel.getPhone();
        this.fax = hotel.getFax();
        this.email = hotel.getEmail();
        this.contactName = hotel.getContactName();
        this.congressId = nonNull(congressHotel.getCongress()) ? congressHotel.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "CongressHotelDTO{" + "id=" + id + "}";
    }
}
