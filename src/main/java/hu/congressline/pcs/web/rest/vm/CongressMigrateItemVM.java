package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CongressMigrateItemVM {
    private Long from;
    private Long to;
    private boolean migrateWorkplace;
    private boolean migrateRegType;
    private boolean migrateHotel;
    private boolean migrateOptionalService;
    private boolean migrateOptionalText;
}
