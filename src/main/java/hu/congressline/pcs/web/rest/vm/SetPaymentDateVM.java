package hu.congressline.pcs.web.rest.vm;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SetPaymentDateVM {
    private Long id;
    private LocalDate paymentDate;
}
