package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.OptionalText;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class OptionalTextDTO {
    private Long id;
    private String name;
    private String optionalText;
    private Long congressId;

    public OptionalTextDTO(@NonNull OptionalText optionalText) {
        this.id = optionalText.getId();
        this.name = optionalText.getName();
        this.optionalText = optionalText.getOptionalText();
        this.congressId = nonNull(optionalText.getCongress()) ? optionalText.getCongress().getId() : null;
    }
}
