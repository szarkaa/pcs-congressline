package hu.congressline.pcs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "bank_account")
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 32)
    @Column(name = "name", length = 32, nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 32)
    @Column(name = "bank_name", length = 32, nullable = false)
    private String bankName;

    @NotNull
    @Size(min = 8, max = 250)
    @Column(name = "bank_address", length = 250, nullable = false)
    private String bankAddress;

    @NotNull
    @Size(min = 8, max = 32)
    @Column(name = "bank_account", length = 32, nullable = false)
    private String bankAccount;

    @Column(name = "swift_code", length = 32)
    @Size(min = 0, max = 32)
    private String swiftCode;

    @ManyToOne
    private Currency currency;

    @ManyToMany(mappedBy = "bankAccounts")
    @JsonIgnore
    private Set<Congress> congresses = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankAccount that = (BankAccount) o;
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
        return "BankAccount{" + "id=" + id + "}";
    }
}
