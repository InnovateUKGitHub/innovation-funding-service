package org.innovateuk.ifs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the base class for testing Repositories against a real database.
 *
 * Tests can rollback their changes to the database using the @Rollback annotation.
 *
 */
@Transactional
public abstract class BaseRepositoryIntegrationTest<RepositoryType> extends BaseAuthenticationAwareIntegrationTest {

    protected RepositoryType repository;

    @Autowired
    protected abstract void setRepository(RepositoryType repository);
}
