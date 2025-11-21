package hu.congressline.pcs.service.dto.kh;

public class PaymentReverseRequest extends SignBase {
    private String merchantId;
    private String payId;

    public PaymentReverseRequest() {
    }

    public PaymentReverseRequest(String merchantId, String payId) {
        this.merchantId = merchantId;
        this.payId = payId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getPayId() {
        return payId;
    }

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, merchantId);
        add(sb, payId);
        add(sb, dttm);
        deleteLast(sb);
        return sb.toString();
    }
}
