package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;

import hu.congressline.pcs.domain.Hotel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class HotelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String name;
    protected String code;
    protected String city;
    protected String street;
    protected String zipCode;
    protected Double latitude;
    protected Double longitude;
    protected String phone;
    protected String fax;
    protected String email;
    protected String contactName;

    public HotelDTO(@NonNull Hotel hotel) {
        this.id = hotel.getId();
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
    }

    @Override
    public String toString() {
        return "HotelDTO{" + "id=" + id + "}";
    }
}
