package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import lombok.Data;
import lombok.NonNull;

@Data
public class OnlineRegCustomQuestionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String question;
    private Boolean required = Boolean.FALSE;
    private OnlineVisibility onlineVisibility;
    private Integer questionOrder;
    private List<String> questionAnswers;
    private StrippedCongressDTO congress;
    private CurrencyDTO currency;

    public OnlineRegCustomQuestionDTO(@NonNull OnlineRegCustomQuestion onlineRegCustomQuestion) {
        this.id = onlineRegCustomQuestion.getId();
        this.question = onlineRegCustomQuestion.getQuestion();
        this.required = onlineRegCustomQuestion.getRequired();
        this.onlineVisibility = onlineRegCustomQuestion.getOnlineVisibility();
        this.questionOrder = onlineRegCustomQuestion.getQuestionOrder();
        this.questionAnswers = onlineRegCustomQuestion.getQuestionAnswers();
        this.congress = new StrippedCongressDTO(onlineRegCustomQuestion.getCongress());
        this.currency = new CurrencyDTO(onlineRegCustomQuestion.getCurrency());
    }
}
