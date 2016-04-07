package com.worth.ifs;

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
 *
 * Created by dwatson on 02/10/15.
 */
@Rollback
@Transactional
public abstract class BaseControllerIntegrationTest<ControllerType> extends BaseWebIntegrationTest {

    public Log LOG = LogFactory.getLog(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    protected ControllerType controller;

    @Autowired
    protected abstract void setControllerUnderTest(ControllerType controller);

    protected UserResource getSteveSmith() {
        return userMapper.mapToResource(userRepository.findByEmail("steve.smith@empire.com").get());
    }

    protected UserResource getPeteTom() {
        return userMapper.mapToResource(userRepository.findByEmail("pete.tom@egg.com").get());
    }

    protected void loginSteveSmith() {
        setLoggedInUser(getSteveSmith());
    }

    protected void loginPeteTom() {
        setLoggedInUser(getPeteTom());
    }
}
