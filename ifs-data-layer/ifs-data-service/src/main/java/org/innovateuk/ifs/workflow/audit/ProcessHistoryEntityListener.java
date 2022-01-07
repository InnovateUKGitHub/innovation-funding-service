package org.innovateuk.ifs.workflow.audit;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.PreUpdate;

import static org.innovateuk.ifs.workflow.audit.ProcessHistoryRepositoryProvider.processHistoryRepository;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
@Slf4j
public class ProcessHistoryEntityListener {

    @PreUpdate
    public void preUpdate(Process process) {
        try {
            processHistoryRepository().save(new ProcessHistory(process));
        }
        catch (IllegalStateException e) {
            // this is to workaround issues when running unit tests in bamboo in cases where
            // the application context has been invalidated
            // see https://devops.innovateuk.org/issue-tracking/browse/IFS-3924
            log.warn("Exception saving ProcessHistory", e);
        }
    }
}