package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.security.CompetitionPermissionRules;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupTemplateService;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupTemplateServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.MockitoAnnotations.initMocks;

public class CompetitionSetupTemplateServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupTemplateService> {
    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSetupTemplateService> getClassUnderTest() {
        return CompetitionSetupTemplateServiceImpl.class;
    }

    @Test
    public void allServiceFunctionsShouldBeAuthorizedForCompAdmin() {
        setLoggedInUser(newUserResource().withRoleGlobal(COMP_ADMIN).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
    }

    @Test
    public void allServiceFunctionsShouldBeAuthorizedForProjectFinance() {
        setLoggedInUser(newUserResource().withRoleGlobal(PROJECT_FINANCE).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
    }

    @Test(expected = AccessDeniedException.class)
    public void initializeCompetitionByCompetitionTemplateShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
    }
}
