package hu.congressline.pcs.service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.InvoiceChargeRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.service.pdf.PdfContext;
import hu.congressline.pcs.service.util.PcsNumberFormatter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public abstract class AbstractPdfService {

    protected InvoiceItemRepository invoiceItemRepository;
    protected InvoiceChargeRepository invoiceChargeRepository;
    protected DiscountService discountService;
    protected MessageSource messageSource;
    protected PcsNumberFormatter formatter;

    @PostConstruct
    public void init() {
        this.formatter = new PcsNumberFormatter();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal sumPricesWithDiscounts(List<Long> ignoredChargeableItemIdList, List<? extends ChargeableItem>... lists) {
        BigDecimal retVal = BigDecimal.ZERO;
        for (List<? extends ChargeableItem> list : lists) {
            for (ChargeableItem item : list) {
                if (item.getChargeableItemPrice() != null && !ignoredChargeableItemIdList.contains(item.getId())) {
                    retVal = retVal.add(discountService.getPriceWithDiscount(item));
                }
            }
        }
        return retVal;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal sumPricesWithDiscounts(List<InvoiceItem> list) {
        BigDecimal retVal = BigDecimal.ZERO;
        for (InvoiceItem item : list) {
            retVal = retVal.add(item.getTotal());
        }
        return retVal;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<InvoiceItem> getSortedInvoiceItemRows(ChargeableItemType itemType, List<InvoiceItem> invoiceItemList) {
        final List<InvoiceItem> list = invoiceItemList.stream()
                .filter(invoiceItem -> invoiceItem.getItemType().equals(itemType))
                .sorted(Comparator.comparing(InvoiceItem::getId))
                .collect(Collectors.toList());
        return list;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Map<String, List<InvoiceItem>> getSortedChargeableItemRows(ChargeableItemType itemType, List<InvoiceItem> invoiceItemList) {
        final Map<String, List<InvoiceItem>> retVal = new HashMap<>();
        invoiceItemList.stream().filter(invoiceItem -> invoiceItem.getItemType().equals(itemType)).forEach(item -> {
            String numberSZJ = item.getSzj() != null ? item.getSzj() : "";
            if (retVal.get(numberSZJ) == null) {
                List<InvoiceItem> list = new ArrayList<>();
                list.add(item);
                retVal.put(numberSZJ, list);
            } else {
                List<InvoiceItem> list = retVal.get(numberSZJ);
                list.add(item);
                retVal.put(numberSZJ, list);
            }
        });
        return retVal;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public boolean hasAnyNonIgnoredItem(List<? extends ChargeableItem> chargableItemList, List<Long> ignoredList) {
        for (ChargeableItem chargeableItem : chargableItemList) {
            if (!ignoredList.contains(chargeableItem.getId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public boolean containsNonPrePayedItem(List<InvoiceItem> items) {
        for (InvoiceItem item : items) {
            if (item.getTotal().compareTo(BigDecimal.ZERO) != 0) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Map<Integer, InvoiceItemSummary> getSortedInvoiceItemRowsByVAT(List<InvoiceItem> invoiceItemList) {
        Map<Integer, InvoiceItemSummary> retVal = new HashMap<>();
        invoiceItemList.stream().forEach(item -> {
            if (retVal.get(item.getVat()) == null) {
                retVal.put(item.getVat(), new InvoiceItemSummary(item));
            } else {
                InvoiceItemSummary summary = retVal.get(item.getVat());
                summary.add(item);
                retVal.put(item.getVat(), summary);
            }
        });

        return retVal;
    }

    public String getMessage(String messageKey, Locale locale, Object... parameters) {
        return messageSource.getMessage(messageKey, parameters, locale);
    }

    protected PdfPTable createTable(int columns, int widthPercentage, float[] widths) throws DocumentException {
        PdfPTable table = new PdfPTable(columns);
        table.setWidthPercentage(widthPercentage);
        table.setWidths(widths);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);
        return table;
    }

    protected void addTableCell(PdfPTable table, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            table.addCell(cell);
        }
    }

    protected void setBorder(int border, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            cell.setBorder(border);
        }
    }

    protected void setPaddingTop(int topSize, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            cell.setPaddingTop(topSize);
        }
    }

    protected void setPaddingBottom(int bottonSize, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            cell.setPaddingBottom(bottonSize);
        }
    }

    protected Paragraph createParagraph(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        return p;
    }

    protected Paragraph createParagraphWithMessage(String messageId, Font font, Locale locale) {
        return createParagraph(getMessage(messageId, locale), font);
    }

    protected Paragraph createRightParagraph(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Paragraph.ALIGN_RIGHT);
        return p;
    }

    protected Paragraph createCenterParagraph(String text, Font font, Locale locale) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        return p;
    }

    protected Paragraph createRightParagraphWithMessage(String messageId, Font font, Locale locale) {
        return createRightParagraph(getMessage(messageId, locale), font);
    }

    protected PdfPCell createCell(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(paragraph);
        cell.setPaddingTop(0);
        cell.setPaddingBottom(0);
        cell.setPaddingRight(0);
        cell.setPaddingLeft(0);
        cell.setBorder(0);
        return cell;
    }

    protected void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    protected BigDecimal getInvoiceItemsSumAmount(List<InvoiceItem> list) {
        return list.stream().map(InvoiceItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    abstract byte[] generatePdf(PdfContext context);

    @Getter
    protected static class InvoiceItemSummary {
        private final String szj;
        private final String vatName;
        private final Integer vat;
        private BigDecimal vatBase;
        private BigDecimal vatValue;
        private BigDecimal total;

        public InvoiceItemSummary(InvoiceItem item) {
            this.szj = item.getSzj();
            this.vat = item.getVat();
            this.vatName = item.getVatName();
            this.vatBase = item.getVatBase();
            this.vatValue = item.getVatValue();
            this.total = item.getTotal();
        }

        public void add(InvoiceItem item) {
            if (!vat.equals(item.getVat())) {
                throw new IllegalStateException("Vat is not the same!");
            }

            vatBase = vatBase.add(item.getVatBase());
            vatValue = vatValue.add(item.getVatValue());
            total = total.add(item.getTotal());
        }
    }
}
