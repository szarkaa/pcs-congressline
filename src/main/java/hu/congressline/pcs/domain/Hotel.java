package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "hotel")
public class Hotel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Size(min = 3, max = 16)
    @Column(name = "code", length = 16, nullable = false)
    private String code;

    @NotNull
    @Size(max = 255)
    @Column(name = "city", length = 255, nullable = false)
    private String city;

    @NotNull
    @Size(max = 255)
    @Column(name = "street", length = 255, nullable = false)
    private String street;

    @NotNull
    @Size(max = 32)
    @Column(name = "zip_code", length = 32, nullable = false)
    private String zipCode;

    @Size(max = 64)
    @Column(name = "phone", length = 64)
    private String phone;

    @Size(max = 64)
    @Column(name = "fax", length = 64)
    private String fax;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @Size(max = 128)
    @Column(name = "contact_name", length = 128)
    private String contactName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Hotel hotel = (Hotel) o;
        if (hotel.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Hotel{" + "id=" + id + "}";
    }
}
