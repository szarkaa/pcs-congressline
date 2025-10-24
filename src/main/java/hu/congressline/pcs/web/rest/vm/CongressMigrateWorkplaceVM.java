package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CongressMigrateWorkplaceVM {
    private Long from;
    private Long to;
}
