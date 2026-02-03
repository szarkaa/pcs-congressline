package hu.congressline.pcs.service.dto;

import java.math.BigDecimal;
import java.util.Objects;

import hu.congressline.pcs.domain.OrderedOptionalService;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderedOptionalServiceDTO {
    private Long id;
    private Long optionalServiceId;
    private String optionalServiceName;
    private Integer participant;
    private Long registrationId;
    private Long payingGroupItemId;
    private String chargeableItemName;
    private BigDecimal chargeableItemPrice;
    private String chargeableItemCurrency;
    private Integer chargeableItemVAT;
    private String chargeableItemSZJ;
    private String payingGroupName;
    private BigDecimal priceWithDiscount;

    public OrderedOptionalServiceDTO(OrderedOptionalService oos) {
        this.id = oos.getId();
        this.optionalServiceId = oos.getOptionalService().getId();
        this.optionalServiceName = oos.getOptionalService().getName();
        this.participant = oos.getParticipant();
        this.registrationId = oos.getRegistration().getId();
        this.payingGroupItemId = oos.getPayingGroupItem() != null ? oos.getPayingGroupItem().getId() : null;
        this.chargeableItemName = oos.getOptionalService().getName();
        this.chargeableItemPrice = oos.getChargeableItemPrice();
        this.chargeableItemCurrency = oos.getOptionalService().getCurrency().getCurrency();
        this.chargeableItemVAT = oos.getOptionalService().getVatInfo().getVat();
        this.chargeableItemSZJ = oos.getOptionalService().getVatInfo().getSzj();
        this.payingGroupName = oos.getPayingGroupItem() != null ? oos.getPayingGroupItem().getPayingGroup().getName() + "/" + oos.getPayingGroupItem().getName() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrderedOptionalServiceDTO that = (OrderedOptionalServiceDTO) o;

        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
