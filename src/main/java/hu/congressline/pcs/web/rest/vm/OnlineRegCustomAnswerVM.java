package hu.congressline.pcs.web.rest.vm;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegCustomAnswerVM {
    private OnlineRegCustomQuestion question;
    private String answer;

}
