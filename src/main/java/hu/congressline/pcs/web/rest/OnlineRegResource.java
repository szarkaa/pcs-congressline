package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.Country;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.PaymentSupplier;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.OnlineRegConfigRepository;
import hu.congressline.pcs.repository.OnlineRegistrationRepository;
import hu.congressline.pcs.service.OnlinePaymentService;
import hu.congressline.pcs.service.OnlineRegPdfService;
import hu.congressline.pcs.service.OnlineRegService;
import hu.congressline.pcs.service.dto.OnlineRegDiscountCodeDTO;
import hu.congressline.pcs.service.dto.kh.PaymentInitResult;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
import hu.congressline.pcs.service.dto.online.CongressDTO;
import hu.congressline.pcs.service.dto.online.HotelDTO;
import hu.congressline.pcs.service.dto.online.OnlineRegCustomQuestionDTO;
import hu.congressline.pcs.service.dto.online.OptionalServiceDTO;
import hu.congressline.pcs.service.dto.online.PaymentResultDTO;
import hu.congressline.pcs.service.dto.online.RegistrationTypeDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.vm.OnlineRegistrationVM;
import hu.congressline.pcs.web.rest.vm.StripePaymentStatusVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/registration/online")
public class OnlineRegResource {
    private static final String ENTITY_NAME = "/api/registration/online/";

