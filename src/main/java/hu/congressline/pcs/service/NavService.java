package hu.congressline.pcs.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoiceNavValidation;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationSeverity;
import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationType;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.InvoiceNavValidationRepository;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.gov.nav.schemas.ntca._1_0.common.BasicHeaderType;
import hu.gov.nav.schemas.ntca._1_0.common.BasicRequestType;
import hu.gov.nav.schemas.ntca._1_0.common.CryptoType;
import hu.gov.nav.schemas.ntca._1_0.common.ObjectFactory;
import hu.gov.nav.schemas.ntca._1_0.common.UserHeaderType;
import hu.gov.nav.schemas.osa._3_0.api.BasicOnlineInvoiceRequestType;
import hu.gov.nav.schemas.osa._3_0.api.GeneralErrorResponse;
import hu.gov.nav.schemas.osa._3_0.api.InvoiceOperationListType;
import hu.gov.nav.schemas.osa._3_0.api.InvoiceOperationType;
import hu.gov.nav.schemas.osa._3_0.api.ManageInvoiceOperationType;
import hu.gov.nav.schemas.osa._3_0.api.ManageInvoiceRequest;
import hu.gov.nav.schemas.osa._3_0.api.ManageInvoiceResponse;
import hu.gov.nav.schemas.osa._3_0.api.ProcessingResultType;
import hu.gov.nav.schemas.osa._3_0.api.QueryTransactionStatusRequest;
import hu.gov.nav.schemas.osa._3_0.api.QueryTransactionStatusResponse;
import hu.gov.nav.schemas.osa._3_0.api.SoftwareOperationType;
import hu.gov.nav.schemas.osa._3_0.api.SoftwareType;
import hu.gov.nav.schemas.osa._3_0.api.TokenExchangeRequest;
import hu.gov.nav.schemas.osa._3_0.api.TokenExchangeResponse;
import hu.gov.nav.schemas.osa._3_0.base.AddressType;
import hu.gov.nav.schemas.osa._3_0.base.InvoiceAppearanceType;
import hu.gov.nav.schemas.osa._3_0.base.InvoiceCategoryType;
import hu.gov.nav.schemas.osa._3_0.base.PaymentMethodType;
import hu.gov.nav.schemas.osa._3_0.base.SimpleAddressType;
import hu.gov.nav.schemas.osa._3_0.base.TaxNumberType;
import hu.gov.nav.schemas.osa._3_0.data.CustomerInfoType;
import hu.gov.nav.schemas.osa._3_0.data.CustomerTaxNumberType;
import hu.gov.nav.schemas.osa._3_0.data.CustomerVatDataType;
import hu.gov.nav.schemas.osa._3_0.data.CustomerVatStatusType;
import hu.gov.nav.schemas.osa._3_0.data.DetailedReasonType;
import hu.gov.nav.schemas.osa._3_0.data.InvoiceData;
import hu.gov.nav.schemas.osa._3_0.data.InvoiceDetailType;
import hu.gov.nav.schemas.osa._3_0.data.InvoiceHeadType;
import hu.gov.nav.schemas.osa._3_0.data.InvoiceMainType;
import hu.gov.nav.schemas.osa._3_0.data.LineAmountsNormalType;
import hu.gov.nav.schemas.osa._3_0.data.LineGrossAmountDataType;
import hu.gov.nav.schemas.osa._3_0.data.LineModificationReferenceType;
import hu.gov.nav.schemas.osa._3_0.data.LineNetAmountDataType;
import hu.gov.nav.schemas.osa._3_0.data.LineOperationType;
import hu.gov.nav.schemas.osa._3_0.data.LineType;
import hu.gov.nav.schemas.osa._3_0.data.LineVatDataType;
import hu.gov.nav.schemas.osa._3_0.data.LinesType;
import hu.gov.nav.schemas.osa._3_0.data.SummaryByVatRateType;
import hu.gov.nav.schemas.osa._3_0.data.SummaryGrossDataType;
import hu.gov.nav.schemas.osa._3_0.data.SummaryNormalType;
import hu.gov.nav.schemas.osa._3_0.data.SummaryType;
import hu.gov.nav.schemas.osa._3_0.data.SupplierInfoType;
import hu.gov.nav.schemas.osa._3_0.data.UnitOfMeasureType;
import hu.gov.nav.schemas.osa._3_0.data.VatRateGrossDataType;
import hu.gov.nav.schemas.osa._3_0.data.VatRateNetDataType;
import hu.gov.nav.schemas.osa._3_0.data.VatRateType;
import hu.gov.nav.schemas.osa._3_0.data.VatRateVatDataType;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.domain.enumeration.Currency.HUF;
import static hu.congressline.pcs.domain.enumeration.NavVatCategory.PRIVATE_PERSON;
import static hu.congressline.pcs.domain.enumeration.VatRateType.REGULAR;
import static hu.congressline.pcs.service.util.NavOnlineUtil.SHA3_512;
import static hu.congressline.pcs.service.util.NavOnlineUtil.convertLocalDateToDate;
import static hu.congressline.pcs.service.util.NavOnlineUtil.decryptAES128ECB;
import static hu.congressline.pcs.service.util.NavOnlineUtil.getStrippedUtcDate;
import static hu.congressline.pcs.service.util.NavOnlineUtil.getUtcDate;
import static hu.congressline.pcs.service.util.NavOnlineUtil.getXmlLocalDate;
import static hu.congressline.pcs.service.util.NavOnlineUtil.getXmlUtcDate;
import static hu.congressline.pcs.service.util.NavOnlineUtil.hashWithSHA3_512;
import static hu.congressline.pcs.service.util.NavOnlineUtil.logXmlAsString;
import static hu.congressline.pcs.service.util.NavOnlineUtil.marsallInvoice;
import static java.math.BigDecimal.ZERO;

