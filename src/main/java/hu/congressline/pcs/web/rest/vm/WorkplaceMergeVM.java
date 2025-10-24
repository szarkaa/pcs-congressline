package hu.congressline.pcs.web.rest.vm;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WorkplaceMergeVM {
    private Long workplaceId;
    private List<Long> mergingWorkplaceIdList;
}