    private final ApplicationProperties properties;
    private final OnlineRegService onlineRegService;
    private final OnlinePaymentService onlinePaymentService;
    private final CountryRepository countryRepository;
    private final OnlineRegConfigRepository onlineRegConfigRepository;
    private final OnlineRegistrationRepository onlineRegistrationRepository;
    private final OnlineRegPdfService onlineRegPdfService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody OnlineRegistrationVM vm) throws URISyntaxException {
        log.debug("REST request to save OnlineRegistration");
        final OnlineRegistration onlineReg = onlineRegService.save(vm);
        OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(onlineReg.getCongress().getId())
            .orElseThrow(() -> new IllegalArgumentException("OnlineRegConfig not found by id: " + onlineReg.getCongress().getId()));

        HttpHeaders headers = new HttpHeaders();
        if (PaymentSupplier.STRIPE.equals(onlineRegConfig.getPaymentSupplier())) {
            if (onlineRegService.getTotalAmountOfOnlineReg(onlineReg).compareTo(BigDecimal.ZERO) > 0) {
                final String paymentTrxId = onlinePaymentService.getNextPaymentTrxId().toString();
                onlineReg.setPaymentTrxId(paymentTrxId);
                OnlineRegistration result = onlineRegistrationRepository.save(onlineReg);
                final String paymentCheckout = onlinePaymentService.makeStripePaymentCheckout(result);
                return ResponseEntity.created(new URI(ENTITY_NAME + result.getId())).headers(headers).body(paymentCheckout);
            }
        } else {
            if ("CARD".equals(onlineReg.getPaymentMethod()) && !"AMEX".equals(onlineReg.getCardType())) {
                if (!Currency.HUF.toString().equalsIgnoreCase(vm.getCurrency()) && !Currency.EUR.toString().equalsIgnoreCase(vm.getCurrency())) {
                    throw new IllegalArgumentException("Payment currency has to be HUF or EUR!");
                }

                Currency currency = Currency.parse(vm.getCurrency());
                final String orderNo = onlinePaymentService.getNextPaymentTrxId().toString();
                final PaymentInitResult initResult = onlinePaymentService.sendKHPaymentInitRequest(orderNo,
                    onlineRegService.getTotalAmountOfOnlineReg(onlineReg), currency);
                onlineReg.setPaymentOrderNumber(orderNo);
                onlineReg.setPaymentTrxId(initResult.getPayId());
                onlineReg.setPaymentTrxStatus(PaymentStatus.PAYMENT_INITIATED.toString());
                onlineReg.setPaymentTrxDate(ZonedDateTime.now());
                onlineRegistrationRepository.save(onlineReg);

                String processUrl = onlinePaymentService.createKHPaymentProcessRequestUrl(initResult.getPayId(), currency);
                headers.set("X-pcsApp-payment-redirect", processUrl);
            }
        }
        return ResponseEntity.created(new URI(ENTITY_NAME + onlineReg.getId())).headers(headers).body(onlineReg);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/stripe/payment/status")
    public ResponseEntity<Void> setStripePaymentStatus(@RequestBody StripePaymentStatusVM status) throws URISyntaxException {
        OnlineRegistration onlineReg = onlineRegistrationRepository.findOneByPaymentTrxId(status.getTxId())
            .orElseThrow(() -> new IllegalArgumentException("OnlineRegistration not found by id: " + status.getTxId()));
        if ("STRIPE".equals(onlineReg.getPaymentMethod())) {
            onlineReg.setPaymentTrxDate(ZonedDateTime.now());
            final OnlineRegistration result = onlineRegistrationRepository.save(onlineReg);
        }
        return ResponseEntity.created(new URI("/api/registration/online/stripe/payment/status/" + onlineReg.getId())).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}")
    public ResponseEntity<CongressDTO> getCongressByUuid(@PathVariable String uuid) {
        log.debug("REST request to get congress for online registration by uuid: {}", uuid);
        CongressDTO vm = onlineRegService.findCongressForOnline(uuid);
        return Optional.ofNullable(vm)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}/online-reg-discount-code/{code}")
    public ResponseEntity<OnlineRegDiscountCodeDTO> getOnlineRegDiscountCode(@PathVariable String uuid, @PathVariable String code) {
        log.debug("REST request to get discount code for online registration by uuid: {}, code: {}", uuid, code);
        final OnlineRegDiscountCodeDTO dto = onlineRegService.getOnlineRegDiscountCode(uuid, code);
        return Optional.ofNullable(dto)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/country")
    public List<Country> getAllCountry() {
        log.debug("REST request to get all countries for online registration");
        return countryRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}/registration-type/{currency}")
    public List<RegistrationTypeDTO> getAllRegTypesByUuid(@PathVariable String uuid, @PathVariable String currency) {
        log.debug("REST request to get all registration types for online registration by uuid: {}, currency: {}", uuid, currency);
        return onlineRegService.getAllRegistrationTypes(uuid, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}/hotel-room/{currency}")
    public List<HotelDTO> getAllHotelRoomsByUuid(@PathVariable String uuid, @PathVariable String currency) {
        log.debug("REST request to get all hotel rooms for online registration by uuid: {}, currency: {}", uuid, currency);
        return onlineRegService.getAllHotelRooms(uuid, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}/optional-service/{currency}")
    public List<OptionalServiceDTO> getAllOptionalServicesByUuid(@PathVariable String uuid, @PathVariable String currency) {
        log.debug("REST request to get all optional services for online registration by uuid: {}, currency: {}", uuid, currency);
        return onlineRegService.getAllOptionalServices(uuid, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress/{uuid}/custom-questions/{currency}")
    public List<OnlineRegCustomQuestionDTO> getAllCustomQuestionsByUuid(@PathVariable String uuid, @PathVariable String currency) {
        log.debug("REST request to get all custom questions for online registration by uuid: {}, currency: {}", uuid, currency);
        return onlineRegService.getAllCustomQuestions(uuid, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/{id}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getOnlineRegPdf(@PathVariable Long id) {
        final OnlineRegistration onlineReg = onlineRegService.getById(id);
        byte[] pdfBytes = onlineRegPdfService.getPdf(onlineReg);
        String fileName = ServiceUtil.normalizeForFilename(onlineReg.getCongress().getMeetingCode().toLowerCase() + "-online-reg-" + onlineReg.getId()) + ".pdf";
        return ResponseEntity
                .ok()
                .headers(createHeaders("inline; filename=" + fileName))
                .contentLength(pdfBytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/header/{uuid}")
    public ResponseEntity<byte[]> getOnlineRegHeaderImage(@PathVariable String uuid) {
        log.debug("REST request to get online header for uuid: {}", uuid);
        final OnlineRegConfig config = onlineRegService.findConfigForOnline(uuid);
        if (config == null || config.getHeaderNormalFile() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final String contentType = config.getHeaderNormalContentType();
        final String contentDisposition = "attachment; filename=" + uuid + "." + contentType.substring(Math.max(0, contentType.lastIndexOf("/") + 1));
        return ResponseEntity.ok().headers(createHeaders(contentDisposition)).contentLength(config.getHeaderNormalFile().length)
                .contentType(MediaType.parseMediaType(config.getHeaderNormalContentType())).body(config.getHeaderNormalFile());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment/result/{txId}")
    public ResponseEntity<PaymentResultDTO> getPaymentResultByTxId(@PathVariable String txId) {
        log.debug("REST request to get payment result for txid: {}", txId);
        PaymentResultDTO vm = onlineRegService.getPaymentResultByTrxId(txId);
        return Optional.ofNullable(vm)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings({"MissingJavadocMethod", "ParameterNumber"})
    @GetMapping("/kh/payment/result")
    public ResponseEntity<Void> getKhPaymentReturnUrl(@RequestParam String payId,
                                                   @RequestParam String dttm,
                                                   @RequestParam Integer resultCode,
                                                   @RequestParam String resultMessage,
                                                   @RequestParam(required = false) Integer paymentStatus,
                                                   @RequestParam(required = false) String authCode,
                                                   @RequestParam(required = false) String merchantData,
                                                   @RequestParam(required = false) String statusDetail,
                                                   @RequestParam String signature
    ) throws URISyntaxException {
        log.debug("REST request to handle kh payment return url with params: payId: {}, dttm: {}"
                + ", resultCode: {}, resultMessage: {}, paymentStatus: {}, authCode: {}, merchantData: {}"
                + ", statusDetail: {}", payId, dttm, resultCode,
            resultMessage, paymentStatus, authCode, merchantData, statusDetail);
        onlineRegService.handleKHPaymentProcessResponse(payId, resultCode.toString(), resultMessage, paymentStatus, authCode);
        PaymentStatus status = PaymentStatus.getByCode(paymentStatus);
        String uriSuffix = resultCode == 0 && (PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT.equals(status) || PaymentStatus.PAYMENT_SETTLED.equals(status))
            ? "success" : "failure";
        URI uri = new URI(properties.getSystem().getBaseUrl() + "/#/registration/online/payment/result/" + uriSuffix + "?txid=" + payId);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(uri).build();
    }

    private HttpHeaders createHeaders(String contentDisposition) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", contentDisposition);
        return headers;
    }
}
