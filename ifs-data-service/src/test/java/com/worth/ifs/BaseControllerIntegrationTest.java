package com.worth.ifs;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * This this the base class for Controller integration tests.  Subclasses will have access to a Controller and be able
 * to test it against a full stack of real REST services, transactional Services and Repositories.
 *
 * Created by dwatson on 02/10/15.
 */
@Rollback
@Transactional
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseWebIntegrationTest {

    public Log LOG = LogFactory.getLog(getClass());

    @Autowired
    private UserRepository userRepository;

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);

    protected User getSteveSmith() {
        return userRepository.findByEmail("steve.smith@empire.com").get();
    }

    protected User getPeteTom() {
        return userRepository.findByEmail("pete.tom@egg.com").get();
    }

    protected void loginSteveSmith() {
        setLoggedInUser(getSteveSmith());
    }

    protected void loginPeteTom() {
        setLoggedInUser(getPeteTom());
    }
}
