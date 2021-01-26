package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.mockito.MockitoAnnotations.initMocks;

public class TermsAndConditionsServiceSecurityTest extends BaseServiceSecurityTest<TermsAndConditionsService> {

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        initMocks(this);
    }

    @Override
    protected Class<? extends TermsAndConditionsService> getClassUnderTest() {
        return TermsAndConditionsServiceImpl.class;
    }

    @Test
    public void getLatestVersionsForAllTermsAndConditions() {
        setLoggedInUser(newUserResource().withRoleGlobal(COMP_ADMIN).build());
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test(expected = AccessDeniedException.class)
    public void getLatestVersionsForAllTermsAndConditions_notLoggedIn() {
        setLoggedInUser(null);
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test(expected = AccessDeniedException.class)
    public void getLatestVersionsForAllTermsAndConditions_wrongUser() {
        setLoggedInUser(newUserResource().withRoleGlobal(Role.APPLICANT).build());
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test
    public void getById() {
        setLoggedInUser(newUserResource().withRoleGlobal(COMP_ADMIN).build());
        classUnderTest.getById(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void getById_notLoggedIn() {
        setLoggedInUser(null);
        classUnderTest.getById(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void getById_wrongUser() {
        setLoggedInUser(newUserResource().withRoleGlobal(Role.APPLICANT).build());
        classUnderTest.getById(1L);
    }
}