@Slf4j
@RequiredArgsConstructor
@Service
public class NavService implements MonetaryService {
    private static final String OK = "OK";
    private static final String SUCCESSFUL_NAV_ONLINE_POST_REQUEST = "Successful nav online post request processing, response code: {} {}";
    private static final String ERROR_NAV_ONLINE_POST_REQUEST = "Error during nav online post request processing, response code: {} {}";
    private static final String ERROR_NAV_ONLINE_EXCHANGE_TOKEN_POST = "Error during exchange token post!";

    private final ApplicationProperties properties;
    private final CompanyService companyService;
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceService invoiceService;
    private final InvoiceNavValidationRepository invoiceNavValidationRepository;

    private ObjectFactory commonFactory;
    private hu.gov.nav.schemas.osa._3_0.base.ObjectFactory baseFactory;
    private hu.gov.nav.schemas.osa._3_0.data.ObjectFactory dataFactory;
    private hu.gov.nav.schemas.osa._3_0.api.ObjectFactory apiFactory;

    @PostConstruct
    public void init() {
        commonFactory = new ObjectFactory();
        baseFactory = new hu.gov.nav.schemas.osa._3_0.base.ObjectFactory();
        dataFactory = new hu.gov.nav.schemas.osa._3_0.data.ObjectFactory();
        apiFactory = new hu.gov.nav.schemas.osa._3_0.api.ObjectFactory();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public QueryTransactionStatusResponse checkTransactionStatus(Long invoiceId) {
        final Invoice invoice = invoiceService.getById(invoiceId);
        final QueryTransactionStatusRequest request = createQueryTransactionStatusRequest(invoice);
        generateRequestSignature(request);
        final QueryTransactionStatusResponse response = postQueryTransactionStatusRequest(request);
        log.debug("Invoice query status for id: {} status: {}", invoiceId, response.getProcessingResults().getProcessingResult().getFirst().getInvoiceStatus());
        return response;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Invoice postInvoiceToNav(Long invoiceId) {
        final Invoice invoice = invoiceService.getById(invoiceId);
        final TokenExchangeResponse exchangeResponse = postExchangeTokenToNav();
        final byte[] encodedExchangeToken = exchangeResponse.getEncodedExchangeToken();
        final String exchangeToken = decryptAES128ECB(encodedExchangeToken, properties.getNav().getUser().getChangeKey());
        final ManageInvoiceRequest invoiceRequest = createManageInvoiceRequest(invoice);
        invoiceRequest.setExchangeToken(exchangeToken);
        generateRequestSignature(invoiceRequest);
        final ManageInvoiceResponse manageInvoiceResponse = postManageInvoice(invoiceRequest);
        String trxId = manageInvoiceResponse.getTransactionId();
        log.debug("Invoice successfully posted to nav with trx id: {}", trxId);

        invoice.setNavTrxId(trxId);
        invoice.setNavStatus(InvoiceNavStatus.SENT);
        return invoiceService.save(invoice);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public void checkPendingInvoiceNavStatus() {
        final List<Invoice> invoices = invoiceService.findAllByNavStatus(Arrays.asList(InvoiceNavStatus.SENT, InvoiceNavStatus.RECEIVED, InvoiceNavStatus.PROCESSING));
        for (Invoice invoice : invoices) {
            final QueryTransactionStatusResponse response = checkTransactionStatus(invoice.getId());
            invoiceNavValidationRepository.deleteByInvoice(invoice);
            final ProcessingResultType processingResultType = response.getProcessingResults().getProcessingResult().getFirst();
            processingResultType.getTechnicalValidationMessages().forEach(message -> {
                InvoiceNavValidation validation = new InvoiceNavValidation();
                validation.setInvoice(invoice);
                validation.setValidationType(InvoiceNavValidationType.TECHNICAL);
                validation.setValidationSeverity(InvoiceNavValidationSeverity.valueOf(message.getValidationResultCode().value()));
                validation.setErrorCode(message.getValidationErrorCode());
                validation.setMessage(message.getMessage());
                invoiceNavValidationRepository.save(validation);
            });

            processingResultType.getBusinessValidationMessages().forEach(message -> {
                InvoiceNavValidation validation = new InvoiceNavValidation();
                validation.setInvoice(invoice);
                validation.setValidationType(InvoiceNavValidationType.BUSINESS);
                validation.setValidationSeverity(InvoiceNavValidationSeverity.valueOf(message.getValidationResultCode().value()));
                validation.setErrorCode(message.getValidationErrorCode());
                validation.setMessage(message.getMessage());
                invoiceNavValidationRepository.save(validation);
            });
            invoice.setNavStatus(InvoiceNavStatus.valueOf(processingResultType.getInvoiceStatus().value()));
            invoiceService.save(invoice);
        }

    }

    @SuppressWarnings("MissingJavadocMethod")
    public String createInvoiceDataXml(Invoice invoice) {
        return marsallInvoice(createInvoiceData(invoice));
    }

    private TokenExchangeResponse postExchangeTokenToNav() {
        final TokenExchangeRequest tokenExchangeRequest = createTokenExchangeRequest();
        generateRequestSignature(tokenExchangeRequest);
        return postExchangeToken(tokenExchangeRequest);
    }

    private QueryTransactionStatusRequest createQueryTransactionStatusRequest(Invoice invoice) {
        QueryTransactionStatusRequest request = apiFactory.createQueryTransactionStatusRequest();
        addBasicRequestType(request);
        addSoftwareInfo(request);
        request.setTransactionId(invoice.getNavTrxId());
        return request;
    }

    private ManageInvoiceRequest createManageInvoiceRequest(Invoice invoice) {
        ManageInvoiceRequest request = apiFactory.createManageInvoiceRequest();
        addBasicRequestType(request);
        addSoftwareInfo(request);
        final InvoiceData invoiceData = createInvoiceData(invoice);
        InvoiceOperationListType invoiceOperations = apiFactory.createInvoiceOperationListType();
        InvoiceOperationType invoiceOperation = apiFactory.createInvoiceOperationType();
        invoiceOperation.setIndex(1);
        invoiceOperation.setInvoiceOperation(invoice.getStorno() ? ManageInvoiceOperationType.STORNO : ManageInvoiceOperationType.CREATE);
        invoiceOperation.setInvoiceData(marsallInvoice(invoiceData).getBytes(StandardCharsets.UTF_8));
        invoiceOperations.getInvoiceOperation().add(invoiceOperation);
        request.setInvoiceOperations(invoiceOperations);
        return request;
    }

    private SummaryType createInvoiceSummary(List<InvoiceItem> itemList) {
        final SummaryType summary = dataFactory.createSummaryType();
        final SummaryNormalType summaryNormal = dataFactory.createSummaryNormalType();
        final InvoiceItem invoiceItem = itemList.stream().findFirst().orElse(null);
        final Currency currency = Currency.parse(invoiceItem.getCurrency().toUpperCase());
        final BigDecimal exchangeRate = invoiceItem.getInvoice().getExchangeRate();
        final Map<hu.congressline.pcs.domain.enumeration.VatRateType, List<ServiceUtil.ItemRowByVat>> itemRowsByVatMap = ServiceUtil.getItemRowsByVatForNav(itemList);
        itemRowsByVatMap.keySet().forEach(rateType -> {
            List<ServiceUtil.ItemRowByVat> itemRowList = itemRowsByVatMap.get(rateType);
            itemRowList.forEach(itemRow -> {
                SummaryByVatRateType summaryByVatRate = dataFactory.createSummaryByVatRateType();
                if (!REGULAR.equals(rateType)) {
                    summaryByVatRate.setVatRate(createVatRateType(rateType, null,
                            itemList.stream().filter(item -> item.getVatRateType().equals(rateType)).findFirst().map(InvoiceItem::getVatExceptionReason)
                                .orElse("Vat exception reason")));
                } else {
                    summaryByVatRate.setVatRate(createVatRateType(rateType, itemRow.getVat(), null));
                }
                final VatRateNetDataType vatRateNetDataType = dataFactory.createVatRateNetDataType();
                vatRateNetDataType.setVatRateNetAmount(itemRow.getVatBase());
                vatRateNetDataType.setVatRateNetAmountHUF(currency.equals(HUF) ? itemRow.getVatBase() :
                    roundUp(itemRow.getVatBase().multiply(exchangeRate), 2));
                summaryByVatRate.setVatRateNetData(vatRateNetDataType);

                final VatRateVatDataType vatRateVatDataType = dataFactory.createVatRateVatDataType();
                vatRateVatDataType.setVatRateVatAmount(!REGULAR.equals(rateType) ? ZERO : itemRow.getVatValue());
                final BigDecimal amount = !REGULAR.equals(rateType) ? ZERO : currency.equals(HUF) ? itemRow.getVatValue()
                    : roundUp(itemRow.getVatValue().multiply(exchangeRate), 2);
                vatRateVatDataType.setVatRateVatAmountHUF(amount);
                summaryByVatRate.setVatRateVatData(vatRateVatDataType);

                final VatRateGrossDataType vatRateGrossDataType = dataFactory.createVatRateGrossDataType();
                vatRateGrossDataType.setVatRateGrossAmount(itemRow.getTotal());
                vatRateGrossDataType.setVatRateGrossAmountHUF(currency.equals(HUF) ? itemRow.getTotal() : roundUp(itemRow.getTotal().multiply(exchangeRate), 2));
                summaryByVatRate.setVatRateGrossData(vatRateGrossDataType);
                summaryNormal.getSummaryByVatRate().add(summaryByVatRate);
            });
        });

        final BigDecimal invoiceNetAmount = itemRowsByVatMap.values().stream()
            .flatMap(Collection::stream)
            .map(ServiceUtil.ItemRowByVat::getVatBase)
            .reduce(ZERO, BigDecimal::add);
        final BigDecimal invoiceVatAmount = itemRowsByVatMap.values().stream()
            .flatMap(Collection::stream)
            .map(ServiceUtil.ItemRowByVat::getVatValue)
            .reduce(ZERO, BigDecimal::add);
        summaryNormal.setInvoiceNetAmount(invoiceNetAmount);
        summaryNormal.setInvoiceNetAmountHUF(currency.equals(HUF) ? invoiceNetAmount : roundUp(invoiceNetAmount.multiply(exchangeRate), 2));
        summaryNormal.setInvoiceVatAmount(invoiceVatAmount);
        summaryNormal.setInvoiceVatAmountHUF(currency.equals(HUF) ? invoiceVatAmount : roundUp(invoiceVatAmount.multiply(exchangeRate), 2));
        summary.setSummaryNormal(summaryNormal);

        final SummaryGrossDataType summaryGrossDataType = dataFactory.createSummaryGrossDataType();
        final BigDecimal invoiceGrossAmount = itemList.stream().map(InvoiceItem::getTotal).reduce(ZERO, BigDecimal::add);
        summaryGrossDataType.setInvoiceGrossAmount(invoiceGrossAmount);
        final BigDecimal grossAmount = currency.equals(HUF) ? invoiceGrossAmount : roundUp(invoiceGrossAmount.multiply(exchangeRate), 2);
        summaryGrossDataType.setInvoiceGrossAmountHUF(grossAmount);
        summary.setSummaryGrossData(summaryGrossDataType);
        return summary;
    }

    private LinesType createInvoiceLines(List<InvoiceItem> items) {
        LinesType lines = dataFactory.createLinesType();
        IntStream.range(0, items.size()).forEach(idx -> {
            InvoiceItem item = items.get(idx);
            LineType line = dataFactory.createLineType();
            line.setLineNumber(BigInteger.valueOf(idx + 1));
            line.setLineDescription(item.getItemName());

            if (item.getInvoice().getStorno()) {
                LineModificationReferenceType lmrt = new LineModificationReferenceType();
                lmrt.setLineNumberReference(BigInteger.valueOf(line.getLineNumber().intValue() + items.size()));
                lmrt.setLineOperation(LineOperationType.CREATE);
                line.setLineModificationReference(lmrt);
            }

            line.setQuantity(new BigDecimal(item.getUnit()));
            if (item.getUnitOfMeasure() != null) {
                line.setUnitOfMeasureOwn(item.getUnitOfMeasure());
                line.setLineExpressionIndicator(false);
            } else {
                line.setLineExpressionIndicator(true);
                line.setUnitOfMeasure(UnitOfMeasureType.PIECE);
            }
            line.setUnitPrice(item.getVatBase().divide(new BigDecimal(item.getUnit()), RoundingMode.HALF_UP));
            LineAmountsNormalType lineAmountsNormal = dataFactory.createLineAmountsNormalType();
            BigDecimal exchangeRate = item.getInvoice().getExchangeRate();
            final LineNetAmountDataType lineNetAmountDataType = dataFactory.createLineNetAmountDataType();
            lineNetAmountDataType.setLineNetAmount(item.getVatBase());
            lineNetAmountDataType.setLineNetAmountHUF(item.getCurrency().equalsIgnoreCase(HUF.toString()) ? item.getVatBase()
                : roundUp(item.getVatBase().multiply(exchangeRate), 2));
            lineAmountsNormal.setLineNetAmountData(lineNetAmountDataType);
            lineAmountsNormal.setLineVatRate(createVatRateType(item.getVatRateType(), item.getVat(), item.getVatExceptionReason()));

            final LineVatDataType lineVatDataType = dataFactory.createLineVatDataType();
            lineVatDataType.setLineVatAmount(item.getVatValue());
            lineVatDataType.setLineVatAmountHUF(item.getCurrency().equalsIgnoreCase(HUF.toString()) ? item.getVatValue()
                : roundUp(item.getVatValue().multiply(exchangeRate), 2));
            lineAmountsNormal.setLineVatData(lineVatDataType);
            final LineGrossAmountDataType lineGrossAmountDataType = dataFactory.createLineGrossAmountDataType();

            lineGrossAmountDataType.setLineGrossAmountNormal(item.getTotal());
            lineGrossAmountDataType.setLineGrossAmountNormalHUF(item.getCurrency().equalsIgnoreCase(HUF.toString()) ? item.getTotal()
                : roundUp(item.getTotal().multiply(exchangeRate), 2));
            lineAmountsNormal.setLineGrossAmountData(lineGrossAmountDataType);
            line.setLineAmountsNormal(lineAmountsNormal);
            lines.getLine().add(line);
        });
        return lines;
    }

    private VatRateType createVatRateType(hu.congressline.pcs.domain.enumeration.VatRateType vatRateType, Integer vat, String vatExceptionReason) {
        final VatRateType lineVatRate = dataFactory.createVatRateType();
        switch (vatRateType) {
            case REGULAR:
                lineVatRate.setVatPercentage(roundUp(new BigDecimal(vat * 0.01), 2));
                break;
            case NAM:
                DetailedReasonType vatExemption = dataFactory.createDetailedReasonType();
                vatExemption.setCase(vatRateType.toString());
                vatExemption.setReason(vatExceptionReason);
                lineVatRate.setVatExemption(vatExemption);
                break;
            case ATK:
            case EUFAD37:
            case EUFADE:
            case EUE:
            case HO:
                DetailedReasonType vatOutOfScope = dataFactory.createDetailedReasonType();
                vatOutOfScope.setCase(vatRateType.toString());
                vatOutOfScope.setReason(vatExceptionReason);
                lineVatRate.setVatOutOfScope(vatOutOfScope);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + vatRateType);
        }
        return lineVatRate;
    }

    private void createCustomerVatData(Invoice invoice, CustomerInfoType customerInfo) {
        String[] taxNumberArray = invoice.getVatRegNumber().split("-");
        CustomerTaxNumberType customerTaxNumberType = dataFactory.createCustomerTaxNumberType();
        CustomerVatDataType vatDataType = dataFactory.createCustomerVatDataType();
        switch (invoice.getNavVatCategory()) {
            case DOMESTIC_NORMAL_VAT_TAX_NUMBER:
                customerTaxNumberType.setTaxpayerId(taxNumberArray[0]);
                customerTaxNumberType.setVatCode(taxNumberArray[1]);
                customerTaxNumberType.setCountyCode(taxNumberArray[2]);
                vatDataType.setCustomerTaxNumber(customerTaxNumberType);
                customerInfo.setCustomerVatData(vatDataType);
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.DOMESTIC);
                break;
            case PRIVATE_PERSON:
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.PRIVATE_PERSON);
                break;
            case EU_NO_VAT_EU_TAX_NUMBER:
                if (StringUtils.hasText(invoice.getVatRegNumber())) {
                    vatDataType = dataFactory.createCustomerVatDataType();
                    vatDataType.setCommunityVatNumber(invoice.getVatRegNumber());
                    customerInfo.setCustomerVatData(vatDataType);
                }
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.OTHER);
                break;
            case EU_VAT_EU_TAX_NUMBER:
                vatDataType = dataFactory.createCustomerVatDataType();
                if (StringUtils.hasText(invoice.getVatRegNumber())) {
                    vatDataType.setCommunityVatNumber(invoice.getVatRegNumber());
                    customerInfo.setCustomerVatData(vatDataType);
                }
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.OTHER);
                break;
            case OTHER_COUNTRY:
                vatDataType = dataFactory.createCustomerVatDataType();
                if (StringUtils.hasText(invoice.getVatRegNumber())) {
                    vatDataType.setThirdStateTaxId(invoice.getVatRegNumber());
                    customerInfo.setCustomerVatData(vatDataType);
                }
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.OTHER);
                break;
            case OTHER_CUSTOMER:
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.OTHER);
                break;
            case DOMESTIC_GROUP_VAT_TAX_NUMBER:
                taxNumberArray = invoice.getVatRegNumber().split("-");
                customerTaxNumberType = dataFactory.createCustomerTaxNumberType();
                customerTaxNumberType.setTaxpayerId(taxNumberArray[0]);
                customerTaxNumberType.setVatCode(taxNumberArray[1]);
                customerTaxNumberType.setCountyCode(taxNumberArray[2]);

                /*
                TaxNumberType taxNumberType = new TaxNumberType();
                taxNumberType.setTaxpayerId(taxNumberArray[0]);
                taxNumberType.setVatCode(taxNumberArray[1]);
                taxNumberType.setCountyCode(taxNumberArray[2]);

                customerTaxNumberType = dataFactory.createCustomerTaxNumberType();
                customerTaxNumberType.setGroupMemberTaxNumber(taxNumberType);
                */

                vatDataType = dataFactory.createCustomerVatDataType();
                vatDataType.setCustomerTaxNumber(customerTaxNumberType);
                customerInfo.setCustomerVatData(vatDataType);
                customerInfo.setCustomerVatStatus(CustomerVatStatusType.DOMESTIC);
                break;
            default:
        }
    }

    private InvoiceHeadType createInvoiceHeader(Invoice invoice, String currency) {
        final Company profile = companyService.getCompanyProfile();
        final InvoiceHeadType invoiceHeadType = dataFactory.createInvoiceHeadType();
        final SupplierInfoType supplier = dataFactory.createSupplierInfoType();
        supplier.setSupplierName(profile.getName());

        final SimpleAddressType satSupplier = baseFactory.createSimpleAddressType();
        satSupplier.setCountryCode(profile.getCountryCode());
        satSupplier.setPostalCode(profile.getZipCode());
        satSupplier.setCity(profile.getCity());
        satSupplier.setAdditionalAddressDetail(profile.getStreetName() + " " + profile.getPublicPlaceCategory() + " " + profile.getNumber());
        AddressType addressType = baseFactory.createAddressType();
        addressType.setSimpleAddress(satSupplier);
        supplier.setSupplierAddress(addressType);

        String[] taxNumberArray = profile.getTaxNumber().split("-");
        TaxNumberType taxNumberType = baseFactory.createTaxNumberType();
        taxNumberType.setTaxpayerId(taxNumberArray[0]);
        taxNumberType.setVatCode(taxNumberArray[1]);
        taxNumberType.setCountyCode(taxNumberArray[2]);
        supplier.setSupplierTaxNumber(taxNumberType);

        supplier.setSupplierBankAccountNumber(invoice.getBankAccount());
        invoiceHeadType.setSupplierInfo(supplier);

        final CustomerInfoType customer = dataFactory.createCustomerInfoType();
        if (!PRIVATE_PERSON.equals(invoice.getNavVatCategory())) {
            customer.setCustomerName(invoice.getName1());
            addressType = baseFactory.createAddressType();
            final SimpleAddressType datCustomer = baseFactory.createSimpleAddressType();
            datCustomer.setCountryCode(invoice.getCountryCode());
            datCustomer.setPostalCode(invoice.getZipCode());
            datCustomer.setCity(invoice.getCity());
            datCustomer.setAdditionalAddressDetail(invoice.getStreet());
            addressType.setSimpleAddress(datCustomer);
            customer.setCustomerAddress(addressType);
        }

        createCustomerVatData(invoice, customer);
        invoiceHeadType.setCustomerInfo(customer);

        final InvoiceDetailType invoiceDetailType = dataFactory.createInvoiceDetailType();
        final XMLGregorianCalendar fulfilmentDate = getXmlLocalDate(convertLocalDateToDate(invoice.getDateOfFulfilment()));
        fulfilmentDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        invoiceDetailType.setInvoiceDeliveryDate(fulfilmentDate);
        final XMLGregorianCalendar paymentDate = getXmlLocalDate(convertLocalDateToDate(invoice.getPaymentDeadline()));
        paymentDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        invoiceDetailType.setPaymentDate(paymentDate);
        invoiceDetailType.setInvoiceCategory(InvoiceCategoryType.NORMAL);
        invoiceDetailType.setCurrencyCode(Currency.valueOf(currency.toUpperCase()).toString());
        invoiceDetailType.setExchangeRate(Currency.valueOf(currency.toUpperCase()).equals(HUF) ? BigDecimal.ONE : invoice.getExchangeRate());
        invoiceDetailType.setPaymentMethod(PaymentMethodType.TRANSFER.value().equals(invoice.getBillingMethod()) ? PaymentMethodType.TRANSFER : PaymentMethodType.CASH);
        invoiceDetailType.setInvoiceAppearance(InvoiceAppearanceType.ELECTRONIC);
        invoiceHeadType.setInvoiceDetail(invoiceDetailType);
        return invoiceHeadType;
    }

    private InvoiceData createInvoiceData(Invoice invoice) {
        final List<InvoiceItem> itemList = invoiceItemRepository.findAllByInvoice(invoice);

        final InvoiceData invoiceData = dataFactory.createInvoiceData();
        invoiceData.setInvoiceNumber(invoice.getInvoiceNumber());
        final XMLGregorianCalendar issueDate = getXmlLocalDate(convertLocalDateToDate(invoice.getCreatedDate()));
        issueDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        invoiceData.setInvoiceIssueDate(issueDate);

        final InvoiceMainType invoiceMainType = dataFactory.createInvoiceMainType();
        invoiceMainType.setInvoice(dataFactory.createInvoiceType());
        invoiceData.setInvoiceMain(invoiceMainType);

        if (invoice.getStorno()) {
            invoiceData.getInvoiceMain().getInvoice().setInvoiceReference(dataFactory.createInvoiceReferenceType());
            invoiceData.getInvoiceMain().getInvoice().getInvoiceReference().setOriginalInvoiceNumber(invoice.getStornoInvoiceNumber());
            invoiceData.getInvoiceMain().getInvoice().getInvoiceReference().setModificationIndex(1);
        }

        invoiceData.getInvoiceMain().getInvoice().setInvoiceHead(createInvoiceHeader(invoice, itemList.stream().findFirst().map(InvoiceItem::getCurrency).orElse(null)));
        invoiceData.getInvoiceMain().getInvoice().setInvoiceLines(createInvoiceLines(itemList));
        invoiceData.getInvoiceMain().getInvoice().setInvoiceSummary(createInvoiceSummary(itemList));
        return invoiceData;
    }

    private TokenExchangeRequest createTokenExchangeRequest() {
        final TokenExchangeRequest request = apiFactory.createTokenExchangeRequest();
        addBasicRequestType(request);
        addSoftwareInfo(request);
        return request;
    }

    private void addBasicRequestType(BasicOnlineInvoiceRequestType brt) {
        final BasicHeaderType header = commonFactory.createBasicHeaderType();
        header.setRequestId(UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase());
        header.setTimestamp(getXmlUtcDate(getUtcDate()));
        header.setHeaderVersion(properties.getNav().getHeader().getHeaderVersion());
        header.setRequestVersion(properties.getNav().getHeader().getRequestVersion());
        brt.setHeader(header);

        final UserHeaderType user = commonFactory.createUserHeaderType();
        user.setLogin(properties.getNav().getUser().getLogin());
        CryptoType cryptoType = commonFactory.createCryptoType();
        cryptoType.setCryptoType("SHA-512");
        cryptoType.setValue(properties.getNav().getUser().getPassword());
        user.setPasswordHash(cryptoType);
        user.setTaxNumber(properties.getNav().getUser().getTaxNumber());
        cryptoType = commonFactory.createCryptoType();

        cryptoType.setCryptoType(SHA3_512);
        cryptoType.setValue(properties.getNav().getUser().getSignKey());
        user.setRequestSignature(cryptoType);
        brt.setUser(user);
    }

    private void addSoftwareInfo(BasicOnlineInvoiceRequestType request) {
        final SoftwareType software = apiFactory.createSoftwareType();
        software.setSoftwareId(properties.getNav().getSoftware().getSoftwareId());
        software.setSoftwareName(properties.getNav().getSoftware().getSoftwareName());
        software.setSoftwareOperation(SoftwareOperationType.LOCAL_SOFTWARE);
        software.setSoftwareMainVersion(properties.getNav().getSoftware().getSoftwareMainVersion());
        software.setSoftwareDevName(properties.getNav().getSoftware().getSoftwareDevName());
        software.setSoftwareDevContact(properties.getNav().getSoftware().getSoftwareDevContact());
        software.setSoftwareDevCountryCode(properties.getNav().getSoftware().getSoftwareDevCountryCode());
        software.setSoftwareDevTaxNumber(properties.getNav().getSoftware().getSoftwareDevTaxNumber());
        request.setSoftware(software);
    }

    @SuppressWarnings("IllegalCatch")
    private TokenExchangeResponse postExchangeToken(TokenExchangeRequest tokenExchangeRequest) {
        TokenExchangeResponse response = null;
        try {
            HttpResponse<String> httpResponse = sendHttpPostRequest("/tokenExchange", getNavObjectAsString(tokenExchangeRequest, TokenExchangeRequest.class));
            if (httpResponse.statusCode() == 200) {
                log.info(SUCCESSFUL_NAV_ONLINE_POST_REQUEST, httpResponse.statusCode(), httpResponse.body());
                final TokenExchangeResponse ter = createNavObjectFromString(TokenExchangeResponse.class, httpResponse.body());
                if (OK.equals(ter.getResult().getFuncCode().value())) {
                    response = ter;
                }
            } else {
                log.warn(ERROR_NAV_ONLINE_POST_REQUEST, httpResponse.statusCode(), httpResponse.body());
                GeneralErrorResponse exceptionResponse = createNavObjectFromString(GeneralErrorResponse.class, httpResponse.body());
            }
        } catch (Exception e) {
            log.error(ERROR_NAV_ONLINE_EXCHANGE_TOKEN_POST, e);
        }
        return response;
    }

    @SuppressWarnings("IllegalCatch")
    private QueryTransactionStatusResponse postQueryTransactionStatusRequest(QueryTransactionStatusRequest transactionStatusRequest) {
        QueryTransactionStatusResponse response = null;
        try {
            HttpResponse<String> httpResponse = sendHttpPostRequest("/queryTransactionStatus", getNavObjectAsString(transactionStatusRequest, QueryTransactionStatusRequest.class));
            if (httpResponse.statusCode() == 200) {
                log.info(SUCCESSFUL_NAV_ONLINE_POST_REQUEST, httpResponse.statusCode(), httpResponse.body());
                final QueryTransactionStatusResponse qtsr = createNavObjectFromString(QueryTransactionStatusResponse.class, httpResponse.body());
                logXmlAsString(qtsr, TokenExchangeResponse.class);
                if (OK.equals(qtsr.getResult().getFuncCode().value())) {
                    response = qtsr;
                }
            } else {
                log.warn("Error during nav online query transaction status request processing, response code: {} {}", httpResponse.statusCode(), httpResponse.body());
                GeneralErrorResponse exceptionResponse = createNavObjectFromString(GeneralErrorResponse.class, httpResponse.body());
            }
        } catch (Exception e) {
            log.error(ERROR_NAV_ONLINE_EXCHANGE_TOKEN_POST, e);
        }
        return response;
    }

    @SuppressWarnings("IllegalCatch")
    private ManageInvoiceResponse postManageInvoice(ManageInvoiceRequest manageInvoiceRequest) {
        ManageInvoiceResponse response = null;
        try {
            final HttpResponse<String> httpResponse = sendHttpPostRequest("/manageInvoice", getNavObjectAsString(manageInvoiceRequest, ManageInvoiceRequest.class));

            if (httpResponse.statusCode() == 200) {
                log.info(SUCCESSFUL_NAV_ONLINE_POST_REQUEST, httpResponse.statusCode(), httpResponse.body());
                final ManageInvoiceResponse mer = createNavObjectFromString(ManageInvoiceResponse.class, httpResponse.body());
                if (OK.equals(mer.getResult().getFuncCode().value())) {
                    response = mer;
                }
            } else {
                log.warn(ERROR_NAV_ONLINE_POST_REQUEST, httpResponse.statusCode(), httpResponse.body());
                GeneralErrorResponse exceptionResponse = createNavObjectFromString(GeneralErrorResponse.class, httpResponse.body());
            }
        } catch (Exception e) {
            log.error(ERROR_NAV_ONLINE_EXCHANGE_TOKEN_POST, e);
        }
        return response;
    }

    @SneakyThrows
    private HttpResponse<String> sendHttpPostRequest(String uri, String body) {
        URI requestUri = URI.create(properties.getNav().getUrl() + uri);
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            final String contentType = "application/xml";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(requestUri)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", contentType)
                .header("Accept", contentType)
                .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    @SneakyThrows
    private String getNavObjectAsString(@NonNull Object xmlObject, @NonNull Class<?> clazz) {
        JAXBContext requestCtx = JAXBContext.newInstance(clazz);
        Marshaller m = requestCtx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(xmlObject, sw);
        log.debug(sw.toString());
        return sw.toString();
    }

    @SneakyThrows
    private <T> T createNavObjectFromString(@NonNull Class<T> clazz, @NonNull String xml) {
        log.debug(xml);
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (StringReader reader = new StringReader(xml)) {
            return clazz.cast(unmarshaller.unmarshal(reader));
        }
    }

    private void generateRequestSignature(BasicRequestType brt) {
        String sb = brt.getHeader().getRequestId() + getStrippedUtcDate(brt.getHeader().getTimestamp().toGregorianCalendar().getTime())
            + properties.getNav().getUser().getSignKey();
        brt.getUser().setRequestSignature(generateRequestSignature(sb));
    }

    private void generateRequestSignature(ManageInvoiceRequest mir) {
        final StringBuilder hashBuilder = new StringBuilder();
        String sb = mir.getHeader().getRequestId() + getStrippedUtcDate(mir.getHeader().getTimestamp().toGregorianCalendar().getTime())
            + properties.getNav().getUser().getSignKey();
        hashBuilder.append(sb);

        mir.getInvoiceOperations().getInvoiceOperation().forEach(invoiceOperationType -> {
            String invoiceDataInfo = invoiceOperationType.getInvoiceOperation().toString() + Base64.encodeBase64String(invoiceOperationType.getInvoiceData());
            String invoiceDataInfoHash = hashWithSHA3_512(invoiceDataInfo);
            hashBuilder.append(invoiceDataInfoHash.toUpperCase());
        });
        mir.getUser().setRequestSignature(generateRequestSignature(hashBuilder.toString()));
    }

    private CryptoType generateRequestSignature(String value) {
        CryptoType cryptoType = commonFactory.createCryptoType();
        cryptoType.setCryptoType(SHA3_512);
        cryptoType.setValue(hashWithSHA3_512(value).toUpperCase());
        return cryptoType;
    }

}
