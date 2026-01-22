package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.enumeration.OnlineType;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class OptionalServiceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private Integer maxPerson;
    private Integer reserved;
    private OnlineType onlineType;
    private OnlineVisibility onlineVisibility;
    private String onlineLabel;
    private Integer onlineOrder;
    private VatInfoDTO vatInfo;
    private CurrencyDTO currency;
    private Long congressId;

    public OptionalServiceDTO(@NonNull OptionalService optionalService) {
        this.id = optionalService.getId();
        this.code = optionalService.getCode();
        this.name = optionalService.getName();
        this.startDate = optionalService.getStartDate();
        this.endDate = optionalService.getEndDate();
        this.price = optionalService.getPrice();
        this.maxPerson = optionalService.getMaxPerson();
        this.reserved = optionalService.getReserved();
        this.onlineType = optionalService.getOnlineType();
        this.onlineVisibility = optionalService.getOnlineVisibility();
        this.onlineLabel = optionalService.getOnlineLabel();
        this.onlineOrder = optionalService.getOnlineOrder();
        this.currency = nonNull(optionalService.getCurrency()) ? new CurrencyDTO(optionalService.getCurrency()) : null;
        this.vatInfo = nonNull(optionalService.getVatInfo()) ? new VatInfoDTO(optionalService.getVatInfo()) : null;
        this.congressId = nonNull(optionalService.getCongress()) ? optionalService.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "OptionalServiceDTO{" + "id=" + id + "}";
    }
}
