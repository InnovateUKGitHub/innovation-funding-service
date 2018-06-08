package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.security.CompetitionPermissionRules;
import org.innovateuk.ifs.competitionsetup.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.competitionsetup.transactional.TermsAndConditionsServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Collections.singletonList;
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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build());
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test(expected = AccessDeniedException.class)
    public void getLatestVersionsForAllTermsAndConditions_notLoggedIn() {
        setLoggedInUser(null);
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test(expected = AccessDeniedException.class)
    public void getLatestVersionsForAllTermsAndConditions_wrongUser() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());
        classUnderTest.getLatestVersionsForAllTermsAndConditions();
    }

    @Test
    public void getById() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build());
        classUnderTest.getById(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void getById_notLoggedIn() {
        setLoggedInUser(null);
        classUnderTest.getById(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void getById_wrongUser() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());
        classUnderTest.getById(1L);
    }
}
