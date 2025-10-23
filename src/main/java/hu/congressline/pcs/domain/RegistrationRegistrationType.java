package hu.congressline.pcs.domain;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "registration_registration_type")
@DiscriminatorValue("REGISTRATION")
public class RegistrationRegistrationType extends ChargeableItem {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "reg_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal regFee;

    @Size(max = 3)
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "acc_people")
    private Integer accPeople;

    @ManyToOne
    private RegistrationType registrationType;

    @ManyToOne
    private PayingGroupItem payingGroupItem;

    @ManyToOne
    private Registration registration;

    @Override
    public String getChargeableItemName() {
        return registrationType.getName();
    }

    @Override
    public BigDecimal getChargeableItemPrice() {
        if (getRegistrationType().getRegistrationType() == RegistrationTypeType.ACCOMPANYING_FEE) {
            return regFee.multiply(new BigDecimal(accPeople));
        } else {
            return regFee;
        }
    }

    @Override
    public Integer getChargeableItemVAT() {
        return registrationType.getVatInfo().getVat();
    }

    @Override
    public String getChargeableItemSZJ() {
        return registrationType.getVatInfo().getSzj();
    }

    @Override
    public String getChargeableItemCurrency() {
        return registrationType.getCurrency().getCurrency();
    }

    @Override
    public ChargeableItemType getChargeableItemType() {
        return ChargeableItemType.REGISTRATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistrationRegistrationType registrationRegistrationType = (RegistrationRegistrationType) o;
        if (registrationRegistrationType.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), registrationRegistrationType.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RegistrationRegistrationType{" + "id=" + getId() + "}";
    }
}
