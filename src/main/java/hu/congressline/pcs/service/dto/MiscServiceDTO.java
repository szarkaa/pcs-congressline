package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.MiscService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class MiscServiceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String measure;
    private BigDecimal price;
    private VatInfoDTO vatInfo;
    private CurrencyDTO currency;
    private Long congressId;

    public MiscServiceDTO(@NonNull MiscService miscService) {
        this.id = miscService.getId();
        this.name = miscService.getName();
        this.description = miscService.getDescription();
        this.measure = miscService.getMeasure();
        this.price = miscService.getPrice();
        this.currency = nonNull(miscService.getCurrency()) ? new CurrencyDTO(miscService.getCurrency()) : null;
        this.vatInfo = nonNull(miscService.getVatInfo()) ? new VatInfoDTO(miscService.getVatInfo()) : null;
        this.congressId = nonNull(miscService.getCongress()) ? miscService.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "MiscServiceDTO{" + "id=" + id + "}";
    }
}
