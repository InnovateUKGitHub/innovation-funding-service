package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.workflow.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreUpdate;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
@Service
@Transactional
public class ProcessHistoryEntityListener {

    private static ProcessHistoryRepository processHistoryRepository;

    private static ProcessHistoryRepository getProcessHistoryRepository() {
        if (ProcessHistoryEntityListener.processHistoryRepository == null) {
            throw new IllegalStateException("processHistoryRepository not autowired in ProcessEntityListener");
        }
        return ProcessHistoryEntityListener.processHistoryRepository;
    }

    @Autowired
    public void setProcessHistoryRepository(ProcessHistoryRepository processHistoryRepository) {
        ProcessHistoryEntityListener.processHistoryRepository = processHistoryRepository;
    }

    @PreUpdate
    public void preUpdate(Process process) {
        getProcessHistoryRepository().save(new ProcessHistory(process));
    }
}