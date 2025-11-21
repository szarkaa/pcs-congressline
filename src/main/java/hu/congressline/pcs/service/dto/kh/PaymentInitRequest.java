package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import hu.congressline.pcs.domain.enumeration.Currency;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "merchantId", "orderNo", "dttm", "payOperation", "payMethod", "totalAmount", "currency", "closePayment", "returnUrl", "returnMethod", "cart", "customer",
    "order", "merchantData", "language", "ttlSec" })
public class PaymentInitRequest extends SignBase {

    private String merchantId;
    private String orderNo;
    private String payOperation; //[payment, oneclickPayment, customPayment]
    private String payMethod; //[card, cart#LVP]
    private Long totalAmount;
    private Currency currency;
    private Boolean closePayment = true;
    private List<PaymentCartItem> cart;
    private String returnUrl;
    private String returnMethod; //[GET, POST]
    private PaymentCustomer customer;
    private PaymentOrder order;
    private String merchantData;
    private String language;
    private Integer ttlSec;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDttm() {
        return dttm;
    }

    public void setDttm(String dttm) {
        this.dttm = dttm;
    }

    public String getPayOperation() {
        return payOperation;
    }

    public void setPayOperation(String payOperation) {
        this.payOperation = payOperation;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

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

    public boolean isClosePayment() {
        return closePayment;
    }

    public void setClosePayment(boolean closePayment) {
        this.closePayment = closePayment;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getReturnMethod() {
        return returnMethod;
    }

    public void setReturnMethod(String returnMethod) {
        this.returnMethod = returnMethod;
    }

    public List<PaymentCartItem> getCart() {
        return cart;
    }

    public void setCart(List<PaymentCartItem> cart) {
        this.cart = cart;
    }

    public PaymentCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(PaymentCustomer customer) {
        this.customer = customer;
    }

    public PaymentOrder getOrder() {
        return order;
    }

    public void setOrder(PaymentOrder order) {
        this.order = order;
    }

    public String getMerchantData() {
        return merchantData;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void setMerchantData(String merchantData) {
        if (merchantData == null) {
            this.merchantData = null;
        } else {
            this.merchantData = Base64.getEncoder().encodeToString(merchantData.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getTtlSec() {
        return ttlSec;
    }

    public void setTtlSec(Integer ttlSec) {
        this.ttlSec = ttlSec;
    }

    public String getSignature() {
        return signature;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, merchantId);
        add(sb, orderNo);
        add(sb, dttm);
        add(sb, payOperation);
        add(sb, payMethod);
        add(sb, totalAmount);
        add(sb, currency.toString());
        add(sb, closePayment);
        add(sb, returnUrl);
        add(sb, returnMethod);

        for (PaymentCartItem item : cart) {
            add(sb, item.toSign());
        }

        if (null != customer) {
            add(sb, customer.toSign());
        }
        if (null != order) {
            add(sb, order.toSign());
        }
        add(sb, merchantData);
        add(sb, language);
        add(sb, ttlSec);
        deleteLast(sb);
        return sb.toString();
    }
}
