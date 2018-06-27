package org.innovateuk.ifs.workflow.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
public class ProcessEntityListener {

    private static EntityManager entityManager;

    private static EntityManager getEntityManager() {
        if (ProcessEntityListener.entityManager == null) {
            throw new IllegalStateException("entityManager not autowired in ProcessEntityListener");
        }
        return ProcessEntityListener.entityManager;
    }

    @Autowired
    private void setEntityManager(EntityManager entityManager) {
        ProcessEntityListener.entityManager = entityManager;
    }

    @PreUpdate
    public void preUpdate(Process process) {
        getEntityManager().persist(new ProcessHistory(process));
    }
}