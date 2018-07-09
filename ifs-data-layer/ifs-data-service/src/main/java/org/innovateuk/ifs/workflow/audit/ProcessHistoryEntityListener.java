package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.workflow.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PreUpdate;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
@Component
public class ProcessHistoryEntityListener {

    private static ProcessHistoryRepository processHistoryRepository;

    private static ProcessHistoryRepository getProcessHistoryRepository() {
        if (ProcessHistoryEntityListener.processHistoryRepository == null) {
            throw new IllegalStateException("processHistoryRepository not autowired in ProcessEntityListener");
        }
        return ProcessHistoryEntityListener.processHistoryRepository;
    }

    @Autowired
    private void setProcessHistoryRepository(ProcessHistoryRepository processHistoryRepository) {
        ProcessHistoryEntityListener.processHistoryRepository = processHistoryRepository;
    }

    @PreUpdate
    public void preUpdate(Process process) {
        getProcessHistoryRepository().save(new ProcessHistory(process));
    }
}