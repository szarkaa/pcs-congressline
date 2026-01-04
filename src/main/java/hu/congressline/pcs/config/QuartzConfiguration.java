package hu.congressline.pcs.config;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import hu.congressline.pcs.service.quartz.AutowiringSpringBeanJobFactory;
import hu.congressline.pcs.service.quartz.NavStatusJob;
import hu.congressline.pcs.service.quartz.PendingBankPaymentStatusJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
//@Configuration
public class QuartzConfiguration {

    private final DataSource dataSource;
    private final ApplicationProperties properties;

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, Trigger navCheckJobTrigger, Trigger pendingBankPaymentStatusJobTrigger) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        factory.setDataSource(dataSource);
        factory.setTriggers(navCheckJobTrigger, pendingBankPaymentStatusJobTrigger);
        log.info("starting jobs....");
        return factory;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public SimpleTriggerFactoryBean navCheckJobTrigger(@Qualifier("navStatusJobDetail") JobDetail jobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setName("navCheckJobTrigger");
        trigger.setGroup("navCheckJobTriggerGroup");
        trigger.setRepeatInterval(properties.getNav().getCheckingFrequencyInMillis());
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public SimpleTriggerFactoryBean pendingBankPaymentStatusJobTrigger(@Qualifier("pendingBankPaymentStatusJobDetail") JobDetail jobDetail) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setName("pendingBankPaymentStatusJobTrigger");
        trigger.setGroup("pendingBankPaymentStatusJobTriggerGroup");
        trigger.setRepeatInterval(properties.getPayment().getGateway().getCheckingFrequencyInMillis());
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public JobDetailFactoryBean navStatusJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(NavStatusJob.class);
        jobDetailFactory.setDescription("Invoke NAV Status Job service...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public JobDetailFactoryBean pendingBankPaymentStatusJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(PendingBankPaymentStatusJob.class);
        jobDetailFactory.setDescription("Invoke Pending Bank Payment Status Job service...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
