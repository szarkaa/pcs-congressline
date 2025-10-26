package hu.congressline.pcs.service.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WorkplaceMergeDTO {
    private Long workplaceId;
    private List<Long> mergingWorkplaceIdList;
}
