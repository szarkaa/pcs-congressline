package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegCustomQuestionVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    private Long congressId;

    @NotNull
    private Long currencyId;

    @Size(max = 1000)
    private String question;

    private Boolean required = Boolean.FALSE;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OnlineVisibility onlineVisibility;

    @Min(0)
    @Max(100)
    private Integer questionOrder;

    private List<String> questionAnswers = new ArrayList<>();

}
