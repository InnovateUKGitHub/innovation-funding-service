package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.workflow.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.persistence.PreUpdate;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
public class ProcessHistoryEntityListener {

    @Autowired
    private ProcessHistoryRepository processHistoryRepository;

    @PreUpdate
    public void preUpdate(Process process) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        processHistoryRepository.save(new ProcessHistory(process));
    }
}