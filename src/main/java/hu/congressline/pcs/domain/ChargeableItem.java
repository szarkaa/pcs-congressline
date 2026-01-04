package hu.congressline.pcs.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "chargeable_item")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ChargeableItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_group_payment")
    private LocalDate dateOfGroupPayment;

    public abstract String getChargeableItemName();

    public abstract BigDecimal getChargeableItemPrice();

    public abstract Integer getChargeableItemVAT();

    public abstract String getChargeableItemSZJ();

    public abstract String getChargeableItemCurrency();

    public abstract ChargeableItemType getChargeableItemType();

    public abstract Registration getRegistration();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChargeableItem that = (ChargeableItem) o;
        if (that.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
