package hu.congressline.pcs.service.dto.kh;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EchoResult extends SignBase {

    private static final long serialVersionUID = -3825192932302805075L;

    private int resultCode;
    private String resultMessage;

    @Override
    public String toSign() {
        StringBuilder sb = new StringBuilder();
        add(sb, dttm);
        add(sb, resultCode);
        add(sb, resultMessage);
        deleteLast(sb);
        return sb.toString();
    }

}
