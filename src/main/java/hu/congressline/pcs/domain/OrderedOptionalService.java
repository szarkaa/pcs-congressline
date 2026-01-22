package hu.congressline.pcs.domain;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.web.rest.vm.OrderedOptionalServiceVM;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "ordered_optional_service")
@DiscriminatorValue("OPTIONAL_SERVICE")
public class OrderedOptionalService extends ChargeableItem {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    @Max(100)
    @Column(name = "participant", nullable = false)
    private Integer participant;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @ManyToOne
    private OptionalService optionalService;

    @ManyToOne
    private PayingGroupItem payingGroupItem;

    @ManyToOne
    private Registration registration;

    @Override
    public String getChargeableItemName() {
        return optionalService.getName();
    }

    @Override
    public BigDecimal getChargeableItemPrice() {
        return optionalService.getPrice().multiply(new BigDecimal(participant));
    }

    @Override
    public Integer getChargeableItemVAT() {
        return optionalService.getVatInfo().getVat();
    }

    @Override
    public String getChargeableItemSZJ() {
        return optionalService.getVatInfo().getSzj();
    }

    @Override
    public String getChargeableItemCurrency() {
        return optionalService.getCurrency().getCurrency();
    }

    @Override
    public ChargeableItemType getChargeableItemType() {
        return ChargeableItemType.OPTIONAL_SERVICE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderedOptionalService orderedOptionalService = (OrderedOptionalService) o;
        if (orderedOptionalService.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), orderedOptionalService.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "OrderedOptionalService{" + "id=" + getId() + "}";
    }

    public void update(@NonNull OrderedOptionalServiceVM viewModel) {
        this.participant = viewModel.getParticipant();
        this.createdDate = this.createdDate == null ? LocalDate.now() :  this.createdDate;
    }
}
