package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.transactional.CompetitionTypeService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Testing the permission rules applied to the secured methods in CompetitionTypeService. This set of tests tests for the
 * individual rules that are called whenever an CompetitionTypeService method is called. They do not however test the logic
 * within those rules
 */
public class CompetitionTypeServiceSecurityTest extends BaseServiceSecurityTest<CompetitionTypeService> {

    private static final EnumSet<Role> NON_COMP_ADMIN_ROLES = complementOf(of(COMP_ADMIN, PROJECT_FINANCE));

    private CompetitionLookupStrategy competitionLookupStrategy;

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionTypeService> getClassUnderTest() {
        return CompetitionTypeService.class;
    }

    @Test
    public void testCompAdminAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build());

        classUnderTest.findAllTypes();
    }

    @Test
    public void testProjectFinanceAllAccessAllowed() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());

        classUnderTest.findAllTypes();
    }

    @Test
    public void testAllAccessDenied() {
        NON_COMP_ADMIN_ROLES.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(role)).build());

            assertAccessDenied(() -> classUnderTest.findAllTypes(), () -> verifyNoMoreInteractions(rules));
        });
    }
}
