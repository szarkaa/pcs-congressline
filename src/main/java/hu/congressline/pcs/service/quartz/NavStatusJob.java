package hu.congressline.pcs.service.quartz;

import lombok.RequiredArgsConstructor;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DisallowConcurrentExecution
public class NavStatusJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavStatusJob.class);

    //private NavOnlineService navOnlineService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //navOnlineService.checkPendingInvoiceNavStatus();
        LOGGER.debug("NAV status checker job done!");
    }
}
