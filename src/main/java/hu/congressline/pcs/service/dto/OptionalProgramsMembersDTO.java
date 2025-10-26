package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OptionalProgramsMembersDTO implements Serializable {
    private String program;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private String currency;
    private Integer numberOfApplicants;
    private Integer maxPerson;
}
