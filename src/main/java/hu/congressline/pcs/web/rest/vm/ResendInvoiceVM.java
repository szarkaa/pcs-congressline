package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResendInvoiceVM {
    @NotNull
    private String email;
    @NotNull
    private Long invoiceId;
}
