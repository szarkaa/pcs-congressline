package hu.congressline.pcs.service.dto.publiccompanydata;

public record CompanyDetails(
    String fullName,
    String shortName,
    String legalForm,
    String fullAddress,
    String addressCounty,
    String addressPostalCode,
    String addressCity,
    String addressStreet,
    String vatNumber,
    String registryNumber,
    String dateFounded,
    String statisticalCode,
    String website,
    String phoneNumber,
    String emailAddress
) {

}
