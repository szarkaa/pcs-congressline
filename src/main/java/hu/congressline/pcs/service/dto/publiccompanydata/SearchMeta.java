package hu.congressline.pcs.service.dto.publiccompanydata;

public record SearchMeta(
    Integer current,
    Integer total_pages,
    Integer total_results,
    Integer size) {

}

