package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.OnlineType;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import hu.congressline.pcs.web.rest.vm.RegistrationTypeVM;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "registration_type")
public class RegistrationType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 16)
    @Column(name = "code", length = 16, nullable = false)
    private String code;

    @NotNull
    @Size(min = 5, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    @Column(name = "first_reg_fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal firstRegFee;

    @Column(name = "first_deadline")
    private LocalDate firstDeadline;

    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    @Column(name = "second_reg_fee", precision = 10, scale = 2)
    private BigDecimal secondRegFee;

    @Column(name = "second_deadline")
    private LocalDate secondDeadline;

    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    @Column(name = "third_reg_fee", precision = 10, scale = 2)
    private BigDecimal thirdRegFee;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type", nullable = false)
    private RegistrationTypeType registrationType;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private VatInfo vatInfo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "online_type", nullable = false)
    private OnlineType onlineType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "online_visibility", nullable = false)
    private OnlineVisibility onlineVisibility;

    @Size(max = 200)
    @Column(name = "online_label", length = 200)
    private String onlineLabel;

    @Min(0)
    @Max(100)
    @Column(name = "online_order")
    private Integer onlineOrder;

    @ManyToOne
    private Congress congress;

    @SuppressWarnings("MissingJavadocMethod")
    public void update(@NonNull RegistrationTypeVM viewModel) {
        this.code = viewModel.getCode();
        this.name = viewModel.getName();
        this.firstRegFee = viewModel.getFirstRegFee();
        this.firstDeadline = viewModel.getFirstDeadline();
        this.secondRegFee = viewModel.getSecondRegFee();
        this.secondDeadline = viewModel.getSecondDeadline();
        this.thirdRegFee = viewModel.getThirdRegFee();
        this.registrationType = viewModel.getRegistrationType();
        this.onlineType = viewModel.getOnlineType();
        this.onlineVisibility = viewModel.getOnlineVisibility();
        this.onlineLabel = viewModel.getOnlineLabel();
        this.onlineOrder = viewModel.getOnlineOrder();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static RegistrationType copy(RegistrationType registrationType) {
        RegistrationType copy = new RegistrationType();
        copy.setCode(registrationType.getCode());
        copy.setName(registrationType.getName());
        copy.setFirstRegFee(registrationType.getFirstRegFee());
        copy.setFirstDeadline(registrationType.getFirstDeadline());
        copy.setSecondRegFee(registrationType.getSecondRegFee());
        copy.setSecondDeadline(registrationType.getSecondDeadline());
        copy.setThirdRegFee(registrationType.getThirdRegFee());
        copy.setRegistrationType(registrationType.getRegistrationType());
        copy.setVatInfo(registrationType.getVatInfo());
        copy.setCurrency(registrationType.getCurrency());
        copy.setOnlineType(registrationType.getOnlineType());
        copy.setOnlineVisibility(registrationType.getOnlineVisibility());
        copy.setOnlineLabel(registrationType.getOnlineLabel());
        copy.setOnlineOrder(registrationType.getOnlineOrder());
        copy.setCongress(registrationType.getCongress());
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistrationType that = (RegistrationType) o;
        if (that.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RegistrationType{" + "id=" + id + "}";
    }
}
