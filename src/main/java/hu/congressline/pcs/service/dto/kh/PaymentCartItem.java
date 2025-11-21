package hu.congressline.pcs.service.dto.kh;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "name", "quantity", "amount", "description" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCartItem extends ApiBase implements Signable {
    private String name;
    private int quantity;
    private long amount;
    private String description;

    public PaymentCartItem(String name, int quantity, long amount) {
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public PaymentCartItem(String name, int quantity, long amount, String description) {
        this(name, quantity, amount);
        this.description = description;
    }

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, name);
        add(sb, quantity);
        add(sb, amount);
        add(sb, description);
        deleteLast(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "PaymentCartItem{" + "name='" + name + '\'' + ", quantity=" + quantity + ", amount=" + amount + ", description='" + description + '\'' + '}';
    }
}
