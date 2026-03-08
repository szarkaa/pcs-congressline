package hu.congressline.pcs.service.dto.publiccompanydata;

public record SearchResponse(
    boolean status,
    SearchPayload response,
    String error,
    Integer errorCode,
    Integer statusCode
) {

}
