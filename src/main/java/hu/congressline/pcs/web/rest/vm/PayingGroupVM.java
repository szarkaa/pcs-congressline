package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PayingGroupVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 64)
    private String name;

    @Size(max = 32)
    private String zipCode;

    @Size(max = 64)
    private String city;

    @Size(max = 128)
    private String street;

    @Size(max = 128)
    private String contactName;

    @Size(max = 64)
    private String email;

    @Size(max = 64)
    private String phone;

    @Size(max = 64)
    private String fax;

    @Size(max = 64)
    private String taxNumber;

    @NotNull
    private Long countryId;

    @NotNull
    private Long currencyId;

    @NotNull
    private Long congressId;
}
