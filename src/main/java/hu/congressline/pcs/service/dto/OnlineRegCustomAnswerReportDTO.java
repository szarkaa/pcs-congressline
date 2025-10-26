package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegCustomAnswerReportDTO implements Serializable {
    private Long registrationId;
    private Integer regId;
    private String name;
    private String currency;
    private List<String> answers;
}
