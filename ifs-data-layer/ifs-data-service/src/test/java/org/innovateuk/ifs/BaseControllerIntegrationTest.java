package org.innovateuk.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

/**
 * This this the base class for Controller integration tests.  Subclasses will have access to a Controller and be able
 * to test it against a full stack of real REST services, transactional Services and Repositories.
 * <p>
 */
@Transactional
@Rollback
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseAuthenticationAwareIntegrationTest {

    public Log LOG = LogFactory.getLog(getClass());

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);
}
