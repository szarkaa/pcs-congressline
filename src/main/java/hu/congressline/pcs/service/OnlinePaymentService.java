package hu.congressline.pcs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.PaymentSupplier;
import hu.congressline.pcs.repository.PaymentTransactionRepository;
import hu.congressline.pcs.service.dto.kh.EchoRequest;
import hu.congressline.pcs.service.dto.kh.EchoResult;
import hu.congressline.pcs.service.dto.kh.PaymentCartItem;
import hu.congressline.pcs.service.dto.kh.PaymentCloseRequest;
import hu.congressline.pcs.service.dto.kh.PaymentCloseResult;
import hu.congressline.pcs.service.dto.kh.PaymentInitRequest;
import hu.congressline.pcs.service.dto.kh.PaymentInitResult;
import hu.congressline.pcs.service.dto.kh.PaymentProcessRequest;
import hu.congressline.pcs.service.dto.kh.PaymentRefundRequest;
import hu.congressline.pcs.service.dto.kh.PaymentRefundResult;
import hu.congressline.pcs.service.dto.kh.PaymentReverseRequest;
import hu.congressline.pcs.service.dto.kh.PaymentReverseResult;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
import hu.congressline.pcs.service.dto.kh.PaymentStatusRequest;
import hu.congressline.pcs.service.dto.kh.PaymentStatusResult;
import hu.congressline.pcs.service.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_RETURNED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_REVERSED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_SETTLED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT;

@Slf4j
@RequiredArgsConstructor
@Service
public class OnlinePaymentService {
    public static final String BANK_AUTH_NUMBER = "bankAuthNumber";
    private static final String PAYMENT_INIT_REQUEST_FAILED = "Payment init request sending failed!";
    private static final String PAYMENT_CURRENCY = "Payment currency has to be HUF or EUR!";
    private static final Gson GSON = new Gson();

    private final ApplicationProperties properties;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final CompanyService companyService;
    private final CongressService congressService;
    private final OnlineRegService onlineRegService;
    private final PaymentCryptoService paymentCryptoService;

