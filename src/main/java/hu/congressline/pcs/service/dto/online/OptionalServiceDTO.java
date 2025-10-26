package hu.congressline.pcs.service.dto.online;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.enumeration.OnlineType;
import lombok.Data;

@Data
public class OptionalServiceDTO {
    private Long id;
    private String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private BigDecimal price;
    private String currency;
    private Integer available;
    private OnlineType onlineType;

    public OptionalServiceDTO(OptionalService optionalService) {
        this.id = optionalService.getId();
        this.name = optionalService.getName() + (optionalService.getOnlineLabel() != null ? " " + optionalService.getOnlineLabel() : "");
        this.startDate = optionalService.getStartDate();
        this.endDate = optionalService.getEndDate();
        this.price = optionalService.getPrice();
        this.currency = optionalService.getCurrency().getCurrency();
        this.available = optionalService.getMaxPerson() - optionalService.getReserved();
        this.onlineType = optionalService.getOnlineType();
    }
}
