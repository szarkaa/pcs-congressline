package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRefundResult extends SignBase {
    @NotNull
    private String payId;
    @NotNull
    private Integer resultCode;
    @NotNull
    private String resultMessage;
    @NotNull
    private Integer paymentStatus;
    private String authCode;
    private String statusDetail;

    public PaymentRefundResult() {
    }

    public PaymentRefundResult(String payId, Integer resultCode, String resultMessage, Integer paymentStatus) {
        this.payId = payId;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.paymentStatus = paymentStatus;
    }

    public PaymentRefundResult(String payId, Integer resultCode, String resultMessage, Integer paymentStatus, String authCode, String statusDetail) {
        this.payId = payId;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.paymentStatus = paymentStatus;
        this.authCode = authCode;
        this.statusDetail = statusDetail;
    }

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, getPayId());
        add(sb, getDttm());
        add(sb, getResultCode());
        add(sb, getResultMessage());
        add(sb, paymentStatus);
        add(sb, getAuthCode());
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

    public String getStatusDetail() {
        return statusDetail;
    }

}
