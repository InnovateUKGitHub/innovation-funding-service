package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.PreUpdate;

import static org.innovateuk.ifs.workflow.audit.ProcessHistoryRepositoryProvider.processHistoryRepository;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
public class ProcessHistoryEntityListener {

    @PreUpdate
    @NotSecured("not secured")
    public void preUpdate(Process process) {
        processHistoryRepository().save(new ProcessHistory(process));
    }
}