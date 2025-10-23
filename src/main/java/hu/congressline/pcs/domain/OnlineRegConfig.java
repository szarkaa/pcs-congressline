package hu.congressline.pcs.domain;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.PaymentSupplier;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "online_reg_config")
public class OnlineRegConfig {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    private Congress congress;

    @ManyToOne
    private Country defaultCountry;

    @Size(max = 255)
    @Column(name = "header_normal_name")
    private String headerNormalName;

    @Size(max = 10485760)
    @Lob
    @Column(name = "header_normal", length = 10485760, columnDefinition = "MEDIUMBLOB")
    private byte[] headerNormalFile;

    @Column(name = "header_normal_content_type")
    private String headerNormalContentType;

    @NotNull
    @Column(name = "closed")
    private boolean closed;

    @NotNull
    @Column(name = "no_payment_required")
    private boolean noPaymentRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_supplier")
    private PaymentSupplier paymentSupplier;

    @Column(name = "stripe_secret_key")
    private String stripeSecretKey;

    @Column(name = "stripe_public_key")
    private String stripePublicKey;

    @Size(max = 16)
    @Column(name = "colorCode", length = 16)
    private String colorCode;

    @Size(max = 60000)
    @Column(name = "browser_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String browserRemarkHu;

    @Size(max = 60000)
    @Column(name = "browser_remark_en", columnDefinition = "TEXT", length = 60000)
    private String browserRemarkEn;

    @Size(max = 60000)
    @Column(name = "browser_remark_es", columnDefinition = "TEXT", length = 60000)
    private String browserRemarkEs;

    @Size(max = 60000)
    @Column(name = "browser_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String browserRemarkPt;

    @NotNull
    @Column(name = "last_name_visible")
    private boolean lastNameVisible;

    @NotNull
    @Column(name = "last_name_required")
    private boolean lastNameRequired;

    @NotNull
    @Column(name = "first_name_visible")
    private boolean firstNameVisible;

    @NotNull
    @Column(name = "first_name_required")
    private boolean firstNameRequired;

    @NotNull
    @Column(name = "title_visible")
    private boolean titleVisible;

    @NotNull
    @Column(name = "title_required")
    private boolean titleRequired;

    @NotNull
    @Column(name = "title_selectable")
    private boolean titleSelectable;

    @NotNull
    @Column(name = "position_visible")
    private boolean positionVisible;

    @NotNull
    @Column(name = "position_required")
    private boolean positionRequired;

    @Size(max = 200)
    @Column(name = "position_label_hu", columnDefinition = "TEXT", length = 200)
    private String positionLabelHu;

    @Size(max = 200)
    @Column(name = "position_label_en", columnDefinition = "TEXT", length = 200)
    private String positionLabelEn;

    @Size(max = 200)
    @Column(name = "position_label_es", columnDefinition = "TEXT", length = 200)
    private String positionLabelEs;

    @Size(max = 200)
    @Column(name = "position_label_pt", columnDefinition = "TEXT", length = 200)
    private String positionLabelPt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_position_hu_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> positionHuValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_position_en_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> positionEnValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_position_es_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> positionEsValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_position_pt_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> positionPtValues;

    @NotNull
    @Column(name = "workplace_visible")
    private boolean workplaceVisible;

    @NotNull
    @Column(name = "workplace_required")
    private boolean workplaceRequired;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_workplace_hu_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> workplaceHuValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_workplace_en_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> workplaceEnValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_workplace_es_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> workplaceEsValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_workplace_pt_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> workplacePtValues;

    @NotNull
    @Column(name = "department_visible")
    private boolean departmentVisible;

    @NotNull
    @Column(name = "department_required")
    private boolean departmentRequired;

    @NotNull
    @Column(name = "zip_code_visible")
    private boolean zipCodeVisible;

    @NotNull
    @Column(name = "zip_code_required")
    private boolean zipCodeRequired;

    @NotNull
    @Column(name = "country_visible")
    private boolean countryVisible;

    @NotNull
    @Column(name = "country_required")
    private boolean countryRequired;

    @NotNull
    @Column(name = "city_visible")
    private boolean cityVisible;

    @NotNull
    @Column(name = "city_required")
    private boolean cityRequired;

    @NotNull
    @Column(name = "street_visible")
    private boolean streetVisible;

    @NotNull
    @Column(name = "street_required")
    private boolean streetRequired;

    @NotNull
    @Column(name = "phone_visible")
    private boolean phoneVisible;

    @NotNull
    @Column(name = "phone_required")
    private boolean phoneRequired;

    @NotNull
    @Column(name = "email_visible")
    private boolean emailVisible;

    @NotNull
    @Column(name = "email_required")
    private boolean emailRequired;

    @NotNull
    @Column(name = "fax_visible")
    private boolean faxVisible;

    @NotNull
    @Column(name = "fax_required")
    private boolean faxRequired;

    @NotNull
    @Column(name = "other_data_visible")
    private boolean otherDataVisible;

    @NotNull
    @Column(name = "other_data_required")
    private boolean otherDataRequired;

    @Size(max = 200)
    @Column(name = "other_data_label_hu", length = 200)
    private String otherDataLabelHu;

    @Size(max = 200)
    @Column(name = "other_data_label_en", length = 200)
    private String otherDataLabelEn;

    @Size(max = 200)
    @Column(name = "other_data_label_es", length = 200)
    private String otherDataLabelEs;

    @Size(max = 200)
    @Column(name = "other_data_label_pt", length = 200)
    private String otherDataLabelPt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_other_data_hu_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> otherDataHuValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_other_data_en_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> otherDataEnValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_other_data_es_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> otherDataEsValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_other_data_pt_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> otherDataPtValues;

    @Size(max = 100)
    @Column(name = "reg_type_first_fee_label_hu", length = 100)
    private String regTypeFirstFeeLabelHu;

    @Size(max = 100)
    @Column(name = "reg_type_first_fee_label_en", length = 100)
    private String regTypeFirstFeeLabelEn;

    @Size(max = 100)
    @Column(name = "reg_type_first_fee_label_es", length = 100)
    private String regTypeFirstFeeLabelEs;

    @Size(max = 100)
    @Column(name = "reg_type_first_fee_label_pt", length = 100)
    private String regTypeFirstFeeLabelPt;

    @Size(max = 100)
    @Column(name = "reg_type_second_fee_label_hu", length = 100)
    private String regTypeSecondFeeLabelHu;

    @Size(max = 100)
    @Column(name = "reg_type_second_fee_label_en", length = 100)
    private String regTypeSecondFeeLabelEn;

    @Size(max = 100)
    @Column(name = "reg_type_second_fee_label_es", length = 100)
    private String regTypeSecondFeeLabelEs;

    @Size(max = 100)
    @Column(name = "reg_type_second_fee_label_pt", length = 100)
    private String regTypeSecondFeeLabelPt;

    @Size(max = 100)
    @Column(name = "reg_type_third_fee_label_hu", length = 100)
    private String regTypeThirdFeeLabelHu;

    @Size(max = 100)
    @Column(name = "reg_type_third_fee_label_en", length = 100)
    private String regTypeThirdFeeLabelEn;

    @Size(max = 100)
    @Column(name = "reg_type_third_fee_label_es", length = 100)
    private String regTypeThirdFeeLabelEs;

    @Size(max = 100)
    @Column(name = "reg_type_third_fee_label_pt", length = 100)
    private String regTypeThirdFeeLabelPt;

    @NotNull
    @Column(name = "roommate_required_visible")
    private boolean roommateRequiredVisible;

    @NotNull
    @Column(name = "special_booking_request_visible")
    private boolean specialBookingRequiredVisible;

    @Size(max = 255)
    @Column(name = "reg_type_extra_title_hu")
    private String regTypeExtraTitleHu;

    @Size(max = 255)
    @Column(name = "reg_type_extra_title_en")
    private String regTypeExtraTitleEn;

    @Size(max = 255)
    @Column(name = "reg_type_extra_title_es")
    private String regTypeExtraTitleEs;

    @Size(max = 255)
    @Column(name = "reg_type_extra_title_pt")
    private String regTypeExtraTitlePt;

    @Size(max = 255)
    @Column(name = "optional_service_extra_title_hu")
    private String optionalServiceExtraTitleHu;

    @Size(max = 255)
    @Column(name = "optional_service_extra_title_en")
    private String optionalServiceExtraTitleEn;

    @Size(max = 255)
    @Column(name = "optional_service_extra_title_es")
    private String optionalServiceExtraTitleEs;

    @Size(max = 255)
    @Column(name = "optional_service_extra_title_pt")
    private String optionalServiceExtraTitlePt;

    @Size(max = 60000)
    @Column(name = "personal_data_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String personalDataRemarkHu;

    @Size(max = 60000)
    @Column(name = "personal_data_remark_en", columnDefinition = "TEXT", length = 60000)
    private String personalDataRemarkEn;

    @Size(max = 60000)
    @Column(name = "personal_data_remark_es", columnDefinition = "TEXT", length = 60000)
    private String personalDataRemarkEs;

    @Size(max = 60000)
    @Column(name = "personal_data_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String personalDataRemarkPt;

    @Size(max = 60000)
    @Column(name = "reg_type_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String regTypeRemarkHu;

    @Size(max = 60000)
    @Column(name = "reg_type_remark_en", columnDefinition = "TEXT", length = 60000)
    private String regTypeRemarkEn;

    @Size(max = 60000)
    @Column(name = "reg_type_remark_es", columnDefinition = "TEXT", length = 60000)
    private String regTypeRemarkEs;

    @Size(max = 60000)
    @Column(name = "reg_type_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String regTypeRemarkPt;

    @Size(max = 60000)
    @Column(name = "room_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String roomRemarkHu;

    @Size(max = 60000)
    @Column(name = "room_remark_en", columnDefinition = "TEXT", length = 60000)
    private String roomRemarkEn;

    @Size(max = 60000)
    @Column(name = "room_remark_es", columnDefinition = "TEXT", length = 60000)
    private String roomRemarkEs;

    @Size(max = 60000)
    @Column(name = "room_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String roomRemarkPt;

    @Size(max = 60000)
    @Column(name = "optional_service_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String optionalServiceRemarkHu;

    @Size(max = 60000)
    @Column(name = "optional_service_remark_en", columnDefinition = "TEXT", length = 60000)
    private String optionalServiceRemarkEn;

    @Size(max = 60000)
    @Column(name = "optional_service_remark_es", columnDefinition = "TEXT", length = 60000)
    private String optionalServiceRemarkEs;

    @Size(max = 60000)
    @Column(name = "optional_service_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String optionalServiceRemarkPt;

    @NotNull
    @Column(name = "bank_transfer_visible")
    private boolean bankTransferVisible = true;

    @NotNull
    @Column(name = "check_visible")
    private boolean checkVisible = true;

    @NotNull
    @Column(name = "credit_card_visible")
    private boolean creditCardVisible = true;

    @Size(max = 60000)
    @Column(name = "billing_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String billingRemarkHu;

    @Size(max = 60000)
    @Column(name = "billing_remark_en", columnDefinition = "TEXT", length = 60000)
    private String billingRemarkEn;

    @Size(max = 60000)
    @Column(name = "billing_remark_es", columnDefinition = "TEXT", length = 60000)
    private String billingRemarkEs;

    @Size(max = 60000)
    @Column(name = "billing_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String billingRemarkPt;

    @Size(max = 60000)
    @Column(name = "bank_transfer_info_hu", columnDefinition = "TEXT", length = 60000)
    private String bankTransferInfoHu;

    @Size(max = 60000)
    @Column(name = "bank_transfer_info_en", columnDefinition = "TEXT", length = 60000)
    private String bankTransferInfoEn;

    @Size(max = 60000)
    @Column(name = "bank_transfer_info_es", columnDefinition = "TEXT", length = 60000)
    private String bankTransferInfoEs;

    @Size(max = 60000)
    @Column(name = "bank_transfer_info_pt", columnDefinition = "TEXT", length = 60000)
    private String bankTransferInfoPt;

    @Size(max = 60000)
    @Column(name = "terms_and_conditions_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String termsAndConditionsRemarkHu;

    @Size(max = 60000)
    @Column(name = "terms_and_conditions_remark_en", columnDefinition = "TEXT", length = 60000)
    private String termsAndConditionsRemarkEn;

    @Size(max = 60000)
    @Column(name = "terms_and_conditions_remark_es", columnDefinition = "TEXT", length = 60000)
    private String termsAndConditionsRemarkEs;

    @Size(max = 60000)
    @Column(name = "terms_and_conditions_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String termsAndConditionsRemarkPt;

    @Size(max = 60000)
    @Column(name = "gdpr_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String gdprRemarkHu;

    @Size(max = 60000)
    @Column(name = "gdpr_remark_en", columnDefinition = "TEXT", length = 60000)
    private String gdprRemarkEn;

    @Size(max = 60000)
    @Column(name = "gdpr_remark_es", columnDefinition = "TEXT", length = 60000)
    private String gdprRemarkEs;

    @Size(max = 60000)
    @Column(name = "gdpr_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String gdprRemarkPt;

    @Size(max = 60000)
    @Column(name = "marketing_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String marketingRemarkHu;

    @Size(max = 60000)
    @Column(name = "marketing_remark_en", columnDefinition = "TEXT", length = 60000)
    private String marketingRemarkEn;

    @Size(max = 60000)
    @Column(name = "marketing_remark_es", columnDefinition = "TEXT", length = 60000)
    private String marketingRemarkEs;

    @Size(max = 60000)
    @Column(name = "marketing_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String marketingRemarkPt;

    @Size(max = 60000)
    @Column(name = "conditions_and_granting_remark_hu", columnDefinition = "TEXT", length = 60000)
    private String conditionsAndGrantingRemarkHu;

    @Size(max = 60000)
    @Column(name = "conditions_and_granting_remark_en", columnDefinition = "TEXT", length = 60000)
    private String conditionsAndGrantingRemarkEn;

    @Size(max = 60000)
    @Column(name = "conditions_and_granting_remark_es", columnDefinition = "TEXT", length = 60000)
    private String conditionsAndGrantingRemarkEs;

    @Size(max = 60000)
    @Column(name = "conditions_and_granting_remark_pt", columnDefinition = "TEXT", length = 60000)
    private String conditionsAndGrantingRemarkPt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OnlineRegConfig)) {
            return false;
        }

        OnlineRegConfig that = (OnlineRegConfig) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
