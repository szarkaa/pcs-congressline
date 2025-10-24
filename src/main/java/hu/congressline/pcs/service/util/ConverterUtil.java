package hu.congressline.pcs.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructorCheck")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterUtil {

    public static BigDecimal getBigDecimalValue(Object object) {
        return getBigDecimalValue(object, 2);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static BigDecimal getBigDecimalValue(Object object, int scale) {
        if (object == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal value = (BigDecimal) object;
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

}
