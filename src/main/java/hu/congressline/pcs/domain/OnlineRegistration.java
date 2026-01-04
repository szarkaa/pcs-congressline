package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "online_registration")
public class OnlineRegistration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 64)
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @NotNull
    @Size(max = 64)
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Size(max = 16)
    @Column(name = "title", length = 16)
    private String title;

    @Size(max = 64)
    @Column(name = "position", length = 64)
    private String position;

    @Size(max = 255)
    @Column(name = "workplace", length = 255)
    private String workplace;

    @Size(max = 255)
    @Column(name = "other_data", length = 255)
    private String otherData;

    @Size(max = 128)
    @Column(name = "department", length = 128)
    private String department;

    @Size(max = 32)
    @Column(name = "zip_code", length = 32)
    private String zipCode;

    @Size(max = 64)
    @Column(name = "city", length = 64)
    private String city;

    @Size(max = 255)
    @Column(name = "street", length = 255)
    private String street;

    @Size(max = 64)
    @Column(name = "phone", length = 64)
    private String phone;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @ManyToOne
    private RegistrationType registrationType;

    @ManyToOne
    private Room room;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Size(max = 64)
    @Column(name = "roommate", length = 64)
    private String roommate;

    @Size(max = 255)
    @Column(name = "room_remark", length = 255)
    private String roomRemark;

    @Size(max = 64)
    @Column(name = "payment_method", length = 64)
    private String paymentMethod;

    @Size(max = 16)
    @Column(name = "card_type", length = 16)
    private String cardType;

    @Size(max = 64)
    @Column(name = "check_name", length = 64)
    private String checkName;

    @Size(max = 255)
    @Column(name = "check_address", length = 255)
    private String checkAddress;

    @Size(max = 64)
    @Column(name = "card_holder_name", length = 64)
    private String cardHolderName;

    @Size(max = 64)
    @Column(name = "card_holder_address", length = 64)
    private String cardHolderAddress;

    @Size(max = 64)
    @Column(name = "card_number", length = 64)
    private String cardNumber;

    @Size(max = 2)
    @Column(name = "card_expiry_month", length = 2)
    private String cardExpiryMonth;

    @Size(max = 2)
    @Column(name = "card_expiry_year", length = 2)
    private String cardExpiryYear;

    @Size(max = 128)
    @Column(name = "invoice_name", length = 128)
    private String invoiceName;

    @ManyToOne
    private Country invoiceCountry;

    @Size(max = 32)
    @Column(name = "invoice_zip_code", length = 32)
    private String invoiceZipCode;

    @Size(max = 64)
    @Column(name = "invoice_city", length = 64)
    private String invoiceCity;

    @Size(max = 255)
    @Column(name = "invoice_address", length = 255)
    private String invoiceAddress;

    @Size(max = 64)
    @Column(name = "invoice_reference_number", length = 64)
    private String invoiceReferenceNumber;

    @Size(max = 64)
    @Column(name = "invoice_tax_number", length = 64)
    private String invoiceTaxNumber;

    @Column(name = "newsletter")
    private Boolean newsletter = Boolean.FALSE;

    @Column(name = "date_of_app")
    private LocalDateTime dateOfApp;

    @Column(name = "payment_trx_id", length = 15)
    private String paymentTrxId;

    @Column(name = "payment_order_number", length = 15)
    private String paymentOrderNumber;

    @Column(name = "payment_trx_result_code", length = 3)
    private String paymentTrxResultCode;

    @Column(name = "payment_trx_status", length = 32)
    private String paymentTrxStatus;

    @Column(name = "payment_trx_result_message", length = 100)
    private String paymentTrxResultMessage;

    @Column(name = "payment_trx_auth_code", length = 32)
    private String paymentTrxAuthCode;

    @Column(name = "bank_auth_number", length = 32)
    private String bankAuthNumber;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "payment_trx_date")
    private ZonedDateTime paymentTrxDate;

    @ManyToOne
    private Country country;

    @Size(min = 5, max = 32)
    @Column(name = "discount_code", length = 32)
    private String discountCode;

    @Min(0)
    @Max(100)
    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private ChargeableItemType discountType;

    @ManyToOne
    private Congress congress;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OnlineRegistration that = (OnlineRegistration) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "OnlineRegistration{" + "id=" + id + "}";
    }
}
