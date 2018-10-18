package org.innovateuk.ifs.transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Helper component to allow services to explicitly flush pending database changes out to the database (without
 * committing at this stage)
 */
@Component
public class TransactionalHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TransactionalHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void flushWithNoCommit() {
        entityManager.flush();
    }
}
