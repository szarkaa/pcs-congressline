package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.Country;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CountryDTO {
    private Long id;
    private String code;
    private String name;

    public CountryDTO(Country country) {
        this.id = country.getId();
        this.code = country.getCode();
        this.name = country.getName();
    }
}
