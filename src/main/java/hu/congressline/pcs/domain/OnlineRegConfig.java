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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "closed")
    private Boolean closed;

    @Column(name = "no_payment_required")
    private Boolean noPaymentRequired;

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

    @Column(name = "last_name_visible")
    private Boolean lastNameVisible;

    @Column(name = "last_name_required")
    private Boolean lastNameRequired;

    @Column(name = "first_name_visible")
    private Boolean firstNameVisible;

    @Column(name = "first_name_required")
    private Boolean firstNameRequired;

    @Column(name = "title_visible")
    private Boolean titleVisible;

    @Column(name = "title_required")
    private Boolean titleRequired;

    @Column(name = "title_selectable")
    private Boolean titleSelectable;

    @Column(name = "position_visible")
    private Boolean positionVisible;

    @Column(name = "position_required")
    private Boolean positionRequired;

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

    @Column(name = "workplace_visible")
    private Boolean workplaceVisible;

    @Column(name = "workplace_required")
    private Boolean workplaceRequired;

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

    @Column(name = "department_visible")
    private Boolean departmentVisible;

    @Column(name = "department_required")
    private Boolean departmentRequired;

    @Column(name = "zip_code_visible")
    private Boolean zipCodeVisible;

    @Column(name = "zip_code_required")
    private Boolean zipCodeRequired;

    @Column(name = "country_visible")
    private Boolean countryVisible;

    @Column(name = "country_required")
    private Boolean countryRequired;

    @Column(name = "city_visible")
    private Boolean cityVisible;

    @Column(name = "city_required")
    private Boolean cityRequired;

    @Column(name = "street_visible")
    private Boolean streetVisible;

    @Column(name = "street_required")
    private Boolean streetRequired;

    @Column(name = "phone_visible")
    private Boolean phoneVisible;

    @Column(name = "phone_required")
    private Boolean phoneRequired;

    @Column(name = "email_visible")
    private Boolean emailVisible;

    @Column(name = "email_required")
    private Boolean emailRequired;

    @Column(name = "fax_visible")
    private Boolean faxVisible;

    @Column(name = "fax_required")
    private Boolean faxRequired;

    @Column(name = "other_data_visible")
    private Boolean otherDataVisible;

    @Column(name = "other_data_required")
    private Boolean otherDataRequired;

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

    @Column(name = "custom1_data_visible")
    private Boolean custom1DataVisible;

    @Column(name = "custom1_data_required")
    private Boolean custom1DataRequired;

    @Size(max = 200)
    @Column(name = "custom1_data_label_hu", length = 200)
    private String custom1DataLabelHu;

    @Size(max = 200)
    @Column(name = "custom1_data_label_en", length = 200)
    private String custom1DataLabelEn;

    @Size(max = 200)
    @Column(name = "custom1_data_label_es", length = 200)
    private String custom1DataLabelEs;

    @Size(max = 200)
    @Column(name = "custom1_data_label_pt", length = 200)
    private String custom1DataLabelPt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom1_data_hu_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom1DataHuValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom1_data_en_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom1DataEnValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom1_data_es_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom1DataEsValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom1_data_pt_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom1DataPtValues;

    @Column(name = "custom2_data_visible")
    private Boolean custom2DataVisible;

    @Column(name = "custom2_data_required")
    private Boolean custom2DataRequired;

    @Size(max = 200)
    @Column(name = "custom2_data_label_hu", length = 200)
    private String custom2DataLabelHu;

    @Size(max = 200)
    @Column(name = "custom2_data_label_en", length = 200)
    private String custom2DataLabelEn;

    @Size(max = 200)
    @Column(name = "custom2_data_label_es", length = 200)
    private String custom2DataLabelEs;

    @Size(max = 200)
    @Column(name = "custom2_data_label_pt", length = 200)
    private String custom2DataLabelPt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom2_data_hu_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom2DataHuValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom2_data_en_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom2DataEnValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom2_data_es_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom2DataEsValues;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "online_reg_config_custom2_data_pt_values", joinColumns = @JoinColumn(name = "online_reg_config_id"))
    @Column(name = "value")
    private List<String> custom2DataPtValues;

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

    @Column(name = "roommate_required_visible")
    private Boolean roommateRequiredVisible;

    @Column(name = "special_booking_request_visible")
    private Boolean specialBookingRequiredVisible;

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

    @Column(name = "bank_transfer_visible")
    private Boolean bankTransferVisible = true;

    @Column(name = "check_visible")
    private Boolean checkVisible = true;

    @Column(name = "credit_card_visible")
    private Boolean creditCardVisible = true;

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

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    public static OnlineRegConfig copy(OnlineRegConfig onlineRegConfig) {
        OnlineRegConfig copy = new OnlineRegConfig();
        copy.setCongress(onlineRegConfig.getCongress());
        copy.setDefaultCountry(onlineRegConfig.getDefaultCountry());
        copy.setHeaderNormalName(onlineRegConfig.getHeaderNormalName());
        copy.setHeaderNormalFile(onlineRegConfig.getHeaderNormalFile());
        copy.setHeaderNormalContentType(onlineRegConfig.getHeaderNormalContentType());
        copy.setClosed(false);
        copy.setNoPaymentRequired(onlineRegConfig.getNoPaymentRequired());
        copy.setPaymentSupplier(onlineRegConfig.getPaymentSupplier());
        copy.setStripeSecretKey(onlineRegConfig.getStripeSecretKey());
        copy.setStripePublicKey(onlineRegConfig.getStripePublicKey());
        copy.setColorCode(onlineRegConfig.getColorCode());
        copy.setBrowserRemarkHu(onlineRegConfig.getBrowserRemarkHu());
        copy.setBrowserRemarkEn(onlineRegConfig.getBrowserRemarkEn());
        copy.setBrowserRemarkEs(onlineRegConfig.getBrowserRemarkEs());
        copy.setBrowserRemarkPt(onlineRegConfig.getBrowserRemarkPt());
        copy.setLastNameVisible(onlineRegConfig.getLastNameVisible());
        copy.setLastNameRequired(onlineRegConfig.getLastNameRequired());
        copy.setFirstNameVisible(onlineRegConfig.getFirstNameVisible());
        copy.setFirstNameRequired(onlineRegConfig.getFirstNameRequired());
        copy.setTitleVisible(onlineRegConfig.getTitleVisible());
        copy.setTitleRequired(onlineRegConfig.getTitleRequired());
        copy.setTitleSelectable(onlineRegConfig.getTitleSelectable());
        copy.setPositionVisible(onlineRegConfig.getPositionVisible());
        copy.setPositionRequired(onlineRegConfig.getPositionRequired());
        copy.setPositionLabelHu(onlineRegConfig.getPositionLabelHu());
        copy.setPositionLabelEn(onlineRegConfig.getPositionLabelEn());
        copy.setPositionLabelEs(onlineRegConfig.getPositionLabelEs());
        copy.setPositionLabelPt(onlineRegConfig.getPositionLabelPt());
        copy.setPositionHuValues(onlineRegConfig.getPositionHuValues());
        copy.setPositionEnValues(onlineRegConfig.getPositionEnValues());
        copy.setPositionEsValues(onlineRegConfig.getPositionEsValues());
        copy.setPositionPtValues(onlineRegConfig.getPositionPtValues());
        copy.setWorkplaceVisible(onlineRegConfig.getWorkplaceVisible());
        copy.setWorkplaceRequired(onlineRegConfig.getWorkplaceRequired());
        copy.setWorkplaceHuValues(onlineRegConfig.getWorkplaceHuValues());
        copy.setWorkplaceEnValues(onlineRegConfig.getWorkplaceEnValues());
        copy.setWorkplaceEsValues(onlineRegConfig.getWorkplaceEsValues());
        copy.setWorkplacePtValues(onlineRegConfig.getWorkplacePtValues());
        copy.setDepartmentVisible(onlineRegConfig.getDepartmentVisible());
        copy.setDepartmentRequired(onlineRegConfig.getDepartmentRequired());
        copy.setZipCodeVisible(onlineRegConfig.getZipCodeVisible());
        copy.setZipCodeRequired(onlineRegConfig.getZipCodeRequired());
        copy.setCountryVisible(onlineRegConfig.getCountryVisible());
        copy.setCountryRequired(onlineRegConfig.getCountryRequired());
        copy.setCityVisible(onlineRegConfig.getCityVisible());
        copy.setCityRequired(onlineRegConfig.getCityRequired());
        copy.setStreetVisible(onlineRegConfig.getStreetVisible());
        copy.setStreetRequired(onlineRegConfig.getStreetRequired());
        copy.setPhoneVisible(onlineRegConfig.getPhoneVisible());
        copy.setPhoneRequired(onlineRegConfig.getPhoneRequired());
        copy.setEmailVisible(onlineRegConfig.getEmailVisible());
        copy.setEmailRequired(onlineRegConfig.getEmailRequired());
        copy.setFaxVisible(onlineRegConfig.getFaxVisible());
        copy.setFaxRequired(onlineRegConfig.getFaxRequired());
        copy.setOtherDataVisible(onlineRegConfig.getOtherDataVisible());
        copy.setOtherDataRequired(onlineRegConfig.getOtherDataRequired());
        copy.setOtherDataLabelHu(onlineRegConfig.getOtherDataLabelHu());
        copy.setOtherDataLabelEn(onlineRegConfig.getOtherDataLabelEn());
        copy.setOtherDataLabelEs(onlineRegConfig.getOtherDataLabelEs());
        copy.setOtherDataLabelPt(onlineRegConfig.getOtherDataLabelPt());
        copy.setOtherDataHuValues(onlineRegConfig.getOtherDataHuValues());
        copy.setOtherDataEnValues(onlineRegConfig.getOtherDataEnValues());
        copy.setOtherDataEsValues(onlineRegConfig.getOtherDataEsValues());
        copy.setOtherDataPtValues(onlineRegConfig.getOtherDataPtValues());
        copy.setCustom1DataVisible(onlineRegConfig.getCustom1DataVisible());
        copy.setCustom1DataRequired(onlineRegConfig.getCustom1DataRequired());
        copy.setCustom1DataLabelHu(onlineRegConfig.getCustom1DataLabelHu());
        copy.setCustom1DataLabelEn(onlineRegConfig.getCustom1DataLabelEn());
        copy.setCustom1DataLabelEs(onlineRegConfig.getCustom1DataLabelEs());
        copy.setCustom1DataLabelPt(onlineRegConfig.getCustom1DataLabelPt());
        copy.setCustom1DataHuValues(onlineRegConfig.getCustom1DataHuValues());
        copy.setCustom1DataEnValues(onlineRegConfig.getCustom1DataEnValues());
        copy.setCustom1DataEsValues(onlineRegConfig.getCustom1DataEsValues());
        copy.setCustom1DataPtValues(onlineRegConfig.getCustom1DataPtValues());
        copy.setCustom2DataVisible(onlineRegConfig.getCustom2DataVisible());
        copy.setCustom2DataRequired(onlineRegConfig.getCustom2DataRequired());
        copy.setCustom2DataLabelHu(onlineRegConfig.getCustom2DataLabelHu());
        copy.setCustom2DataLabelEn(onlineRegConfig.getCustom2DataLabelEn());
        copy.setCustom2DataLabelEs(onlineRegConfig.getCustom2DataLabelEs());
        copy.setCustom2DataLabelPt(onlineRegConfig.getCustom2DataLabelPt());
        copy.setCustom2DataHuValues(onlineRegConfig.getCustom2DataHuValues());
        copy.setCustom2DataEnValues(onlineRegConfig.getCustom2DataEnValues());
        copy.setCustom2DataEsValues(onlineRegConfig.getCustom2DataEsValues());
        copy.setCustom2DataPtValues(onlineRegConfig.getCustom2DataPtValues());
        copy.setRegTypeFirstFeeLabelHu(onlineRegConfig.getRegTypeFirstFeeLabelHu());
        copy.setRegTypeFirstFeeLabelEn(onlineRegConfig.getRegTypeFirstFeeLabelEn());
        copy.setRegTypeFirstFeeLabelEs(onlineRegConfig.getRegTypeFirstFeeLabelEs());
        copy.setRegTypeFirstFeeLabelPt(onlineRegConfig.getRegTypeFirstFeeLabelPt());
        copy.setRegTypeSecondFeeLabelHu(onlineRegConfig.getRegTypeSecondFeeLabelHu());
        copy.setRegTypeSecondFeeLabelEn(onlineRegConfig.getRegTypeSecondFeeLabelEn());
        copy.setRegTypeSecondFeeLabelEs(onlineRegConfig.getRegTypeSecondFeeLabelEs());
        copy.setRegTypeSecondFeeLabelPt(onlineRegConfig.getRegTypeSecondFeeLabelPt());
        copy.setRegTypeThirdFeeLabelHu(onlineRegConfig.getRegTypeThirdFeeLabelHu());
        copy.setRegTypeThirdFeeLabelEn(onlineRegConfig.getRegTypeThirdFeeLabelEn());
        copy.setRegTypeThirdFeeLabelEs(onlineRegConfig.getRegTypeThirdFeeLabelEs());
        copy.setRegTypeThirdFeeLabelPt(onlineRegConfig.getRegTypeThirdFeeLabelPt());
        copy.setRoommateRequiredVisible(onlineRegConfig.getRoommateRequiredVisible());
        copy.setSpecialBookingRequiredVisible(onlineRegConfig.getSpecialBookingRequiredVisible());
        copy.setRegTypeExtraTitleHu(onlineRegConfig.getRegTypeExtraTitleHu());
        copy.setRegTypeExtraTitleEn(onlineRegConfig.getRegTypeExtraTitleEn());
        copy.setRegTypeExtraTitleEs(onlineRegConfig.getRegTypeExtraTitleEs());
        copy.setRegTypeExtraTitlePt(onlineRegConfig.getRegTypeExtraTitlePt());
        copy.setOptionalServiceExtraTitleHu(onlineRegConfig.getOptionalServiceExtraTitleHu());
        copy.setOptionalServiceExtraTitleEn(onlineRegConfig.getOptionalServiceExtraTitleEn());
        copy.setOptionalServiceExtraTitleEs(onlineRegConfig.getOptionalServiceExtraTitleEs());
        copy.setOptionalServiceExtraTitlePt(onlineRegConfig.getOptionalServiceExtraTitlePt());
        copy.setPersonalDataRemarkHu(onlineRegConfig.getPersonalDataRemarkHu());
        copy.setPersonalDataRemarkEn(onlineRegConfig.getPersonalDataRemarkEn());
        copy.setPersonalDataRemarkEs(onlineRegConfig.getPersonalDataRemarkEs());
        copy.setPersonalDataRemarkPt(onlineRegConfig.getPersonalDataRemarkPt());
        copy.setRegTypeRemarkHu(onlineRegConfig.getRegTypeRemarkHu());
        copy.setRegTypeRemarkEn(onlineRegConfig.getRegTypeRemarkEn());
        copy.setRegTypeRemarkEs(onlineRegConfig.getRegTypeRemarkEs());
        copy.setRegTypeRemarkPt(onlineRegConfig.getRegTypeRemarkPt());
        copy.setRoomRemarkHu(onlineRegConfig.getRoomRemarkHu());
        copy.setRoomRemarkEn(onlineRegConfig.getRoomRemarkEn());
        copy.setRoomRemarkEs(onlineRegConfig.getRoomRemarkEs());
        copy.setRoomRemarkPt(onlineRegConfig.getRoomRemarkPt());
        copy.setOptionalServiceRemarkHu(onlineRegConfig.getOptionalServiceRemarkHu());
        copy.setOptionalServiceRemarkEn(onlineRegConfig.getOptionalServiceRemarkEn());
        copy.setOptionalServiceRemarkEs(onlineRegConfig.getOptionalServiceRemarkEs());
        copy.setOptionalServiceRemarkPt(onlineRegConfig.getOptionalServiceRemarkPt());
        copy.setBankTransferVisible(onlineRegConfig.getBankTransferVisible());
        copy.setCheckVisible(onlineRegConfig.getCheckVisible());
        copy.setCreditCardVisible(onlineRegConfig.getCreditCardVisible());
        copy.setBillingRemarkHu(onlineRegConfig.getBillingRemarkHu());
        copy.setBillingRemarkEn(onlineRegConfig.getBillingRemarkEn());
        copy.setBillingRemarkEs(onlineRegConfig.getBillingRemarkEs());
        copy.setBillingRemarkPt(onlineRegConfig.getBillingRemarkPt());
        copy.setBankTransferInfoHu(onlineRegConfig.getBankTransferInfoHu());
        copy.setBankTransferInfoEn(onlineRegConfig.getBankTransferInfoEn());
        copy.setBankTransferInfoEs(onlineRegConfig.getBankTransferInfoEs());
        copy.setBankTransferInfoPt(onlineRegConfig.getBankTransferInfoPt());
        copy.setTermsAndConditionsRemarkHu(onlineRegConfig.getTermsAndConditionsRemarkHu());
        copy.setTermsAndConditionsRemarkEn(onlineRegConfig.getTermsAndConditionsRemarkEn());
        copy.setTermsAndConditionsRemarkEs(onlineRegConfig.getTermsAndConditionsRemarkEs());
        copy.setTermsAndConditionsRemarkPt(onlineRegConfig.getTermsAndConditionsRemarkPt());
        copy.setGdprRemarkHu(onlineRegConfig.getGdprRemarkHu());
        copy.setGdprRemarkEn(onlineRegConfig.getGdprRemarkEn());
        copy.setGdprRemarkEs(onlineRegConfig.getGdprRemarkEs());
        copy.setGdprRemarkPt(onlineRegConfig.getGdprRemarkPt());
        copy.setMarketingRemarkHu(onlineRegConfig.getMarketingRemarkHu());
        copy.setMarketingRemarkEn(onlineRegConfig.getMarketingRemarkEn());
        copy.setMarketingRemarkEs(onlineRegConfig.getMarketingRemarkEs());
        copy.setMarketingRemarkPt(onlineRegConfig.getMarketingRemarkPt());
        copy.setConditionsAndGrantingRemarkHu(onlineRegConfig.getConditionsAndGrantingRemarkHu());
        copy.setConditionsAndGrantingRemarkEn(onlineRegConfig.getConditionsAndGrantingRemarkEn());
        copy.setConditionsAndGrantingRemarkEs(onlineRegConfig.getConditionsAndGrantingRemarkEs());
        copy.setConditionsAndGrantingRemarkPt(onlineRegConfig.getConditionsAndGrantingRemarkPt());
        return copy;
    }

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
