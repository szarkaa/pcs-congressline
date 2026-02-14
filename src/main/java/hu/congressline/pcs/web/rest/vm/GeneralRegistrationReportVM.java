package hu.congressline.pcs.web.rest.vm;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GeneralRegistrationReportVM extends GeneralReportVM {
    private Integer regId;
    private String lastName;
    private String firstName;
    private String invoiceName;
    private String email;
    private String accPeopleLastName;
    private String accPeopleFirstName;
    private String position;
    private String otherData;
    private Long registrationType;
    private Long workplace;
    private Long payingGroup;
    private Set<Long> optionalServices = new HashSet<>();
    private Long hotelId;
    private Long country;
    private Boolean countryNegation;
    private Boolean presenter;
    private Boolean etiquette;
    private Boolean closed;
    private Boolean onSpot;
    private Boolean cancelled;
}
