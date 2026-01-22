package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WorkplaceVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull
    @Size(max = 255)
    private String name;
    private String vatRegNumber;
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
    private String fax;
    @Size(max = 64)
    private String email;
    private Long countryId;
    private String countryName;
    private Long congressId;
}
