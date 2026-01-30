package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.repository.BankAccountRepository;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.OnlineRegConfigRepository;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.security.RandomUtil;
import hu.congressline.pcs.service.dto.OnlineRegConfigDTO;
import hu.congressline.pcs.service.util.RichTextHtmlCodec;
import hu.congressline.pcs.web.rest.vm.CongressVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CongressService {

    private final CongressRepository congressRepository;
    private final OnlineRegConfigRepository onlineRegConfigRepository;
    private final OnlineRegCustomQuestionRepository onlineRegCustomQuestionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CurrencyService currencyService;
    private final CountryRepository countryRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public List<Congress> findAllCongresses() {
        return congressRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Congress> findById(Long id) {
        log.debug("Request to find Congress : {}", id);
        return congressRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getById(Long id) {
        log.debug("Request to get Congress : {}", id);
        return congressRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Congress not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getEagerById(Long id) {
        log.debug("Request to get Congress eagerly: {}", id);
        return congressRepository.findOneEagerlyById(id).orElseThrow(() -> new IllegalArgumentException("Congress eager not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getByMeetingCode(String meetingCode) {
        log.debug("Request to get Congress by meeting code: {}", meetingCode);
        return congressRepository.findOneByMeetingCode(meetingCode).orElseThrow(() -> new IllegalArgumentException("Congress not found with meeting code: " + meetingCode));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        onlineRegCustomQuestionRepository.deleteAllByCongressId(id);
        onlineRegConfigRepository.deleteAllByCongressId(id);
        congressRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress persist(CongressVM viewModel) {
        Congress congress = new Congress();
        congress.setUuid(RandomUtil.generateRandomAlphanumericString());
        congress.update(viewModel);
        congress.setDefaultCountry(viewModel.getDefaultCountryId() != null ? countryRepository.findById(viewModel.getDefaultCountryId()).orElse(null) : null);
        congress.setCurrencies(currencyService.getAllByIds(viewModel.getCurrencyIds()));
        congress.setOnlineRegCurrencies(currencyService.getAllByIds(viewModel.getOnlineRegCurrencyIds()));
        congress.setBankAccounts(new HashSet<>(bankAccountRepository.findAllById(viewModel.getBankAccountIds())));
        Congress result = congressRepository.save(congress);
        OnlineRegConfig config = new OnlineRegConfig();
        config.setCongress(result);
        onlineRegConfigRepository.save(config);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress update(CongressVM viewModel) {
        Congress congress = getById(viewModel.getId());
        congress.setUuid(RandomUtil.generateRandomAlphanumericString());
        congress.update(viewModel);
        congress.setDefaultCountry(viewModel.getDefaultCountryId() != null ? countryRepository.findById(viewModel.getDefaultCountryId()).orElse(null) : null);
        congress.setCurrencies(currencyService.getAllByIds(viewModel.getCurrencyIds()));
        congress.setOnlineRegCurrencies(currencyService.getAllByIds(viewModel.getOnlineRegCurrencyIds()));
        congress.setBankAccounts(new HashSet<>(bankAccountRepository.findAllById(viewModel.getBankAccountIds())));
        return congressRepository.save(congress);

    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress save(Congress congress) {
        return congressRepository.save(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<OnlineRegConfig> findConfigByCongressId(Long id) {
        return onlineRegConfigRepository.findOneByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegConfig getConfigByCongressId(Long id) {
        return onlineRegConfigRepository.findOneByCongressId(id).orElseThrow(() -> new IllegalArgumentException("Online reg config not found by id: " + id));
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    public OnlineRegConfig saveOnlineRegConfig(OnlineRegConfigDTO viewModel) {
        final OnlineRegConfig onlineRegConfig = getConfigByCongressId(viewModel.getCongressId());

        onlineRegConfig.setDefaultCountry(viewModel.getDefaultCountryId() != null ? countryRepository.findById(viewModel.getDefaultCountryId()).orElse(null) : null);
        onlineRegConfig.setHeaderNormalName(viewModel.getHeaderNormalName());
        onlineRegConfig.setHeaderNormalFile(viewModel.getHeaderNormalFile());
        onlineRegConfig.setHeaderNormalContentType(viewModel.getHeaderNormalContentType());
        onlineRegConfig.setClosed(viewModel.isClosed());
        onlineRegConfig.setNoPaymentRequired(viewModel.isNoPaymentRequired());
        onlineRegConfig.setPaymentSupplier(viewModel.getPaymentSupplier());
        onlineRegConfig.setStripeSecretKey(viewModel.getStripeSecretKey());
        onlineRegConfig.setStripePublicKey(viewModel.getStripePublicKey());
        onlineRegConfig.setColorCode(viewModel.getColorCode());
        onlineRegConfig.setBrowserRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getBrowserRemarkHu()));
        onlineRegConfig.setBrowserRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getBrowserRemarkEn()));
        onlineRegConfig.setBrowserRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getBrowserRemarkEs()));
        onlineRegConfig.setBrowserRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getBrowserRemarkPt()));
        onlineRegConfig.setLastNameVisible(viewModel.isLastNameVisible());
        onlineRegConfig.setLastNameRequired(viewModel.isLastNameRequired());
        onlineRegConfig.setFirstNameVisible(viewModel.isFirstNameVisible());
        onlineRegConfig.setFirstNameRequired(viewModel.isFirstNameRequired());
        onlineRegConfig.setTitleVisible(viewModel.isTitleVisible());
        onlineRegConfig.setTitleRequired(viewModel.isTitleRequired());
        onlineRegConfig.setTitleSelectable(viewModel.isTitleSelectable());
        onlineRegConfig.setPositionVisible(viewModel.isPositionVisible());
        onlineRegConfig.setPositionRequired(viewModel.isPositionRequired());
        onlineRegConfig.setPositionLabelHu(viewModel.getPositionLabelHu());
        onlineRegConfig.setPositionLabelEn(viewModel.getPositionLabelEn());
        onlineRegConfig.setPositionLabelEs(viewModel.getPositionLabelEs());
        onlineRegConfig.setPositionLabelPt(viewModel.getPositionLabelPt());
        onlineRegConfig.setPositionHuValues(viewModel.getPositionHuValues());
        onlineRegConfig.setPositionEnValues(viewModel.getPositionEnValues());
        onlineRegConfig.setPositionEsValues(viewModel.getPositionEsValues());
        onlineRegConfig.setPositionPtValues(viewModel.getPositionPtValues());
        onlineRegConfig.setWorkplaceVisible(viewModel.isWorkplaceVisible());
        onlineRegConfig.setWorkplaceRequired(viewModel.isWorkplaceRequired());
        onlineRegConfig.setWorkplaceHuValues(viewModel.getWorkplaceHuValues());
        onlineRegConfig.setWorkplaceEnValues(viewModel.getWorkplaceEnValues());
        onlineRegConfig.setWorkplaceEsValues(viewModel.getWorkplaceEsValues());
        onlineRegConfig.setWorkplacePtValues(viewModel.getWorkplacePtValues());
        onlineRegConfig.setDepartmentVisible(viewModel.isDepartmentVisible());
        onlineRegConfig.setDepartmentRequired(viewModel.isDepartmentRequired());
        onlineRegConfig.setZipCodeVisible(viewModel.isZipCodeVisible());
        onlineRegConfig.setZipCodeRequired(viewModel.isZipCodeRequired());
        onlineRegConfig.setCountryVisible(viewModel.isCountryVisible());
        onlineRegConfig.setCountryRequired(viewModel.isCountryRequired());
        onlineRegConfig.setCityVisible(viewModel.isCityVisible());
        onlineRegConfig.setCityRequired(viewModel.isCityRequired());
        onlineRegConfig.setStreetVisible(viewModel.isStreetVisible());
        onlineRegConfig.setStreetRequired(viewModel.isStreetRequired());
        onlineRegConfig.setPhoneVisible(viewModel.isPhoneVisible());
        onlineRegConfig.setPhoneRequired(viewModel.isPhoneRequired());
        onlineRegConfig.setEmailVisible(viewModel.isEmailVisible());
        onlineRegConfig.setEmailRequired(viewModel.isEmailRequired());
        onlineRegConfig.setFaxVisible(viewModel.isFaxVisible());
        onlineRegConfig.setFaxRequired(viewModel.isFaxRequired());
        onlineRegConfig.setOtherDataVisible(viewModel.isOtherDataVisible());
        onlineRegConfig.setOtherDataRequired(viewModel.isOtherDataRequired());
        onlineRegConfig.setOtherDataLabelHu(viewModel.getOtherDataLabelHu());
        onlineRegConfig.setOtherDataLabelEn(viewModel.getOtherDataLabelEn());
        onlineRegConfig.setOtherDataLabelEs(viewModel.getOtherDataLabelEs());
        onlineRegConfig.setOtherDataLabelPt(viewModel.getOtherDataLabelPt());
        onlineRegConfig.setOtherDataHuValues(viewModel.getOtherDataHuValues());
        onlineRegConfig.setOtherDataEnValues(viewModel.getOtherDataEnValues());
        onlineRegConfig.setOtherDataEsValues(viewModel.getOtherDataEsValues());
        onlineRegConfig.setOtherDataPtValues(viewModel.getOtherDataPtValues());
        onlineRegConfig.setRegTypeFirstFeeLabelHu(viewModel.getRegTypeFirstFeeLabelHu());
        onlineRegConfig.setRegTypeFirstFeeLabelEn(viewModel.getRegTypeFirstFeeLabelEn());
        onlineRegConfig.setRegTypeFirstFeeLabelEs(viewModel.getRegTypeFirstFeeLabelEs());
        onlineRegConfig.setRegTypeFirstFeeLabelPt(viewModel.getRegTypeFirstFeeLabelPt());
        onlineRegConfig.setRegTypeSecondFeeLabelHu(viewModel.getRegTypeSecondFeeLabelHu());
        onlineRegConfig.setRegTypeSecondFeeLabelEn(viewModel.getRegTypeSecondFeeLabelEn());
        onlineRegConfig.setRegTypeSecondFeeLabelEs(viewModel.getRegTypeSecondFeeLabelEs());
        onlineRegConfig.setRegTypeSecondFeeLabelPt(viewModel.getRegTypeSecondFeeLabelPt());
        onlineRegConfig.setRegTypeThirdFeeLabelHu(viewModel.getRegTypeThirdFeeLabelHu());
        onlineRegConfig.setRegTypeThirdFeeLabelEn(viewModel.getRegTypeThirdFeeLabelEn());
        onlineRegConfig.setRegTypeThirdFeeLabelEs(viewModel.getRegTypeThirdFeeLabelEs());
        onlineRegConfig.setRegTypeThirdFeeLabelPt(viewModel.getRegTypeThirdFeeLabelPt());
        onlineRegConfig.setRoommateRequiredVisible(viewModel.isRoommateRequiredVisible());
        onlineRegConfig.setSpecialBookingRequiredVisible(viewModel.isSpecialBookingRequiredVisible());
        onlineRegConfig.setRegTypeExtraTitleHu(viewModel.getRegTypeExtraTitleHu());
        onlineRegConfig.setRegTypeExtraTitleEn(viewModel.getRegTypeExtraTitleEn());
        onlineRegConfig.setRegTypeExtraTitleEs(viewModel.getRegTypeExtraTitleEs());
        onlineRegConfig.setRegTypeExtraTitlePt(viewModel.getRegTypeExtraTitlePt());
        onlineRegConfig.setOptionalServiceExtraTitleHu(viewModel.getOptionalServiceExtraTitleHu());
        onlineRegConfig.setOptionalServiceExtraTitleEn(viewModel.getOptionalServiceExtraTitleEn());
        onlineRegConfig.setOptionalServiceExtraTitleEs(viewModel.getOptionalServiceExtraTitleEs());
        onlineRegConfig.setOptionalServiceExtraTitlePt(viewModel.getOptionalServiceExtraTitlePt());
        onlineRegConfig.setPersonalDataRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getPersonalDataRemarkHu()));
        onlineRegConfig.setPersonalDataRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getPersonalDataRemarkEn()));
        onlineRegConfig.setPersonalDataRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getPersonalDataRemarkEs()));
        onlineRegConfig.setPersonalDataRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getPersonalDataRemarkPt()));
        onlineRegConfig.setRegTypeRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getRegTypeRemarkHu()));
        onlineRegConfig.setRegTypeRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getRegTypeRemarkEn()));
        onlineRegConfig.setRegTypeRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getRegTypeRemarkEs()));
        onlineRegConfig.setRegTypeRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getRegTypeRemarkPt()));
        onlineRegConfig.setRoomRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getRoomRemarkHu()));
        onlineRegConfig.setRoomRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getRoomRemarkEn()));
        onlineRegConfig.setRoomRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getRoomRemarkEs()));
        onlineRegConfig.setRoomRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getRoomRemarkPt()));
        onlineRegConfig.setOptionalServiceRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getOptionalServiceRemarkHu()));
        onlineRegConfig.setOptionalServiceRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getOptionalServiceRemarkEn()));
        onlineRegConfig.setOptionalServiceRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getOptionalServiceRemarkEs()));
        onlineRegConfig.setOptionalServiceRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getOptionalServiceRemarkPt()));
        onlineRegConfig.setBankTransferVisible(viewModel.isBankTransferVisible());
        onlineRegConfig.setCheckVisible(viewModel.isCheckVisible());
        onlineRegConfig.setCreditCardVisible(viewModel.isCreditCardVisible());
        onlineRegConfig.setBillingRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getBillingRemarkHu()));
        onlineRegConfig.setBillingRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getBillingRemarkEn()));
        onlineRegConfig.setBillingRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getBillingRemarkEs()));
        onlineRegConfig.setBillingRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getBillingRemarkPt()));
        onlineRegConfig.setBankTransferInfoHu(RichTextHtmlCodec.encodeForDb(viewModel.getBankTransferInfoHu()));
        onlineRegConfig.setBankTransferInfoEn(RichTextHtmlCodec.encodeForDb(viewModel.getBankTransferInfoEn()));
        onlineRegConfig.setBankTransferInfoEs(RichTextHtmlCodec.encodeForDb(viewModel.getBankTransferInfoEs()));
        onlineRegConfig.setBankTransferInfoPt(RichTextHtmlCodec.encodeForDb(viewModel.getBankTransferInfoPt()));
        onlineRegConfig.setTermsAndConditionsRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getTermsAndConditionsRemarkHu()));
        onlineRegConfig.setTermsAndConditionsRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getTermsAndConditionsRemarkEn()));
        onlineRegConfig.setTermsAndConditionsRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getTermsAndConditionsRemarkEs()));
        onlineRegConfig.setTermsAndConditionsRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getTermsAndConditionsRemarkPt()));
        onlineRegConfig.setGdprRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getGdprRemarkHu()));
        onlineRegConfig.setGdprRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getGdprRemarkEn()));
        onlineRegConfig.setGdprRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getGdprRemarkEs()));
        onlineRegConfig.setGdprRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getGdprRemarkPt()));
        onlineRegConfig.setMarketingRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getMarketingRemarkHu()));
        onlineRegConfig.setMarketingRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getMarketingRemarkEn()));
        onlineRegConfig.setMarketingRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getMarketingRemarkEs()));
        onlineRegConfig.setMarketingRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getMarketingRemarkPt()));
        onlineRegConfig.setConditionsAndGrantingRemarkHu(RichTextHtmlCodec.encodeForDb(viewModel.getConditionsAndGrantingRemarkHu()));
        onlineRegConfig.setConditionsAndGrantingRemarkEn(RichTextHtmlCodec.encodeForDb(viewModel.getConditionsAndGrantingRemarkEn()));
        onlineRegConfig.setConditionsAndGrantingRemarkEs(RichTextHtmlCodec.encodeForDb(viewModel.getConditionsAndGrantingRemarkEs()));
        onlineRegConfig.setConditionsAndGrantingRemarkPt(RichTextHtmlCodec.encodeForDb(viewModel.getConditionsAndGrantingRemarkPt()));
        return onlineRegConfigRepository.save(onlineRegConfig);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Set<Currency> getOnlineRegCurrenciesByCongressId(Long id) {
        return findById(id).map(Congress::getOnlineRegCurrencies).orElse(new HashSet<>());
    }
}
