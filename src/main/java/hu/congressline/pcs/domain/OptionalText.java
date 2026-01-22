package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.congressline.pcs.web.rest.vm.OptionalTextVM;
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
@Table(name = "optional_text")
public class OptionalText implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 32)
    @Column(name = "name", length = 32, nullable = false)
    private String name;

    @NotNull
    @Size(max = 2048)
    @Column(name = "optional_text", length = 2048, nullable = false)
    private String optionalText;

    @ManyToOne
    private Congress congress;

    public void update(OptionalTextVM viewModel) {
        this.name = viewModel.getName();
        this.optionalText = viewModel.getOptionalText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OptionalText that = (OptionalText) o;

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
        return "OptionalText{" + "id=" + id + "}";
    }
}
