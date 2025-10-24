package hu.congressline.pcs.service.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.enumeration.VatRateType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static hu.congressline.pcs.domain.enumeration.VatRateType.REGULAR;

@SuppressWarnings("checkstyle:HideUtilityClassConstructorCheck")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceUtil {

    @SuppressWarnings("MissingJavadocMethod")
    public static String normalizeForFilename(String string) {
        String result = StringUtils.strip(StringUtils.stripAccents(string.toLowerCase()).replaceAll("[^a-zA-Z0-9]", "-"), "-");
        while (true) {
            final String doubleDash = "--";
            if (!result.contains(doubleDash)) {
                break;
            }
            result = result.replaceAll(doubleDash, "-");
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Map<String, ItemRowByVat> getItemRowsByVat(final List<InvoiceItem> itemList) {
        final Map<String, ItemRowByVat> retVal = new LinkedHashMap<>();
        // zero vat items
        itemList.stream().filter(item -> !REGULAR.equals(item.getVatRateType())).map(InvoiceItem::getVatName).distinct().sorted().forEach(vatName -> {
            ItemRowByVat itemRow = new ItemRowByVat();
            itemRow.vat = 0;
            itemRow.vatBase = itemList.stream().filter(item -> item.getVatName().equals(vatName)).map(InvoiceItem::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.vatValue = itemList.stream().filter(item -> item.getVatName().equals(vatName)).map(InvoiceItem::getVatValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.total = itemList.stream().filter(item -> item.getVatName().equals(vatName)).map(InvoiceItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            retVal.put(vatName, itemRow);
        });

        // non zero vat items
        itemList.stream().filter(item -> REGULAR.equals(item.getVatRateType())).map(InvoiceItem::getVat).distinct().sorted().forEach(vat -> {
            ItemRowByVat itemRow = new ItemRowByVat();
            itemRow.vat = vat;
            itemRow.vatBase = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.vatValue = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getVatValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.total = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            retVal.put(vat.toString(), itemRow);
        });
        return retVal;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Map<VatRateType, List<ItemRowByVat>> getItemRowsByVatForNav(final List<InvoiceItem> itemList) {
        final Map<VatRateType, List<ItemRowByVat>> retVal = new LinkedHashMap<>();

        itemList.stream().map(InvoiceItem::getVatRateType).filter(vatRateType -> !vatRateType.equals(REGULAR)).distinct().sorted().forEach(vatRateType -> {
            ItemRowByVat itemRow = new ItemRowByVat();
            itemRow.vatBase = itemList.stream().filter(item -> item.getVatRateType().equals(vatRateType)).map(InvoiceItem::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.total = itemRow.vatBase;
            retVal.put(vatRateType, new ArrayList<>());
            retVal.get(vatRateType).add(itemRow);
        });

        retVal.put(REGULAR, new ArrayList<>());
        itemList.stream().filter(item -> item.getVatRateType().equals(REGULAR)).map(InvoiceItem::getVat).distinct().sorted().forEach(vat -> {
            ItemRowByVat itemRow = new ItemRowByVat();
            itemRow.vat = vat;
            itemRow.vatBase = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.vatValue = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getVatValue).reduce(BigDecimal.ZERO, BigDecimal::add);
            itemRow.total = itemList.stream().filter(item -> item.getVat().equals(vat)).map(InvoiceItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            retVal.get(REGULAR).add(itemRow);
        });
        return retVal;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRowByVat {
        private Integer vat = 0;
        private BigDecimal vatBase;
        private BigDecimal vatValue;
        private BigDecimal total;
    }

}
