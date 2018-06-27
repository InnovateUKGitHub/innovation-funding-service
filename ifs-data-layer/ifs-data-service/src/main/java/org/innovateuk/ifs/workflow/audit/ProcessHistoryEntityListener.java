package org.innovateuk.ifs.workflow.audit;

import org.innovateuk.ifs.workflow.domain.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * {@code EntityListener} to create new {@link ProcessHistory}s whenever a {@link Process} is updated.
 */
@Component
public class ProcessHistoryEntityListener {

    private static EntityManager entityManager;

    private static EntityManager getEntityManager() {
        if (ProcessHistoryEntityListener.entityManager == null) {
            throw new IllegalStateException("entityManager not autowired in ProcessEntityListener");
        }
        return ProcessHistoryEntityListener.entityManager;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        ProcessHistoryEntityListener.entityManager = entityManager;
    }

    @PreUpdate
    public void preUpdate(Process process) {
        getEntityManager().persist(new ProcessHistory(process));
    }
}