package hu.congressline.pcs.service.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class PendingBankPaymentStatusJob implements Job {

    //private final OnlineRegService onlineRegService;
    //private final PaymentRefundService refundService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //onlineRegService.checkPendingPaymentResults();
        //refundService.checkPendingRefundResults();
        log.debug("Pending bank payment status checker job done!");
    }
}
