package hu.congressline.pcs.web.rest.vm;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.Country;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegistrationVM implements Serializable {
    private Long id;
    private String lastName;
    private String firstName;
    private String title;
    private String position;
    private String workplace;
    private String otherData;
    private String department;
    private String zipCode;
    private String city;
    private String street;
    private String phone;
    private String email;
    private RegistrationType registrationType;
    private Room room;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String roommate;
    private String roomRemark;
    private String paymentMethod;
    private String cardType;
    private String checkName;
    private String checkAddress;
    private String cardHolderName;
    private String cardHolderAddress;
    private String cardNumber;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private String invoiceName;
    private Country invoiceCountry;
    private String invoiceZipCode;
    private String invoiceCity;
    private String invoiceAddress;
    private String invoiceReferenceNumber;
    private String invoiceTaxNumber;
    private Boolean newsletter;
    private Country country;
    private String discountCode;
    private Integer discountPercentage;
    private ChargeableItemType discountType;
    @NotNull
    private String currency;
    @NotNull
    private String uuid;
    private List<OnlineRegOptionalServiceVM> optionalServices;
    private List<OnlineRegCustomAnswerVM> customAnswers;
    private List<OnlineRegRegTypeVM> extraRegTypes;
}
