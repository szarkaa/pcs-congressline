package hu.congressline.pcs.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.AccPeople;
import hu.congressline.pcs.domain.AccPeopleOnline;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationCustomAnswer;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;
import hu.congressline.pcs.domain.OnlineRegistrationRegistrationType;
import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.RoomReservation;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.domain.enumeration.PaymentSupplier;
import hu.congressline.pcs.repository.AccPeopleOnlineRepository;
import hu.congressline.pcs.repository.AccPeopleRepository;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.repository.OnlineRegConfigRepository;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.repository.OnlineRegDiscountCodeRepository;
import hu.congressline.pcs.repository.OnlineRegistrationCustomAnswerRepository;
import hu.congressline.pcs.repository.OnlineRegistrationOptionalServiceRepository;
import hu.congressline.pcs.repository.OnlineRegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.OnlineRegistrationRepository;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.PaymentTransactionRepository;
import hu.congressline.pcs.repository.RegistrationTypeRepository;
import hu.congressline.pcs.repository.RoomReservationEntryRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.repository.RoomReservationRepository;
import hu.congressline.pcs.service.dto.OnlineRegDiscountCodeDTO;
import hu.congressline.pcs.service.dto.RoomReservationEntryDTO;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
import hu.congressline.pcs.service.dto.kh.PaymentStatusResult;
import hu.congressline.pcs.service.dto.online.CongressDTO;
import hu.congressline.pcs.service.dto.online.HotelDTO;
import hu.congressline.pcs.service.dto.online.OnlineRegConfigDTO;
import hu.congressline.pcs.service.dto.online.OnlineRegCustomQuestionDTO;
import hu.congressline.pcs.service.dto.online.OptionalServiceDTO;
import hu.congressline.pcs.service.dto.online.PaymentResultDTO;
import hu.congressline.pcs.service.dto.online.RegistrationTypeDTO;
import hu.congressline.pcs.service.dto.online.RoomDTO;
import hu.congressline.pcs.service.pdf.OnlineRegPdfContext;
import hu.congressline.pcs.web.rest.vm.AccPeopleVM;
import hu.congressline.pcs.web.rest.vm.OnlineRegFilterVM;
import hu.congressline.pcs.web.rest.vm.OnlineRegOptionalServiceVM;
import hu.congressline.pcs.web.rest.vm.OnlineRegRegTypeVM;
import hu.congressline.pcs.web.rest.vm.OnlineRegistrationVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.config.JHipsterProperties;

