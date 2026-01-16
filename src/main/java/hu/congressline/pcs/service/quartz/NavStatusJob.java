package hu.congressline.pcs.service.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import hu.congressline.pcs.service.NavService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class NavStatusJob implements Job {

    private final NavService navOnlineService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        navOnlineService.checkPendingInvoiceNavStatus();
        log.debug("NAV status checker job done!");
    }
}
