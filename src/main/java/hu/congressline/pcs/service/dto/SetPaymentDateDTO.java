package hu.congressline.pcs.service.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SetPaymentDateDTO {
    private Long id;
    private LocalDate paymentDate;
}
