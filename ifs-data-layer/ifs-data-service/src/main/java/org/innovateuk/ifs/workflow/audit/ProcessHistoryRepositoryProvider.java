package org.innovateuk.ifs.workflow.audit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * TODO class comment
 */
@Component
public class ProcessHistoryRepositoryProvider implements ApplicationContextAware {


    private static volatile ApplicationContext applicationContext;

    public static ProcessHistoryRepository processHistoryRepository() {
        if (ProcessHistoryRepositoryProvider.applicationContext == null) {
            throw new IllegalStateException("applicationContext not autowired in ProcessEntityListener");
        }
        return ProcessHistoryRepositoryProvider.applicationContext.getBean(ProcessHistoryRepository.class);

    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProcessHistoryRepositoryProvider.applicationContext = applicationContext;
    }
}