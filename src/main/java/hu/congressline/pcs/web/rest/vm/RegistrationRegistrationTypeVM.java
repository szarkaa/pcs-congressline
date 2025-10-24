package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;
import java.util.Objects;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationRegistrationTypeVM {
    private Long id;
    private Long registrationTypeId;
    private String registrationTypeName;
    private String registrationTypeType;
    private Long registrationId;
    private Long payingGroupItemId;
    private BigDecimal regFee;
    private Integer accPeople;
    private String currency;
    private Integer chargeableItemVAT;
    private String chargeableItemSZJ;
    private String payingGroupName;
    private BigDecimal priceWithDiscount;

    public RegistrationRegistrationTypeVM(RegistrationRegistrationType rrt) {
        this.id = rrt.getId();
        this.registrationTypeId = rrt.getRegistrationType().getId();
        this.registrationTypeName = rrt.getRegistrationType().getName();
        this.registrationTypeType = rrt.getRegistrationType().getRegistrationType().toString();
        this.registrationId = rrt.getRegistration().getId();
        this.payingGroupItemId = rrt.getPayingGroupItem() != null ? rrt.getPayingGroupItem().getId() : null;
        this.regFee = rrt.getRegFee();
        this.accPeople = rrt.getAccPeople();
        this.currency = rrt.getCurrency();
        this.chargeableItemVAT = rrt.getRegistrationType().getVatInfo().getVat();
        this.chargeableItemSZJ = rrt.getRegistrationType().getVatInfo().getSzj();
        this.payingGroupName = rrt.getPayingGroupItem() != null ? rrt.getPayingGroupItem().getPayingGroup().getName() + "/" + rrt.getPayingGroupItem().getName() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegistrationRegistrationTypeVM that = (RegistrationRegistrationTypeVM) o;
        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
