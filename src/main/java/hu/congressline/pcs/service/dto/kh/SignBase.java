package hu.congressline.pcs.service.dto.kh;

import hu.congressline.pcs.service.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SignBase extends ApiBase implements Signable {

    private static final long serialVersionUID = -3825192932302805075L;

    protected String dttm;
    protected String signature;

    public void fillDttm() {
        this.dttm = DateUtil.getPaymentDateNow();
    }

}