    @SuppressWarnings("MissingJavadocMethod")
    public void checkPendingPaymentResults() {
        List<PaymentTransaction> paymentTransactionList = paymentTransactionRepository.findByPaymentTrxStatus(PAYMENT_WAITING_FOR_SETTLEMENT.toString());
        List<OnlineRegistration> onlineRegList = onlineRegService.findAllByPaymentTrxStatus();
        log.debug("checkPendingPaymentResults: found {} pending online registrations", onlineRegList.size());
        onlineRegList.forEach(onlineReg -> {
            final OnlineRegConfig onlineRegConfig = congressService.getConfigByCongressId(onlineReg.getCongress().getId());
            if (PaymentSupplier.KH.equals(onlineRegConfig.getPaymentSupplier())) {
                String currency = onlineReg.getCurrency();
                final PaymentStatusResult statusResult = sendPaymentStatusRequest(onlineReg.getPaymentTrxId(), currency);
                onlineReg.setPaymentTrxResultCode(statusResult.getResultCode() != null ? statusResult.getResultCode().toString() : null);
                onlineReg.setPaymentTrxStatus(statusResult.getPaymentStatus() != null ? PaymentStatus.getByCode(statusResult.getPaymentStatus()).toString() : null);
                onlineReg.setPaymentTrxAuthCode(statusResult.getAuthCode());
                onlineReg.setBankAuthNumber(BANK_AUTH_NUMBER);
                onlineReg.setPaymentTrxResultMessage(statusResult.getResultMessage());
                onlineRegService.save(onlineReg);
            }
        });

        log.debug("checkPendingPaymentResults: found {} pending payment transactions", paymentTransactionList.size());
        paymentTransactionList.forEach(paymentTransaction -> {
            final OnlineRegConfig onlineRegConfig = congressService.getConfigByCongressId(paymentTransaction.getCongress().getId());
            if (PaymentSupplier.KH.equals(onlineRegConfig.getPaymentSupplier())) {
                String currency = paymentTransaction.getCurrency();
                final PaymentStatusResult statusResult = sendPaymentStatusRequest(paymentTransaction.getTransactionId(), currency);
                if (statusResult.getPaymentStatus() != null && List.of(PAYMENT_REVERSED, PAYMENT_RETURNED, PAYMENT_SETTLED)
                        .contains(PaymentStatus.getByCode(statusResult.getPaymentStatus()))) {
                    paymentTransaction.setPaymentTrxResultCode(statusResult.getResultCode() != null ? statusResult.getResultCode().toString() : null);
                    paymentTransaction.setPaymentTrxStatus(statusResult.getPaymentStatus() != null ? PaymentStatus.getByCode(statusResult.getPaymentStatus()).toString() : null);
                    paymentTransaction.setPaymentTrxAuthCode(statusResult.getAuthCode());
                    paymentTransaction.setBankAuthNumber(BANK_AUTH_NUMBER);
                    paymentTransaction.setPaymentTrxResultMessage(statusResult.getResultMessage());
                    paymentTransactionRepository.save(paymentTransaction);
                }
            }
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public String makeStripePaymentCheckout(OnlineRegistration onlineReg) {
        String retVal = null;
        final OnlineRegConfig onlineRegConfig = congressService.getConfigByCongressId(onlineReg.getCongress().getId());
        if (onlineRegConfig != null) {
            final BigDecimal totalAmount = onlineRegService.getTotalAmountOfOnlineReg(onlineReg);
            try {
                SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
                    .setLocale("HUF".equalsIgnoreCase(onlineReg.getCurrency()) ? SessionCreateParams.Locale.HU : SessionCreateParams.Locale.EN)
                    .setSuccessUrl(properties.getSystem().getBaseUrl() + "/#/registration/online/payment/result/success?txid=" + onlineReg.getPaymentTrxId())
                    .setCancelUrl(properties.getSystem().getBaseUrl() + "/#/registration/online/payment/result/failure?txid=" + onlineReg.getPaymentTrxId())
                    .addAllPaymentMethodType(Collections.singletonList(SessionCreateParams.PaymentMethodType.CARD))
                    .setCustomerEmail(onlineReg.getEmail())
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(onlineReg.getEmail());

                SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder = new SessionCreateParams.LineItem.PriceData.ProductData.Builder()
                    .setName("Online registration");

                SessionCreateParams.LineItem.PriceData.Builder priceDataBuilder = new SessionCreateParams.LineItem.PriceData.Builder()
                    .setProductData(productDataBuilder.build())
                    .setCurrency(onlineReg.getCurrency())
                    .setUnitAmount(totalAmount.multiply(new BigDecimal(100)).longValue());

                SessionCreateParams.LineItem item = new SessionCreateParams.LineItem.Builder()
                    .setQuantity(1L)
                    .setPriceData(priceDataBuilder.build())
                    .build();
                builder.addLineItem(item);

                RequestOptions requestOptions = RequestOptions.builder().setApiKey(onlineRegConfig.getStripeSecretKey()).build();
                SessionCreateParams createParams = builder.build();
                Session session = Session.create(createParams, requestOptions);

                Map<String, Object> responseData = new HashMap<>();
                responseData.put("sessionId", session.getId());
                return GSON.toJson(responseData);

            } catch (StripeException e) {
                log.error("Creating stripe payment checkout failed", e);
            }
        }
        return retVal;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public String createKHPaymentProcessRequestUrl(String payId, Currency currency) {
        try {
            PaymentProcessRequest processRequest = new PaymentProcessRequest(getMerchantId(currency), payId);
            paymentCryptoService.createSignature(currency, processRequest);
            return getGatewayUri() + "/payment/process/" + processRequest.getMerchantId() + "/" + processRequest.getPayId()
                + "/" + processRequest.getDttm() + "/" + encodeUrl(processRequest.getSignature());
        } catch (Exception e) {
            log.error(PAYMENT_INIT_REQUEST_FAILED, e);
        }
        return null;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public PaymentInitResult sendKHPaymentInitRequest(String orderNo, BigDecimal amount, Currency currency) {
        try {
            //sendEchoRequest(currency);
            log.debug("Payment init request sending");
            PaymentInitRequest initRequest = new PaymentInitRequest();
            initRequest.setMerchantId(getMerchantId(currency));
            initRequest.setCurrency(currency);
            initRequest.setOrderNo(orderNo);
            initRequest.setDttm(DateUtil.getPaymentDateNow());
            initRequest.setPayOperation("payment");
            initRequest.setPayMethod("card");
            long totalAmount = amount.multiply(new BigDecimal(100)).longValue();
            initRequest.setTotalAmount(totalAmount);

            PaymentCartItem cart = new PaymentCartItem(Currency.HUF.equals(currency) ? "Regisztracios dij" : "Registration fee", 1, totalAmount);
            initRequest.setCart(List.of(cart));
            initRequest.setReturnUrl(properties.getPayment().getGateway().getReturnUrl());
            initRequest.setReturnMethod("GET");
            initRequest.setLanguage(Currency.HUF.equals(currency) ? "hu" : "en");
            paymentCryptoService.createSignature(currency, initRequest);
            log.debug("initrequest: {}", new ObjectMapper().writeValueAsString(initRequest));

            URI uri = new URI(getGatewayUri() + "/payment/init");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(initRequest), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(uri, httpEntity, String.class);
            log.debug("initresult: {}", response.getBody());
            return new ObjectMapper().readValue(response.getBody(), PaymentInitResult.class);
        } catch (HttpClientErrorException e) {
            log.error("Unsuccessful init invocation", e);
            log.error("status code: {}", e.getStatusCode().toString());
            log.error("error body: {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error(PAYMENT_INIT_REQUEST_FAILED, e);
        }
        return null;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public PaymentReverseResult sendPaymentReverseRequest(String payId, String currency) {
        if (!Currency.HUF.toString().equalsIgnoreCase(currency) && !Currency.EUR.toString().equalsIgnoreCase(currency.toUpperCase())) {
            throw new IllegalArgumentException(PAYMENT_CURRENCY);
        }

        try {
            Currency paymentCurrency = Currency.parse(currency);
            PaymentReverseRequest reverseRequest = new PaymentReverseRequest(getMerchantId(paymentCurrency), payId);
            paymentCryptoService.createSignature(paymentCurrency, reverseRequest);
            log.debug("reverserequest: {}", new ObjectMapper().writeValueAsString(reverseRequest));

            URI uri = new URI(getGatewayUri() + "/payment/reverse");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(reverseRequest), headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            log.debug("reverseresult: {}", response.getBody());
            return new ObjectMapper().readValue(response.getBody(), PaymentReverseResult.class);
        } catch (Exception e) {
            log.error("Payment reverse request sending failed!", e);
        }
        return null;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public PaymentRefundResult sendPaymentRefundRequest(String payId, BigDecimal amount, String currency) {
        if (!Currency.HUF.toString().equalsIgnoreCase(currency) && !Currency.EUR.toString().equalsIgnoreCase(currency.toUpperCase())) {
            throw new IllegalArgumentException(PAYMENT_CURRENCY);
        }

        try {
            Currency paymentCurrency = Currency.parse(currency);
            PaymentRefundRequest refundRequest = new PaymentRefundRequest(getMerchantId(paymentCurrency), payId);
            if (amount != null) {
                refundRequest.setAmount(amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).longValue());
            }
            paymentCryptoService.createSignature(paymentCurrency, refundRequest);
            log.debug("refundrequest: {}", new ObjectMapper().writeValueAsString(refundRequest));

            URI uri = new URI(getGatewayUri() + "/payment/refund");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(refundRequest), headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            log.debug("refundresult: {}", response.getBody());
            return new ObjectMapper().readValue(response.getBody(), PaymentRefundResult.class);
        } catch (Exception e) {
            log.error("Payment refund request sending failed!", e);
        }
        return null;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public PaymentStatusResult sendPaymentStatusRequest(String payId, String currency) {
        if (!Currency.HUF.toString().equalsIgnoreCase(currency) && !Currency.EUR.toString().equalsIgnoreCase(currency.toUpperCase())) {
            throw new IllegalArgumentException(PAYMENT_CURRENCY);
        }

        try {
            Currency paymentCurrency = Currency.parse(currency);
            PaymentStatusRequest statusRequest = new PaymentStatusRequest(getMerchantId(paymentCurrency), payId);
            paymentCryptoService.createSignature(paymentCurrency, statusRequest);
            URI uri = new URI(getGatewayUri() + "/payment/status/" + statusRequest.getMerchantId() + "/" + statusRequest.getPayId()
                + "/" + statusRequest.getDttm() + "/" + encodeUrl(statusRequest.getSignature()));

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            log.debug("status result: {}", response.getBody());
            return new ObjectMapper().readValue(response.getBody(), PaymentStatusResult.class);
        } catch (Exception e) {
            log.error("Payment status request sending failed!", e);
        }
        return null;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public PaymentCloseResult sendPaymentCloseRequest(String payId, String currency) {
        try {
            Currency paymentCurrency = Currency.parse(currency);
            PaymentCloseRequest closeRequest = new PaymentCloseRequest(getMerchantId(paymentCurrency), payId);
            paymentCryptoService.createSignature(paymentCurrency, closeRequest);
            URI uri = new URI(getGatewayUri() + "/payment/close");

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(closeRequest), headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            log.debug("close result: {}", response.getBody());
            return new ObjectMapper().readValue(response.getBody(), PaymentCloseResult.class);
        } catch (Exception e) {
            log.error("Payment close request sending failed!", e);
        }
        return null;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public synchronized Integer getNextPaymentTrxId() {
        Company companyProfile = companyService.getCompanyProfile();

        Integer paymentTrxId = companyProfile.getPaymentTrxId();
        companyProfile.setPaymentTrxId(paymentTrxId + 1);
        Company result = companyService.save(companyProfile);
        return result.getPaymentTrxId();
    }

    private String getGatewayUri() {
        String uri = properties.getPayment().getGateway().getUrl();
        return uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
    }

    private String getMerchantId(Currency currency) {
        if (Currency.EUR.equals(currency)) {
            return properties.getPayment().getGateway().getMerchantIdForEUR();
        } else if (Currency.HUF.equals(currency)) {
            return properties.getPayment().getGateway().getMerchantIdForHUF();
        } else {
            throw new IllegalArgumentException(PAYMENT_CURRENCY);
        }
    }

    private String encodeUrl(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private void sendEchoRequest(Currency currency) throws Exception {
        EchoRequest echoRequest = new EchoRequest(getMerchantId(currency));
        log.debug("echorequest: {}", echoRequest);
        paymentCryptoService.createSignature(currency, echoRequest);

        URI url = new URI(getGatewayUri() + "/echo");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(new ObjectMapper().writeValueAsString(echoRequest), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
        EchoResult echoResult = new ObjectMapper().readValue(response.getBody(), EchoResult.class);
        log.debug("echoresult: {}", echoResult);
    }
}
