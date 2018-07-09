package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.workflow.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
@Component
public class ProcessHistoryRepositoryProvider {

    private static ProcessHistoryRepository processHistoryRepository;

    static ProcessHistoryRepository processHistoryRepository() {
        if (ProcessHistoryRepositoryProvider.processHistoryRepository == null) {
            throw new IllegalStateException("processHistoryRepository not autowired in ProcessEntityListener");
        }
        return ProcessHistoryRepositoryProvider.processHistoryRepository;
    }

    @Autowired
    public void setProcessHistoryRepository(ProcessHistoryRepository processHistoryRepository) {
        ProcessHistoryRepositoryProvider.processHistoryRepository = processHistoryRepository;
    }
}