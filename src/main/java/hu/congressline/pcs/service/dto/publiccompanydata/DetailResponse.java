package hu.congressline.pcs.service.dto.publiccompanydata;

public record DetailResponse(
    boolean status,
    DetailPayload response,
    String error,
    Integer errorCode,
    Integer statusCode
) {

}
