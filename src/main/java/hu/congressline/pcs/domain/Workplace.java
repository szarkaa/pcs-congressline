package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "workplace")
public class Workplace implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "vat_reg_number")
    private String vatRegNumber;

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
    @Column(name = "fax", length = 64)
    private String fax;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @ManyToOne
    private Country country;

    @ManyToOne
    private Congress congress;

    @SuppressWarnings("MissingJavadocMethod")
    public static Workplace copy(Workplace workplace) {
        Workplace copy = new Workplace();
        copy.setName(workplace.getName());
        copy.setVatRegNumber(workplace.getVatRegNumber());
        copy.setDepartment(workplace.getDepartment());
        copy.setZipCode(workplace.getZipCode());
        copy.setCity(workplace.getCity());
        copy.setStreet(workplace.getStreet());
        copy.setPhone(workplace.getPhone());
        copy.setFax(workplace.getFax());
        copy.setEmail(workplace.getEmail());
        copy.setCountry(workplace.getCountry());
        copy.setCongress(workplace.getCongress());
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

        Workplace workplace = (Workplace) o;
        if (workplace.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, workplace.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Workplace{" + "id=" + id + "}";
    }
}
