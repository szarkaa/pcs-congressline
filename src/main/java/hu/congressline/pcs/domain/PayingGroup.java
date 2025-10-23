package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "paying_group")
public class PayingGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 64)
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Size(max = 32)
    @Column(name = "zip_code", length = 32)
    private String zipCode;

    @Size(max = 64)
    @Column(name = "city", length = 64)
    private String city;

    @Size(max = 128)
    @Column(name = "street", length = 128)
    private String street;

    @Size(max = 128)
    @Column(name = "contact_name", length = 128)
    private String contactName;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @Size(max = 64)
    @Column(name = "phone", length = 64)
    private String phone;

    @Size(max = 64)
    @Column(name = "fax", length = 64)
    private String fax;

    @Size(max = 64)
    @Column(name = "tax_number", length = 64)
    private String taxNumber;

    @ManyToOne
    private Country country;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private Congress congress;

    @OneToMany(mappedBy = "payingGroup", fetch = FetchType.LAZY)
    private Set<PayingGroupItem> payingGroupItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PayingGroup payingGroup = (PayingGroup) o;
        if (payingGroup.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, payingGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PayingGroup{" + "id=" + id + "}";
    }
}
