package com.worth.ifs;

import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * This this the base class for Controller integration tests.  Subclasses will have access to a Controller and be able
 * to test it against a full stack of real REST services, transactional Services and Repositories.
 * <p>
 * Created by dwatson on 02/10/15.
 */
@Rollback
@Transactional
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseIntegrationTest {

    public Log LOG = LogFactory.getLog(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);

    protected UserResource getSteveSmith() {
        return getByEmail("steve.smith@empire.com");
    }

    protected UserResource getPeteTom() {
        return getByEmail("pete.tom@egg.com");
    }

    protected UserResource getCompAdmin() {
        return getByEmail("compadmin@innovateuk.test");
    }

    protected UserResource getPaulPlum() {
        return getByEmail("paul.plum@gmail.com");
    }

    protected UserResource getFelixWilson() {
        return getByEmail("felix.wilson@gmail.com");
    }

    protected UserResource getAnonUser() {
        return SecurityRuleUtil.getAnonymous();
    }

    protected UserResource getSystemRegistrationUser() {
        return getByEmail("ifs_web_user@innovateuk.org");
    }

    protected UserResource getByEmail(String email) {
        return userMapper.mapToResource(userRepository.findByEmail(email).get());
    }

    protected void loginSteveSmith() {
        setLoggedInUser(getSteveSmith());
    }

    protected void loginCompAdmin() {
        setLoggedInUser(getCompAdmin());
    }

    protected void loginSystemRegistrationUser() {
        setLoggedInUser(getSystemRegistrationUser());
    }

    protected void loginPeteTom() {
        setLoggedInUser(getPeteTom());
    }

    protected void loginPaulPlum() {
        setLoggedInUser(getPaulPlum());
    }

    protected void loginFelixWilson() {
        setLoggedInUser(getFelixWilson());
    }
}
