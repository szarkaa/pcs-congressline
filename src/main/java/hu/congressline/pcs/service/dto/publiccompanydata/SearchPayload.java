package hu.congressline.pcs.service.dto.publiccompanydata;

import java.util.List;

public record SearchPayload(
    SearchMeta meta,
    List<SearchResultItem> results
) {

}
