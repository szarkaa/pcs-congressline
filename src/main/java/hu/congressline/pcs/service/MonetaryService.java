package hu.congressline.pcs.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.NonNull;

public interface MonetaryService {

    default BigDecimal roundUp(@NonNull BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    default BigDecimal roundUp(@NonNull BigDecimal value, int decimal) {
        return value.setScale(decimal, RoundingMode.HALF_UP);
    }
}
