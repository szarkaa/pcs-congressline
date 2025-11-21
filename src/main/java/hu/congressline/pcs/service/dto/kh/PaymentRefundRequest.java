package hu.congressline.pcs.service.dto.kh;

public class PaymentRefundRequest extends SignBase {
    private String merchantId;
    private String payId;
    private Long amount;

    public PaymentRefundRequest() {
    }

    public PaymentRefundRequest(String merchantId, String payId) {
        this.merchantId = merchantId;
        this.payId = payId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getPayId() {
        return payId;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, merchantId);
        add(sb, payId);
        add(sb, dttm);
        add(sb, amount);
        deleteLast(sb);
        return sb.toString();
    }
}
