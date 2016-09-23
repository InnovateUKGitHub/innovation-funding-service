package com.worth.ifs;

import com.worth.ifs.commons.BaseIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 * This is the base class for testing Repositories against a real database.
 *
 * Tests can rollback their changes to the database using the @Rollback annotation.
 *
 * Created by dwatson on 02/10/15.
 */
@Transactional
public abstract class BaseRepositoryIntegrationTest<RepositoryType> extends BaseAuthenticationAwareIntegrationTest {

    protected RepositoryType repository;

    @Autowired
    private EntityManager em;

    protected void flushAndClearSession() {
        em.flush();
        em.clear();
    }

    @Autowired
    protected abstract void setRepository(RepositoryType repository);
}