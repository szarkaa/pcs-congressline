package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "online_reg_custom_question")
public class OnlineRegCustomQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Congress congress;

    @NotNull
    @ManyToOne
    private Currency currency;

    @Size(max = 1000)
    @Column(name = "question", length = 1000)
    private String question;

    @Column(name = "required")
    private Boolean required = Boolean.FALSE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "online_visibility", nullable = false)
    private OnlineVisibility onlineVisibility;

    @Min(0)
    @Max(100)
    @Column(name = "question_order")
    private Integer questionOrder;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_custom_question_answers", joinColumns = @JoinColumn(name = "online_reg_custom_question_id"))
    @Column(name = "value", length = 1000)
    private List<String> questionAnswers;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OnlineRegCustomQuestion)) {
            return false;
        }

        OnlineRegCustomQuestion that = (OnlineRegCustomQuestion) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
