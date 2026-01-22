package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MiscServiceVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @Size(max = 20)
    private String measure;

    @NotNull
    @DecimalMin("-99000000")
    @DecimalMax("99000000")
    private BigDecimal price;

    @NotNull
    private Long vatInfoId;

    @NotNull
    private Long currencyId;

    @NotNull
    private Long congressId;

    @Override
    public String toString() {
        return "MiscServiceVM{" + "id=" + id + "}";
    }
}
