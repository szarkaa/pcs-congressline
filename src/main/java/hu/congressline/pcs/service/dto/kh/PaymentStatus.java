package hu.congressline.pcs.service.dto.kh;

import java.util.Arrays;

public enum PaymentStatus {

    PAYMENT_INITIATED(1),
    PAYMENT_IN_PROGRESS(2),
    PAYMENT_CANCELLED(3),
    PAYMENT_CONFIRMED(4),
    PAYMENT_REVERSED(5),
    PAYMENT_DENIED(6),
    PAYMENT_WAITING_FOR_SETTLEMENT(7),
    PAYMENT_SETTLED(8),
    PAYMENT_REFUND_PROCESSING(9),
    PAYMENT_RETURNED(10);

    private final int paymentStatus;

    PaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static PaymentStatus getByCode(int paymentStatus) {
        return Arrays.stream(values())
            .filter(ps -> ps.paymentStatus == paymentStatus)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("PaymentStatus code is not valid!"));
    }

}
