package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.enumeration.Currency;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PriceService {

    public BigDecimal getVatValue(BigDecimal total, Integer vat, int scale) {
        BigDecimal vatBase = getVatBase(total, vat, scale);
        return total.subtract(vatBase);
    }

    public BigDecimal getVatBase(BigDecimal total, Integer vat, int scale) {
        BigDecimal theVat = new BigDecimal(vat).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        return total.divide(theVat, scale, RoundingMode.HALF_UP);
    }

    public int getScale(ChargeableItem item) {
        return !Currency.HUF.toString().equalsIgnoreCase(item.getChargeableItemCurrency()) ? 2 : 0;
    }

    public int getScale(PayingGroupItem item) {
        return !Currency.HUF.toString().equalsIgnoreCase(item.getPayingGroup().getCurrency().getCurrency()) ? 2 : 0;
    }

}
