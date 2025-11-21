package hu.congressline.pcs.service.dto.kh;

import jakarta.validation.constraints.NotNull;

public class PaymentInitResult extends SignBase {
    @NotNull
    private String payId;
    @NotNull
    private Integer resultCode;
    @NotNull
    private String resultMessage;
    private Integer paymentStatus;
    private String authCode;
    private String customerCode;
    private String statusDetail;

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, getPayId());
        add(sb, getDttm());
        add(sb, getResultCode());
        add(sb, getResultMessage());
        add(sb, getPaymentStatus());
        add(sb, getAuthCode());
        add(sb, getCustomerCode());
        add(sb, getStatusDetail());
        deleteLast(sb);
        return sb.toString();
    }

    public String getPayId() {
        return payId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

}
