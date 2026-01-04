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
import lombok.Data;

@Data
@Entity
@Table(name = "online_registration_custom_answer")
public class OnlineRegistrationCustomAnswer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private OnlineRegistration onlineRegistration;

    @ManyToOne
    private Registration registration;

    @NotNull
    @ManyToOne
    private OnlineRegCustomQuestion question;

    @NotNull
    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OnlineRegistrationCustomAnswer that = (OnlineRegistrationCustomAnswer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OnlineRegistrationCustomAnswer{" + "id=" + id + "}";
    }
}
