package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Base class for integration tests that need to provide authenticate users.
 */
public abstract class BaseAuthenticationAwareIntegrationTest extends BaseIntegrationTest {

    public static final int USER_COUNT = 19;
    public static final List<String> ALL_USERS_EMAIL = asList(
            "steve.smith@empire.com",
            "jessica.doe@ludlow.co.uk",
            "paul.plum@gmail.com",
            "competitions@innovateuk.gov.uk",
            "finance@innovateuk.gov.uk",
            "pete.tom@egg.com",
            "felix.wilson@gmail.com",
            "ewan+1@hiveit.co.uk",
            "ifs_web_user@innovateuk.org",
            "compadmin@innovateuk.test",
            "comp_exec1@innovateuk.test",
            "comp_exec2@innovateuk.test",
            "comp_technologist1@innovateuk.test",
            "comp_technologist2@innovateuk.test",
            "ifsadmin@innovateuk.test",
            "pfifs2100@gmail.vom",
            "pmifs2100@gmail.vom");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    protected EntityManager em;

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

    protected UserResource getIfsAdmin() {
        return getByEmail("ifsadmin@innovateuk.test");
    }

    protected UserResource getSuperAdminUser() {
        return getByEmail("bucky.barnes@innovateuk.test");
    }

    protected UserResource getAnonUser() {
        return SecurityRuleUtil.getAnonymous();
    }

    protected UserResource getSystemRegistrationUser() {
        return getByEmail("ifs_web_user@innovateuk.org");
    }

    protected UserResource getSystemMaintenanceUser() {
        return getByEmail("ifs_system_maintenance_user@innovateuk.org");
    }

    protected UserResource getByEmail(String email) {
        return userMapper.mapToResource(userRepository.findByEmail(email).orElse(null));
    }

    protected User getUserByEmail(String email) {return userRepository.findByEmail(email).orElse(null);}

    protected void loginSteveSmith() {
        setLoggedInUser(getSteveSmith());
    }

    protected void loginCompAdmin() {
        setLoggedInUser(getCompAdmin());
    }

    protected void loginIfsAdmin() {
        setLoggedInUser(getIfsAdmin());
    }

    protected void loginSystemRegistrationUser() {
        setLoggedInUser(getSystemRegistrationUser());
    }

    protected void loginSystemMaintenanceUser() {
        setLoggedInUser(getSystemMaintenanceUser());
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

    protected void loginSuperAdmin() {
        setLoggedInUser(getSuperAdminUser());
    }

    protected void flushAndClearSession() {
        em.flush();
        em.clear();
    }
}
