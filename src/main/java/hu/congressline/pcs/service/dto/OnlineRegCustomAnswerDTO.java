package hu.congressline.pcs.service.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegCustomAnswerDTO implements Serializable {
    private String question;
    private String answer;
}