import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_RETURNED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_REVERSED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_SETTLED;
import static hu.congressline.pcs.service.dto.kh.PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT;
import static java.lang.Boolean.TRUE;
import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OnlineRegService {
    private static final String BANK_AUTH_NUMBER = "bankAuthNumber";
    private static final String ONLINE_REGISTRATION_NOT_FOUND = "OnlineRegistration not found by trx id: ";

    private final CongressRepository congressRepository;
    private final RegistrationService registrationService;
    private final OnlineRegConfigRepository onlineRegConfigRepository;
    private final RegistrationTypeRepository rtRepository;
    private final RoomService roomService;
    private final OptionalServiceRepository osRepository;
    private final OnlineRegCustomQuestionRepository onlineRegCustomQuestionRepository;
    private final OrderedOptionalServiceRepository oosRepository;
    private final RegistrationRegistrationTypeService rrtService;
    private final OnlineRegistrationRepository repository;
    private final OnlineRegistrationRegistrationTypeRepository orrtRepository;
    private final OnlineRegistrationOptionalServiceRepository orosRepository;
    private final OnlineRegistrationCustomAnswerRepository orcaRepository;
    private final AccPeopleOnlineRepository accPeopleOnlineRepository;
    private final AccPeopleRepository accPeopleRepository;
    private final RoomReservationRepository rrRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final RoomReservationService rrService;
    private final OrderedOptionalServiceService oosService;
    //private final MailService mailService;
    private final OnlineRegPdfService pdfService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OnlineRegistrationRepository onlineRegistrationRepository;
    private final OnlinePaymentService paymentService;
    private final RoomReservationEntryRepository rreRepository;
    private final WorkplaceService workplaceService;
    private final JHipsterProperties properties;
    private final ApplicationProperties applicationProperties;
    private final CompanyService companyService;
    private final OnlineRegDiscountCodeRepository discountCodeRepository;
    private final PaymentTransactionService paymentTransactionService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OnlineRegistration> findById(Long id) {
        log.debug("Request to find OnlineRegistration : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OnlineRegistration getById(Long id) {
        log.debug("Request to get OnlineRegistration : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("OnlineRegistration not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public CongressDTO findCongressForOnline(String uuid) {
        Congress congress = congressRepository.findOneByUuid(uuid).orElse(null);
        if (congress == null) {
            return null;
        }
        final OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(congress.getId());
        CongressDTO dto = new CongressDTO(congress);
        final OnlineRegConfigDTO onlineRegConfigDTO = new OnlineRegConfigDTO(onlineRegConfig);
        onlineRegConfigDTO.setDiscountAvailable(discountCodeRepository.countAllByCongressId(congress.getId()) > 0);
        dto.setOnlineRegConfig(onlineRegConfigDTO);
        return dto;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public PaymentResultDTO getPaymentResultByTrxId(String trxId) {
        OnlineRegistration onlineReg = onlineRegistrationRepository.findOneByPaymentTrxId(trxId)
                .orElseThrow(() -> new IllegalArgumentException(ONLINE_REGISTRATION_NOT_FOUND + trxId));
        final OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(onlineReg.getCongress().getId());
        PaymentResultDTO dto = new PaymentResultDTO();
        dto.setCongressName(onlineReg.getCongress().getName());
        dto.setCongressUuid(onlineReg.getCongress().getUuid());
        dto.setWebsite(onlineReg.getCongress().getWebsite());
        dto.setColorCode(onlineRegConfig.getColorCode());
        dto.setPaymentTrxResultCode(onlineReg.getPaymentTrxResultCode());
        dto.setPaymentTrxResultMessage(onlineReg.getPaymentTrxResultMessage());
        dto.setPaymentTrxStatus(onlineReg.getPaymentTrxStatus());
        dto.setAmount(getTotalAmountOfOnlineReg(onlineReg));
        dto.setCurrency(onlineReg.getCurrency());
        return dto;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void handleKHPaymentProcessResponse(String payId, String resultCode, String resultMessage, Integer paymentStatus, String authCode) {
        OnlineRegistration onlineReg = onlineRegistrationRepository.findOneByPaymentTrxId(payId)
                .orElseThrow(() -> new IllegalArgumentException(ONLINE_REGISTRATION_NOT_FOUND + payId));
        log.debug("handleKHPaymentProcessResponse online reg found by payId: {}", payId);
        onlineReg.setPaymentTrxResultCode(resultCode);
        onlineReg.setPaymentTrxStatus(paymentStatus != null ? PaymentStatus.getByCode(paymentStatus).toString() : null);
        onlineReg.setPaymentTrxAuthCode(authCode);
        onlineReg.setBankAuthNumber(BANK_AUTH_NUMBER);
        onlineReg.setPaymentTrxResultMessage(resultMessage);
        onlineReg = onlineRegistrationRepository.save(onlineReg);
        log.debug("handleKHPaymentProcessResponse online reg updated with payment info");
        if (PAYMENT_WAITING_FOR_SETTLEMENT.toString().equals(onlineReg.getPaymentTrxStatus())) {
            paymentTransactionService.createPaymentTransaction(onlineReg);
        }

        manageFinalPaymentStatus(onlineReg);
    }

    private void manageFinalPaymentStatus(OnlineRegistration onlineReg) {
        log.debug("manageFinalPaymentStatus payment status: {}", PaymentStatus.valueOf(onlineReg.getPaymentTrxStatus()));
        //Manage the transactions that are in a final state
        /*
        if (List.of(PAYMENT_DENIED, PAYMENT_CANCELLED, PAYMENT_WAITING_FOR_SETTLEMENT)
            .contains(PaymentStatus.valueOf(onlineReg.getPaymentTrxStatus()))) {
            mailService.sendOnlinePaymentNotificationEmail(onlineReg.getEmail(),
                onlineReg,
                getTotalAmountOfOnlineReg(onlineReg),
                onlineReg.getCurrency(),
                companyService.getCompanyProfile(),
                Locale.forLanguageTag(Currency.HUF.toString().equalsIgnoreCase(onlineReg.getCurrency()) ? Language.HU.toString().toLowerCase() : Language.EN.toString().toLowerCase()));
        }
        */
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void checkPendingPaymentResults() {
        List<PaymentTransaction> paymentTransactionList = paymentTransactionRepository.findByPaymentTrxStatus(PAYMENT_WAITING_FOR_SETTLEMENT.toString());
        List<OnlineRegistration> onlineRegList = onlineRegistrationRepository.findByPaymentTrxStatusIn(
            List.of(PAYMENT_WAITING_FOR_SETTLEMENT)
                .stream()
                .map(PaymentStatus::toString)
                .collect(Collectors.toList()));
        log.debug("checkPendingPaymentResults: found {} pending online registrations", onlineRegList.size());
        onlineRegList.forEach(onlineReg -> {
            final OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(onlineReg.getCongress().getId());
            if (PaymentSupplier.KH.equals(onlineRegConfig.getPaymentSupplier())) {
                String currency = onlineReg.getCurrency();
                final PaymentStatusResult statusResult = paymentService.sendPaymentStatusRequest(onlineReg.getPaymentTrxId(), currency);
                onlineReg.setPaymentTrxResultCode(statusResult.getResultCode() != null ? statusResult.getResultCode().toString() : null);
                onlineReg.setPaymentTrxStatus(statusResult.getPaymentStatus() != null ? PaymentStatus.getByCode(statusResult.getPaymentStatus()).toString() : null);
                onlineReg.setPaymentTrxAuthCode(statusResult.getAuthCode());
                onlineReg.setBankAuthNumber(BANK_AUTH_NUMBER);
                onlineReg.setPaymentTrxResultMessage(statusResult.getResultMessage());
                onlineRegistrationRepository.save(onlineReg);
            }
        });

        log.debug("checkPendingPaymentResults: found {} pending payment transactions", paymentTransactionList.size());
        paymentTransactionList.forEach(paymentTransaction -> {
            final OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(paymentTransaction.getCongress().getId());
            if (PaymentSupplier.KH.equals(onlineRegConfig.getPaymentSupplier())) {
                String currency = paymentTransaction.getCurrency();
                final PaymentStatusResult statusResult = paymentService.sendPaymentStatusRequest(paymentTransaction.getTransactionId(), currency);
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
    public OnlineRegConfig findConfigForOnline(String uuid) {
        Congress congress = congressRepository.findOneByUuid(uuid).orElse(null);
        if (congress == null) {
            return null;
        }
        return onlineRegConfigRepository.findOneByCongressId(congress.getId());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<RegistrationTypeDTO> getAllRegistrationTypes(String uuid, String currency) {
        return rtRepository.findByOnlineVisibilityAndCongressUuidAndCurrencyCurrencyOrderByOnlineOrder(OnlineVisibility.VISIBLE, uuid, currency.toUpperCase())
                .stream().map(rt -> {
                    RegistrationTypeDTO dto = new RegistrationTypeDTO(rt);
                    dto.setCurrentRegFee(rrtService.calculateRegFee(rt, LocalDate.now()));
                    return dto;
                }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<OptionalServiceDTO> getAllOptionalServices(String uuid, String currency) {
        return osRepository.findByOnlineVisibilityAndCongressUuidAndCurrencyCurrencyOrderByOnlineOrder(OnlineVisibility.VISIBLE, uuid, currency.toUpperCase())
                .stream().filter(os -> os.getMaxPerson().compareTo(os.getReserved()) > 0).map(OptionalServiceDTO::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<OnlineRegCustomQuestionDTO> getAllCustomQuestions(String uuid, String currency) {
        return onlineRegCustomQuestionRepository.findAllByCongressUuidAndCurrencyCurrencyOrderByQuestionOrder(uuid, currency.toUpperCase())
                .stream().filter(question -> question.getOnlineVisibility().equals(OnlineVisibility.VISIBLE))
                .map(OnlineRegCustomQuestionDTO::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<HotelDTO> getAllHotelRooms(String uuid, String currency) {
        List<Room> roomList = roomService.findAllOnlineRooms(uuid, currency.toUpperCase());
        Map<String, HotelDTO> hotelDTOMap = new HashMap<>();
        for (Room room : roomList) {
            if (room.getBed().equals(1) || room.getBed().equals(2)) {
                HotelDTO hotelDTO = hotelDTOMap.get(room.getCongressHotel().getHotel().getId().toString());
                if (hotelDTO == null) {
                    hotelDTO = new HotelDTO(room.getCongressHotel().getHotel());
                    hotelDTOMap.put(hotelDTO.getId().toString(), hotelDTO);
                }

                RoomDTO roomDTO = new RoomDTO(room);
                roomDTO.setReservations(rreRepository.findAllByRoomId(roomDTO.getId()).stream().map(RoomReservationEntryDTO::new)
                        .filter(dto -> !dto.getReserved().equals(0)).collect(Collectors.toList()));

                boolean hasVacancy = roomDTO.getReservations().isEmpty()
                    || roomDTO.getReservations().stream()
                    .filter(rre -> rre.getReservationDate().equals(room.getCongressHotel().getCongress().getStartDate())
                        && roomDTO.getQuantity() <= rre.getReserved()).findAny().isEmpty();

                if (hasVacancy) {
                    List<RoomDTO> list = room.getBed().equals(1) ? hotelDTO.getSingleList() : hotelDTO.getDoubleList();
                    list.add(roomDTO);
                }
            }
        }
        List<HotelDTO> hotelDTOList = new ArrayList<>(hotelDTOMap.values());
        hotelDTOList.forEach(h -> {
            h.getSingleList().sort(Comparator.comparing(RoomDTO::getPrice));
            h.getDoubleList().sort(Comparator.comparing(RoomDTO::getPrice));
        });
        hotelDTOList.sort(Comparator.comparing(HotelDTO::getName));
        return hotelDTOList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegistration save(OnlineRegistration onlineRegistration) {
        return onlineRegistrationRepository.save(onlineRegistration);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    public OnlineRegistration save(OnlineRegistrationVM vm) {
        final Congress congress = congressRepository.findOneByUuid(vm.getUuid()).orElseThrow(() -> new IllegalStateException("Unidentified congress uuid:" + vm.getUuid()));
        final OnlineRegConfig onlineRegConfig = onlineRegConfigRepository.findOneByCongressId(congress.getId());
        OnlineRegistration or = new OnlineRegistration();
        or.setCurrency(vm.getCurrency().toUpperCase());
        or.setTitle(vm.getTitle());
        or.setLastName(vm.getLastName());
        or.setFirstName(vm.getFirstName());
        or.setPosition(vm.getPosition());
        or.setWorkplace(vm.getWorkplace());
        or.setOtherData(vm.getOtherData());
        or.setDepartment(vm.getDepartment());
        or.setZipCode(vm.getZipCode());
        or.setCity(vm.getCity());
        or.setStreet(vm.getStreet());
        or.setCountry(vm.getCountry());
        or.setPhone(vm.getPhone());
        or.setEmail(vm.getEmail());
        or.setRegistrationType(vm.getRegistrationType());
        or.setRoom(vm.getRoom());
        or.setArrivalDate(vm.getArrivalDate());
        or.setDepartureDate(vm.getDepartureDate());
        or.setRoommate(vm.getRoommate());
        or.setRoomRemark(vm.getRoomRemark());
        or.setPaymentMethod(PaymentSupplier.STRIPE.equals(onlineRegConfig.getPaymentSupplier()) ? "STRIPE" : vm.getPaymentMethod());
        or.setCardType(vm.getCardType());
        or.setCheckName(vm.getCheckName());
        or.setCheckAddress(vm.getCheckAddress());
        or.setCardHolderName(vm.getCardHolderName());
        or.setCardHolderAddress(vm.getCardHolderAddress());
        or.setCardNumber(vm.getCardNumber());
        or.setCardExpiryMonth(vm.getCardExpiryMonth());
        or.setCardExpiryYear(vm.getCardExpiryYear());
        or.setInvoiceName(vm.getInvoiceName());
        or.setInvoiceCountry(vm.getInvoiceCountry());
        or.setInvoiceZipCode(vm.getInvoiceZipCode());
        or.setInvoiceCity(vm.getInvoiceCity());
        or.setInvoiceAddress(vm.getInvoiceAddress());
        or.setInvoiceReferenceNumber(vm.getInvoiceReferenceNumber());
        or.setInvoiceTaxNumber(vm.getInvoiceTaxNumber());
        or.setNewsletter(vm.getNewsletter());
        or.setDateOfApp(LocalDateTime.now());
        or.setDiscountCode(vm.getDiscountCode());
        or.setDiscountPercentage(vm.getDiscountPercentage());
        or.setDiscountType(vm.getDiscountType());
        or.setCongress(congress);

        OnlineRegistration result = repository.save(or);
        vm.getExtraRegTypes().forEach(ert -> {
            OnlineRegistrationRegistrationType orrt = new OnlineRegistrationRegistrationType();
            orrt.setRegistration(result);
            orrt.setRegistrationType(ert.getRegistrationType());
            final OnlineRegistrationRegistrationType orrtResult = orrtRepository.save(orrt);
            ert.getAccompanies().forEach(acc -> {
                AccPeopleOnline apo = new AccPeopleOnline();
                apo.setFirstName(acc.getFirstName());
                apo.setLastName(acc.getLastName());
                apo.setOnlineRegistrationRegistrationType(orrtResult);
                accPeopleOnlineRepository.save(apo);
            });
        });

        vm.getOptionalServices().forEach(os -> {
            OnlineRegistrationOptionalService oros = new OnlineRegistrationOptionalService();
            oros.setRegistration(result);
            oros.setOptionalService(os.getOptionalService());
            oros.setParticipant(os.getParticipants());
            final OnlineRegistrationOptionalService oos = orosRepository.save(oros);
            final OptionalService optionalService = osRepository.findById(oos.getOptionalService().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Optional service not found with id: " + oos.getOptionalService().getId()));
            oosService.increaseOptionalServiceReservedNumber(optionalService, oos.getParticipant());
        });

        LocalDate now = LocalDate.now();
        vm.getCustomAnswers().forEach(answer -> {
            OnlineRegistrationCustomAnswer orca = new OnlineRegistrationCustomAnswer();
            orca.setOnlineRegistration(result);
            orca.setQuestion(answer.getQuestion());
            orca.setAnswer(answer.getAnswer());
            orca.setCreatedDate(now);
            orcaRepository.save(orca);
        });

        if (result.getRoom() != null) {
            final Room room = roomService.getById(result.getRoom().getId());
            final Stream<LocalDate> range = Stream.iterate(result.getArrivalDate(), d -> d.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(result.getArrivalDate(), result.getDepartureDate()));
            range.forEach(localDate -> rrService.increaseRoomReservedNumber(room, localDate));
        }

        Locale locale = Locale.forLanguageTag(Currency.HUF.toString().equalsIgnoreCase(vm.getCurrency()) ? "hu" : "en");
        if (result.getEmail() != null) {
            int i = 0; //dummy shit for checkstyle
            //mailService.sendOnlineRegNotificationEmail(result.getEmail(), properties.getMail().getFrom(), congress.getMeetingCode(), result.getEmail(), locale);
        }

        return result;
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    public Registration accept(OnlineRegistrationVM vm) {
        final OnlineRegistration onlineReg = getById(vm.getId());
        Workplace workplace = null;
        if (onlineReg.getWorkplace() != null) {
            workplace = new Workplace();
            workplace.setName(onlineReg.getWorkplace());
            workplace.setDepartment(onlineReg.getDepartment());
            workplace.setZipCode(onlineReg.getZipCode());
            workplace.setCity(onlineReg.getCity());
            workplace.setStreet(onlineReg.getStreet());
            workplace.setCountry(onlineReg.getCountry());
            workplace.setPhone(onlineReg.getPhone());
            workplace.setEmail(onlineReg.getEmail());
            workplace.setCongress(onlineReg.getCongress());
            workplace = workplaceService.save(workplace);
        }

        Registration reg = new Registration();
        reg.setTitle(onlineReg.getTitle());
        reg.setLastName(onlineReg.getLastName());
        reg.setFirstName(onlineReg.getFirstName());
        reg.setShortName(StringUtils.hasText(onlineReg.getFirstName()) ? onlineReg.getFirstName().substring(0, 1).toUpperCase() + "." : "");
        reg.setPosition(onlineReg.getPosition());
        reg.setOtherData(onlineReg.getOtherData());
        reg.setWorkplace(workplace);
        reg.setDepartment(onlineReg.getDepartment());
        reg.setZipCode(onlineReg.getZipCode());
        reg.setCity(onlineReg.getCity());
        reg.setStreet(onlineReg.getStreet());
        reg.setCountry(onlineReg.getCountry());
        reg.setPhone(onlineReg.getPhone());
        reg.setEmail(onlineReg.getEmail());
        reg.setInvoiceName(onlineReg.getInvoiceName());
        reg.setInvoiceCountry(onlineReg.getInvoiceCountry());
        reg.setInvoiceZipCode(onlineReg.getInvoiceZipCode());
        reg.setInvoiceCity(onlineReg.getInvoiceCity());
        reg.setInvoiceAddress(onlineReg.getInvoiceAddress());
        reg.setInvoiceTaxNumber(onlineReg.getInvoiceTaxNumber());
        reg.setRemark(createRemark(onlineReg));
        reg.setCongress(onlineReg.getCongress());
        Registration result = registrationService.save(reg);

        if (onlineReg.getRegistrationType() != null) {
            RegistrationRegistrationType rrt = new RegistrationRegistrationType();
            rrt.setCreatedDate(LocalDate.now());
            rrt.setRegistrationType(onlineReg.getRegistrationType());
            rrt.setRegistration(result);
            rrt.setAccPeople(1);
            rrtService.setRegFee(rrt);
            rrtService.save(rrt);
        }

        List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(onlineReg);
        orrtList.forEach(orrt -> {
            final List<AccPeopleOnline> accPeopleOnlineList = accPeopleOnlineRepository.findAllByOnlineRegistrationRegistrationType(orrt);
            RegistrationRegistrationType rrt = new RegistrationRegistrationType();
            rrt.setCreatedDate(LocalDate.now());
            rrt.setRegistrationType(orrt.getRegistrationType());
            rrt.setRegistration(result);
            rrt.setAccPeople(accPeopleOnlineList.size());
            rrtService.setRegFee(rrt);
            final RegistrationRegistrationType rrtResult = rrtService.save(rrt);
            accPeopleOnlineList.forEach(accPeopleOnline -> {
                AccPeople accPeople = new AccPeople();
                accPeople.setLastName(accPeopleOnline.getLastName());
                accPeople.setFirstName(accPeopleOnline.getFirstName());
                accPeople.setRegistrationRegistrationType(rrtResult);
                accPeopleRepository.save(accPeople);
            });
        });

        if (onlineReg.getRoom() != null) {
            RoomReservation rr = new RoomReservation();
            rr.setRoom(onlineReg.getRoom());
            rr.setArrivalDate(onlineReg.getArrivalDate());
            rr.setDepartureDate(onlineReg.getDepartureDate());
            rr.setShared(false);
            final RoomReservation rrResult = rrRepository.save(rr);

            RoomReservationRegistration rrr = new RoomReservationRegistration();
            rrr.setCreatedDate(LocalDate.now());
            rrr.setRegistration(result);
            rrr.setRoomReservation(rrResult);
            rrr.setComment(onlineReg.getRoomRemark());
            rrrRepository.save(rrr);
        }

        List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineReg);
        orosList.forEach(oros -> {
            OrderedOptionalService oos = new OrderedOptionalService();
            oos.setCreatedDate(LocalDate.now());
            oos.setOptionalService(oros.getOptionalService());
            oos.setParticipant(oros.getParticipant());
            oos.setRegistration(result);
            oosRepository.save(oos);
        });

        List<OnlineRegistrationCustomAnswer> orcaList = orcaRepository.findAllByOnlineRegistrationIdOrderByQuestionQuestionOrderAsc(onlineReg.getId());
        orcaList.forEach(orca -> {
            orca.setOnlineRegistration(null);
            orca.setRegistration(result);
            orcaRepository.save(orca);
        });

        delete(onlineReg.getId());
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegistrationVM get(OnlineRegistration onlineReg) {
        OnlineRegistrationVM vm = new OnlineRegistrationVM();
        vm.setId(onlineReg.getId());
        vm.setTitle(onlineReg.getTitle());
        vm.setLastName(onlineReg.getLastName());
        vm.setFirstName(onlineReg.getFirstName());
        vm.setPosition(onlineReg.getPosition());
        vm.setOtherData(onlineReg.getOtherData());
        vm.setWorkplace(onlineReg.getWorkplace());
        vm.setDepartment(onlineReg.getDepartment());
        vm.setZipCode(onlineReg.getZipCode());
        vm.setCity(onlineReg.getCity());
        vm.setStreet(onlineReg.getStreet());
        vm.setCountry(onlineReg.getCountry());
        vm.setPhone(onlineReg.getPhone());
        vm.setEmail(onlineReg.getEmail());
        vm.setRegistrationType(onlineReg.getRegistrationType());
        vm.setRoom(onlineReg.getRoom());
        vm.setArrivalDate(onlineReg.getArrivalDate());
        vm.setDepartureDate(onlineReg.getDepartureDate());
        vm.setRoommate(onlineReg.getRoommate());
        vm.setRoomRemark(onlineReg.getRoomRemark());
        vm.setUuid(onlineReg.getCongress().getUuid());
        vm.setPaymentMethod(onlineReg.getPaymentMethod());
        vm.setInvoiceName(onlineReg.getInvoiceName());
        vm.setInvoiceCountry(onlineReg.getInvoiceCountry());
        vm.setInvoiceZipCode(onlineReg.getInvoiceZipCode());
        vm.setInvoiceCity(onlineReg.getInvoiceCity());
        vm.setInvoiceAddress(onlineReg.getInvoiceAddress());
        vm.setInvoiceReferenceNumber(onlineReg.getInvoiceReferenceNumber());
        vm.setInvoiceTaxNumber(onlineReg.getInvoiceTaxNumber());
        vm.setCheckName(onlineReg.getCheckName());
        vm.setCheckAddress(onlineReg.getCheckAddress());
        vm.setCurrency(onlineReg.getCurrency());

        final List<OnlineRegRegTypeVM> orrtVMList = new ArrayList<>();
        List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(onlineReg);
        orrtList.forEach(orrt -> {
            final OnlineRegRegTypeVM orrtVM = new OnlineRegRegTypeVM();
            orrtVM.setRegistrationType(orrt.getRegistrationType());
            final List<AccPeopleOnline> accPeopleOnlineList = accPeopleOnlineRepository.findAllByOnlineRegistrationRegistrationType(orrt);
            orrtVM.setAccompanies(accPeopleOnlineList.stream().map(accPeopleOnline -> {
                AccPeopleVM accPeople = new AccPeopleVM();
                accPeople.setLastName(accPeopleOnline.getLastName());
                accPeople.setFirstName(accPeopleOnline.getFirstName());
                return accPeople;
            }).collect(Collectors.toList()));
            orrtVMList.add(orrtVM);
        });
        vm.setExtraRegTypes(orrtVMList);

        List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineReg);
        vm.setOptionalServices(orosList.stream().map(oros -> {
            OnlineRegOptionalServiceVM orosVM = new OnlineRegOptionalServiceVM();
            orosVM.setOptionalService(oros.getOptionalService());
            orosVM.setParticipants(oros.getParticipant());
            return orosVM;
        }).collect(Collectors.toList()));

        return vm;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        final OnlineRegistration or = getById(id);
        final List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(or);
        orrtList.forEach(orrt -> accPeopleOnlineRepository.deleteAll(accPeopleOnlineRepository.findAllByOnlineRegistrationRegistrationType(orrt)));
        orrtRepository.deleteAll(orrtList);
        orosRepository.deleteAll(orosRepository.findAllByRegistration(or));
        repository.delete(or);
    }

    private String createRemark(OnlineRegistration onlineReg) {
        StringBuilder sb = new StringBuilder();
        final String lineFeed = "\n";
        sb.append(StringUtils.hasText(onlineReg.getRoommate()) ? "Roommate:" + onlineReg.getRoommate() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getPaymentMethod()) ? "Payment method:" + onlineReg.getPaymentMethod() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCheckName()) ? "Check name:" + onlineReg.getCheckName() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCheckAddress()) ? "Check address:" + onlineReg.getCheckAddress() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCardHolderName()) ? "Card holder name:" + onlineReg.getCardHolderName() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCardHolderAddress()) ? "Card holder address:" + onlineReg.getCardHolderAddress() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCardNumber()) ? "Card number:" + onlineReg.getCardNumber() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCardExpiryMonth()) ? "Card expiry month:" + onlineReg.getCardExpiryMonth() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getCardExpiryYear()) ? "Card expiry year:" + onlineReg.getCardExpiryYear() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getInvoiceReferenceNumber()) ? "Invoice reference number:" + onlineReg.getInvoiceReferenceNumber() + lineFeed : "");
        sb.append(StringUtils.hasText(onlineReg.getDiscountCode()) ? "Discount code:" + onlineReg.getDiscountCode() + lineFeed : "");
        sb.append(TRUE.equals(onlineReg.getNewsletter()) ? "Newsletter:" + onlineReg.getNewsletter() + lineFeed : "");

        BigDecimal regSubTotal = getRegistrationTypeSubTotalAmountOfOnlineReg(onlineReg);
        BigDecimal roomSubTotal = getHotelAmountOfOnlineReg(onlineReg);
        BigDecimal osSubTotal = getOptionalServiceTotalAmountOfOnlineReg(onlineReg);
        String currency = onlineReg.getCurrency();
        if ("CARD".equals(onlineReg.getPaymentMethod())) {
            sb.append(StringUtils.hasText(onlineReg.getCardNumber()) ? "Payment trx amount: " + regSubTotal.add(roomSubTotal).add(osSubTotal).toString() + lineFeed : "");
            sb.append(StringUtils.hasText(onlineReg.getPaymentTrxId()) ? "Payment order No: " + onlineReg.getPaymentOrderNumber() + lineFeed : "");
            sb.append(StringUtils.hasText(onlineReg.getPaymentTrxId()) ? "Payment trx ID: " + onlineReg.getPaymentTrxId() + lineFeed : "");
            sb.append(StringUtils.hasText(onlineReg.getPaymentMethod()) ? "Payment trx method: " + onlineReg.getPaymentMethod() + lineFeed : "");
            sb.append("Payment merchant ID: ").append(Currency.HUF.toString().equalsIgnoreCase(currency) ? applicationProperties.getPayment().getGateway().getMerchantIdForHUF()
                    : applicationProperties.getPayment().getGateway().getMerchantIdForEUR()).append(lineFeed);
            sb.append("Payment trx status: ").append(getPaymentTrxStatusMessage(onlineReg)).append(lineFeed);
            sb.append("Payment trx response: " + "PU Payment" + lineFeed);
            sb.append(StringUtils.hasText(onlineReg.getPaymentTrxAuthCode()) ? "Payment trx auth code: " + onlineReg.getPaymentTrxAuthCode() + lineFeed : "");
            sb.append(onlineReg.getPaymentTrxDate() != null ? "Payment trx time: " + onlineReg.getPaymentTrxDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + lineFeed : "");
        }
        return sb.toString();
    }

    @SuppressWarnings("InnerAssignment")
    private String getPaymentTrxStatusMessage(OnlineRegistration onlineReg) {
        String status = "Unsuccessful";
        if (onlineReg.getPaymentTrxStatus() != null) {
            final String successful = "Successful";
            switch (onlineReg.getPaymentTrxStatus()) {
                case "PAYMENT_WAITING_FOR_SETTLEMENT" -> status = successful;
                case "PAYMENT_SETTLED" -> status = successful;
                default -> throw new IllegalStateException("Unexpected payment trx status, value: " + onlineReg.getPaymentTrxStatus());
            }
        }
        return status;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public byte[] getPdf(OnlineRegistration onlineReg) {
        final List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(onlineReg);
        final List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineReg);

        return pdfService.generatePdf(new OnlineRegPdfContext(onlineReg, orrtList, orosList));
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public byte[] getAllPdf(List<OnlineRegistration> orList) {
        final String errorCreatingAllOnlinePdf = "Error while creating all online reg pdf";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfCopyFields copy = new PdfCopyFields(baos);
            copy.open();
            for (OnlineRegistration or : orList) {
                try {
                    // assuming getPdf(or) returns byte[] for a single registration PDF
                    byte[] pdfBytes = getPdf(or);
                    PdfReader reader = new PdfReader(pdfBytes);

                    copy.addDocument(reader);   // <- this exists on PdfCopyFields
                    reader.close();
                } catch (DocumentException | IOException e) {
                    log.error(errorCreatingAllOnlinePdf, e);
                }
            }

            copy.close(); // writes final merged PDF into baos
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(errorCreatingAllOnlinePdf, e);
            return null;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getRegistrationTypeSubTotalAmountOfOnlineReg(OnlineRegistration registration) {
        BigDecimal total = BigDecimal.ZERO;
        List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(registration);

        if (registration.getRegistrationType() != null) {
            final RegistrationType rt = rtRepository.findById(registration.getRegistrationType().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Registration type not found with id: " + registration.getRegistrationType().getId()));
            total = total.add(rrtService.calculateRegFee(rt, registration.getDateOfApp().toLocalDate()));
        }

        final BigDecimal orrtSubTotal = orrtList.stream().map(orrt -> {
            final List<AccPeopleOnline> accPeopleOnlineList = accPeopleOnlineRepository.findAllByOnlineRegistrationRegistrationType(orrt);
            return rrtService.calculateRegFee(orrt.getRegistrationType(), registration.getDateOfApp().toLocalDate())
                    .multiply(BigDecimal.valueOf(Math.max(1, accPeopleOnlineList.size())));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        total = total.add(orrtSubTotal);

        if (registration.getDiscountPercentage() != null && registration.getDiscountPercentage() > 0) {
            BigDecimal discountMultiplier = BigDecimal.valueOf(registration.getDiscountPercentage().doubleValue() / 100);
            if (registration.getDiscountType() == null || ChargeableItemType.REGISTRATION.equals(registration.getDiscountType())) {
                total = total.subtract(total.multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP));
            }
        }

        return total;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getHotelAmountOfOnlineReg(OnlineRegistration registration) {
        BigDecimal total = BigDecimal.ZERO;
        if (registration.getRoom() != null) {
            final Room room = roomService.getById(registration.getRoom().getId());
            BigDecimal subTotal = room.getPrice().multiply(BigDecimal.valueOf(DAYS.between(registration.getArrivalDate(), registration.getDepartureDate())));
            if (registration.getDiscountPercentage() != null && registration.getDiscountPercentage() > 0) {
                BigDecimal discountMultiplier = BigDecimal.valueOf(registration.getDiscountPercentage().doubleValue() / 100);
                if (registration.getDiscountType() == null || ChargeableItemType.HOTEL.equals(registration.getDiscountType())) {
                    subTotal = subTotal.subtract(subTotal.multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP));
                }
            }
            total = total.add(subTotal);
        }

        return total;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getOptionalServiceTotalAmountOfOnlineReg(OnlineRegistration registration) {
        BigDecimal total = BigDecimal.ZERO;
        List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(registration);
        if (!orosList.isEmpty()) {
            BigDecimal subTotal = orosList.stream().map(oros -> oros.getOptionalService().getPrice().multiply(new BigDecimal(oros.getParticipant())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (registration.getDiscountPercentage() != null && registration.getDiscountPercentage() > 0) {
                BigDecimal discountMultiplier = BigDecimal.valueOf(registration.getDiscountPercentage().doubleValue() / 100);
                if (registration.getDiscountType() == null || ChargeableItemType.OPTIONAL_SERVICE.equals(registration.getDiscountType())) {
                    subTotal = subTotal.subtract(subTotal.multiply(discountMultiplier).setScale(0, RoundingMode.HALF_UP));
                }
            }
            total = total.add(subTotal);
        }
        return total;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getTotalAmountOfOnlineReg(OnlineRegistration registration) {
        return getRegistrationTypeSubTotalAmountOfOnlineReg(registration)
                .add(getHotelAmountOfOnlineReg(registration))
                .add(getOptionalServiceTotalAmountOfOnlineReg(registration));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void acceptAll(OnlineRegFilterVM onlineRegFilter) {
        onlineRegFilter.getOnlineRegIdList().forEach(id -> {
            accept(get(getById(id)));
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAll(OnlineRegFilterVM onlineRegFilter) {
        onlineRegFilter.getOnlineRegIdList().forEach(this::delete);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegDiscountCodeDTO getOnlineRegDiscountCode(String uuid, String code) {
        return discountCodeRepository.findOneByCongressUuidAndCode(uuid, code).map(OnlineRegDiscountCodeDTO::new)
            .orElseThrow(() -> new IllegalArgumentException("Online reg discount code not found with code: " + code));
    }
}
