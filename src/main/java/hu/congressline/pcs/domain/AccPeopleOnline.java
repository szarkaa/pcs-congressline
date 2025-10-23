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
@Table(name = "acc_people_online")
public class AccPeopleOnline implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 8)
    @Column(name = "title", length = 8)
    private String title;

    @NotNull
    @Size(max = 64)
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @NotNull
    @Size(max = 64)
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @ManyToOne
    private OnlineRegistrationRegistrationType onlineRegistrationRegistrationType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccPeopleOnline accPeople = (AccPeopleOnline) o;
        if (accPeople.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, accPeople.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AccPeopleOnline{" + "id=" + id + "}";
    }
}
