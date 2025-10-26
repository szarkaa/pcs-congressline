package hu.congressline.pcs.service.dto.online;

import java.util.List;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegCustomQuestionDTO {
    private Long id;
    private String question;
    private Boolean required;
    private Integer questionOrder;
    private List<String> questionAnswers;

    public OnlineRegCustomQuestionDTO(OnlineRegCustomQuestion question) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.required = question.getRequired();
        this.questionOrder = question.getQuestionOrder();
        this.questionAnswers = question.getQuestionAnswers();
    }
}
