package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;
import java.util.Objects;

import hu.congressline.pcs.domain.OrderedOptionalService;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderedOptionalServiceVM {
    private Long id;
    private Long optionalServiceId;
    private String optionalServiceName;
    private Integer participant;
    private Long registrationId;
    private Long payingGroupItemId;
    private BigDecimal chargeableItemPrice;
    private String chargeableItemCurrency;
    private Integer chargeableItemVAT;
    private String chargeableItemSZJ;
    private String payingGroupName;
    private BigDecimal priceWithDiscount;

    public OrderedOptionalServiceVM(OrderedOptionalService oos) {
        this.id = oos.getId();
        this.optionalServiceId = oos.getOptionalService().getId();
        this.optionalServiceName = oos.getOptionalService().getName();
        this.participant = oos.getParticipant();
        this.registrationId = oos.getRegistration().getId();
        this.payingGroupItemId = oos.getPayingGroupItem() != null ? oos.getPayingGroupItem().getId() : null;
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

        OrderedOptionalServiceVM that = (OrderedOptionalServiceVM) o;

        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
