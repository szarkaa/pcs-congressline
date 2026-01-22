package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;

import hu.congressline.pcs.domain.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class CurrencyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String currency;

    public CurrencyDTO(@NonNull Currency currency) {
        this.id = currency.getId();
        this.currency = currency.getCurrency();
    }

    @Override
    public String toString() {
        return "CurrencyDTO{" + "id=" + id + "}";
    }

}
