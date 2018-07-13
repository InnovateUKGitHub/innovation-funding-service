package org.innovateuk.ifs.workflow.audit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Provides a static reference to a {@link ProcessHistoryRepository}.
 */
@Component
class ProcessHistoryRepositoryProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    static ProcessHistoryRepository processHistoryRepository() {
        if (applicationContext == null) {
            throw new IllegalStateException("null applicationContext in ProcessHistoryRepositoryProvider");
        }
        ProcessHistoryRepository processHistoryRepository =
                ProcessHistoryRepositoryProvider.applicationContext.getBean(ProcessHistoryRepository.class);
        if (processHistoryRepository == null) {
            throw new IllegalStateException("No ProcessHistoryRepository found in application context: " + applicationContext);
        }
        return processHistoryRepository;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ProcessHistoryRepositoryProvider.applicationContext = applicationContext;
    }
}