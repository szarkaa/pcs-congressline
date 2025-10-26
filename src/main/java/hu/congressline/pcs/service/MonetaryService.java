package hu.congressline.pcs.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.NonNull;

public interface MonetaryService {

    default BigDecimal roundUp(@NonNull BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
