package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentAccount {
    private String createdAt; //Date of opening the user's customer account in the e-shop. ISO8061 format is accepted.
    private String changedAt; //The date of the last change of the customer's account in the e-shop, including the type of change of the billing or delivery address.
    // ISO8061 format is accepted.
    private String changedPwdAt; //Date of the last change (or reset) of the user's password to the customer's account in the e-shop. ISO8061 format is accepted.
    private int orderHistory; //The number of times the OneClick template was used for purchases on this customer account in the last 180 days.
    // Minimum allowed value: 0, maximum: 9999.
    private int paymentsDay; //The number of purchases (both successful and unsuccessful) on the customer's account in the last 24 hours, regardless of the payment method.
    // Minimum allowed value: 0, maximum: 999.
    private int paymentsYear; //The number of purchases (both successful and unsuccessful) on the customer's account in the last 365 days, regardless of the payment method.
    // Minimum allowed value: 0, maximum: 999.
    private int oneclickAdds; //The number of attempts to create a OneClick template in a customer's account in the last 24 hours. Minimum allowed value: 0, maximum: 999.
    private boolean suspicious; //Indication of suspicious activity of the user or customer account in the e-shop for the entire history of the customer account,
    // including previous (successful or stopped) fraud attempts.

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(String changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedPwdAt() {
        return changedPwdAt;
    }

    public void setChangedPwdAt(String changedPwdAt) {
        this.changedPwdAt = changedPwdAt;
    }

    public int getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(int orderHistory) {
        this.orderHistory = orderHistory;
    }

    public int getPaymentsDay() {
        return paymentsDay;
    }

    public void setPaymentsDay(int paymentsDay) {
        this.paymentsDay = paymentsDay;
    }

    public int getPaymentsYear() {
        return paymentsYear;
    }

    public void setPaymentsYear(int paymentsYear) {
        this.paymentsYear = paymentsYear;
    }

    public int getOneclickAdds() {
        return oneclickAdds;
    }

    public void setOneclickAdds(int oneclickAdds) {
        this.oneclickAdds = oneclickAdds;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

}
