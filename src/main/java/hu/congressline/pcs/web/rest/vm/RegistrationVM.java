package hu.congressline.pcs.web.rest.vm;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationVM {
    private Long id;
    @NotNull
    @Size(max = 64)
    private String lastName;
    @NotNull
    @Size(max = 64)
    private String firstName;
    @Size(max = 16)
    private String shortName;
    @Size(max = 16)
    private String title;
    @Size(max = 64)
    private String position;
    @Size(max = 255)
    private String otherData;
    @Size(max = 128)
    private String department;
    @Size(max = 32)
    private String zipCode;
    @Size(max = 64)
    private String city;
    @Size(max = 255)
    private String street;
    @Size(max = 64)
    private String phone;
    @Size(max = 64)
    private String email;
    @Size(max = 64)
    private String fax;
    private LocalDate dateOfApp;
    @Size(max = 2000)
    private String remark;
    @Size(max = 128)
    private String invoiceName;
    private Long invoiceCountryId;
    @Size(max = 32)
    private String invoiceZipCode;
    @Size(max = 64)
    private String invoiceCity;
    @Size(max = 255)
    private String invoiceAddress;
    @Size(max = 64)
    private String invoiceTaxNumber;
    private Boolean onSpot;
    private Boolean cancelled;
    private Boolean presenter;
    private Boolean closed;
    private Boolean etiquette;
    private Long workplaceId;
    private Long countryId;
    @NotNull
    private Long congressId;
}
