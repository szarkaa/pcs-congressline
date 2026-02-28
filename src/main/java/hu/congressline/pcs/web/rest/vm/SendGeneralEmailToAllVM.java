package hu.congressline.pcs.web.rest.vm;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendGeneralEmailToAllVM {
    private Long congressId;
    private Set<Long> registrationIds;
    private String topic;
    private String emailBody;
}
