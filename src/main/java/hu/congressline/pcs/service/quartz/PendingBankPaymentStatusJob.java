package hu.congressline.pcs.service.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import hu.congressline.pcs.service.OnlinePaymentService;
import hu.congressline.pcs.service.PaymentRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class PendingBankPaymentStatusJob implements Job {

    private final OnlinePaymentService onlinePaymentService;
    private final PaymentRefundService refundService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        onlinePaymentService.checkPendingPaymentResults();
        refundService.checkPendingRefundResults();
        log.debug("Pending bank payment status checker job done!");
    }
}
