package hu.congressline.pcs.service.dto.publiccompanydata;

public record SearchResultItem(
    String id,
    String fullName,
    String shortName,
    String vatNumber,
    String status
) {

}
