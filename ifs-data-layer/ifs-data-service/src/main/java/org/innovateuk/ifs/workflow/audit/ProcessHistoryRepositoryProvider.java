package org.innovateuk.ifs.workflow.audit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * TODO class comment
 */
@Component
public class ProcessHistoryRepositoryProvider implements ApplicationContextAware {

    private static volatile ApplicationContext applicationContext;

    public static Optional<ProcessHistoryRepository> processHistoryRepository() {
        return Optional.ofNullable(applicationContext == null ?
                null : ProcessHistoryRepositoryProvider.applicationContext.getBean(ProcessHistoryRepository.class));

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProcessHistoryRepositoryProvider.applicationContext = applicationContext;
    }
}