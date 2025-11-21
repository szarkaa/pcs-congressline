package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class PaymentAddress extends ApiBase implements Signable {
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String zip;
    private String state;
    private String country;

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, address1);
        add(sb, address2);
        add(sb, address3);
        add(sb, city);
        add(sb, zip);
        add(sb, state);
        add(sb, country);
        deleteLast(sb);
        return sb.toString();
    }
}
