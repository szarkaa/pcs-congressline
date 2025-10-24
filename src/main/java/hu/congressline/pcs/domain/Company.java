package hu.congressline.pcs.domain;

import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "company")
public class Company implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 5, max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "name_2", length = 255)
    private String name2;

    @Size(max = 255)
    @Column(name = "address_2", length = 255)
    private String address2;

    @NotNull
    @Size(max = 2)
    @Column(length = 2, nullable = false)
    private String countryCode;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String zipCode;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String city;

    @NotNull
    @Size(max = 250)
    @Column(length = 250, nullable = false)
    private String streetName;

    @NotNull
    @Size(max = 100)
    @Column(length = 100, nullable = false)
    private String publicPlaceCategory;

    @NotNull
    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String number;

    @Size(max = 10)
    @Column(length = 10)
    private String building;

    @Size(max = 10)
    @Column(length = 10)
    private String staircase;

    @Size(max = 10)
    @Column(length = 10)
    private String floor;

    @Size(max = 10)
    @Column(length = 10)
    private String door;

    @NotNull
    @Size(max = 64)
    @Column(name = "tax_number", length = 64, nullable = false)
    private String taxNumber;

    @Size(max = 64)
    @Column(name = "eu_tax_number", length = 64)
    private String euTaxNumber;

    @Size(max = 64)
    @Column(name = "phone", length = 64)
    private String phone;

    @Size(max = 64)
    @Column(name = "fax", length = 64)
    private String fax;

    @Column(name = "licence_number")
    private String licenceNumber;

    @NotNull
    @Size(max = 64)
    @Column(name = "invoice_number_prefix", length = 64, nullable = false)
    private String invoiceNumberPrefix;

    @NotNull
    @Column(name = "invoice_number", nullable = false)
    private Integer invoiceNumber = 0;

    @NotNull
    @Column(name = "proforma_invoice_number", nullable = false)
    private Integer proFormaInvoiceNumber = 0;

    @Column(name = "annual_year_prefix", nullable = false)
    private Integer annualYearPrefix = LocalDate.now().getYear();

    @NotNull
    @Column(name = "payment_trx_id", nullable = false)
    private Integer paymentTrxId = 0;

    @SuppressWarnings("MissingJavadocMethod")
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(getZipCode()).append(" ")
                .append(getCity()).append(" ")
                .append(getStreetName()).append(" ")
                .append(getPublicPlaceCategory()).append(" ")
                .append(getNumber());

        if (StringUtils.isNotEmpty(getBuilding())) {
            sb.append(" ").append(getBuilding());
        }
        if (StringUtils.isNotEmpty(getStaircase())) {
            sb.append(" ").append(getStaircase());
        }
        if (StringUtils.isNotEmpty(getFloor())) {
            sb.append(" ").append(getFloor());
        }
        if (StringUtils.isNotEmpty(getDoor())) {
            sb.append(" ").append(getDoor());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
        if (company.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Company{" + "id=" + id + "}";
    }
}
