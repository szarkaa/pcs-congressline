package hu.congressline.pcs.service.dto;

import java.time.LocalDate;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Country;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StrippedCongressDTO {

    @NotNull
    private Long id;
    @NotNull
    private String meetingCode;
    @NotNull
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Country defaultCountry;

    public StrippedCongressDTO(Congress congress) {
        this.id = congress.getId();
        this.meetingCode = congress.getMeetingCode();
        this.name = congress.getName();
        this.startDate = congress.getStartDate();
        this.endDate = congress.getEndDate();
        this.defaultCountry = congress.getDefaultCountry();
    }

    @Override
    public String toString() {
        return "CongressVM{" + "id='" + id + "}";
    }
}
