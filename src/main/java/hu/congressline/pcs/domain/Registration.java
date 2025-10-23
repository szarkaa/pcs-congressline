package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "registration")
public class Registration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "reg_id", nullable = false)
    private Integer regId;

    @NotNull
    @Size(max = 64)
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @NotNull
    @Size(max = 64)
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Size(max = 16)
    @Column(name = "short_name", length = 16)
    private String shortName;

    @Size(max = 16)
    @Column(name = "title", length = 16)
    private String title;

    @Size(max = 64)
    @Column(name = "position", length = 64)
    private String position;

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

    @Size(max = 64)
    @Column(name = "fax", length = 64)
    private String fax;

    @Column(name = "date_of_app")
    private LocalDate dateOfApp;

    @Size(max = 2000)
    @Column(name = "remark", columnDefinition = "text", length = 2000)
    private String remark;

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
    @Column(name = "invoice_tax_number", length = 64)
    private String invoiceTaxNumber;

    @Column(name = "on_spot")
    private Boolean onSpot;

    @Column(name = "cancelled")
    private Boolean cancelled;

    @Column(name = "presenter")
    private Boolean presenter;

    @Column(name = "closed")
    private Boolean closed;

    @Column(name = "etiquette")
    private Boolean etiquette;

    @ManyToOne
    private Workplace workplace;

    @ManyToOne
    private Country country;

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
        Registration registration = (Registration) o;
        if (registration.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, registration.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Registration{" + "id=" + id + ", regId='" + regId + "'" + ", lastName='" + lastName + "'" + ", firstName='" + firstName + "'" + "}";
    }
}
