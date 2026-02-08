package hu.congressline.pcs.service.dto.online;

import hu.congressline.pcs.domain.OnlineRegistration;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegistrationDTO {

    private Long id;
    private CongressDTO congress;

    public OnlineRegistrationDTO(OnlineRegistration onlineReg) {
        this.id = onlineReg.getId();
        this.congress = new CongressDTO(onlineReg.getCongress());
    }
}
