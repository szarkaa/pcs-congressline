package hu.congressline.pcs.service.dto.kh;

import hu.congressline.pcs.domain.enumeration.Currency;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class PaymentOrderGiftcard {
    private long totalAmount; //The total sum of the values of gift or prepaid cards in minor units that are included in the purchase in the given currency.
    private Currency currency;
    @Min(1)
    @Max(99)
    private int quantity;

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
