package hu.congressline.pcs.service.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("checkstyle:HideUtilityClassConstructorCheck")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

    public static final String DATE_FORMAT_EN = "dd/MMM/yyyy";
    public static final String DATE_FORMAT_HU = "yyyy-MM-dd";

    public static String getPaymentDateNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return formatter.format(LocalDateTime.now());
    }

}
