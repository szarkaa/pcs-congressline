package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOrder extends ApiBase implements Signable {
    private String type; //[purchase, balance, prepaid, cash, check]
    private String availability;
    private String delivery; //[shipping, shipping_verified, instore, digital, ticket, order]
    private String deliveryMode; //[0, 1, 2, 3]
    private String deliveryEmail;
    private boolean nameMatch;
    private boolean addressMatch;
    private PaymentAddress billing;
    private PaymentAddress shipping;
    private String shippingAddedAt;
    private boolean reorder;
    private GiftCards giftcards;

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, type);
        add(sb, availability);
        add(sb, delivery);
        add(sb, deliveryMode);
        add(sb, deliveryEmail);
        add(sb, nameMatch);
        add(sb, addressMatch);
        if (billing != null) {
            add(sb, billing.toSign());
        }
        if (shipping != null) {
            add(sb, shipping.toSign());
        }
        add(sb, shippingAddedAt);
        add(sb, reorder);
        if (giftcards != null) {
            add(sb, giftcards.toSign());
        }
        deleteLast(sb);
        return sb.toString();
    }

    @Getter
    @Setter
    public static class GiftCards extends ApiBase implements Signable {
        private long totalAmount;
        private String currency;
        private int quantity;

        @Override
        public String toSign() {
            StringBuilder sb = new StringBuilder();
            add(sb, totalAmount);
            add(sb, currency);
            add(sb, quantity);
            deleteLast(sb);
            return sb.toString();
        }
    }
}
