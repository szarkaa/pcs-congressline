package hu.congressline.pcs.service.dto;

import java.util.Objects;

import hu.congressline.pcs.domain.VatInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class VatInfoDTO {
    private Long id;
    private String name;
    private Integer vat;
    private String szj;

    public VatInfoDTO(VatInfo vatInfo) {
        this.id = vatInfo.getId();
        this.name = vatInfo.getName();
        this.szj = vatInfo.getSzj();
        this.vat = vatInfo.getVat();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VatInfoDTO that = (VatInfoDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
