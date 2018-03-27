package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupQuestionService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.fail;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class CompetitionSetupQuestionServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupQuestionService> {

    private static final long QUESTION_ID = 1L;

    @Override
    protected Class<? extends CompetitionSetupQuestionService> getClassUnderTest() {
        return TestCompetitionService.class;
    }

    @Test
    public void testGetByQuestionIdDeniedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.getByQuestionId(QUESTION_ID);
    }

    @Test
    public void testGetByQuestionIdDeniedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.getByQuestionId(QUESTION_ID);
            fail("Should not have been able to get question from id without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testGetQuestionIdDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.getByQuestionId(QUESTION_ID);
                fail("Should not have been able to get question from id without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testSaveAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.update(newCompetitionSetupQuestionResource().build());
    }

    @Test
    public void testSaveAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.update(newCompetitionSetupQuestionResource().build());
            fail("Should not have been able to update question without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testSaveDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.update(newCompetitionSetupQuestionResource().build());
                fail("Should not have been able to update question without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testDeleteAllowedIfGlobalCompAdminRole() {
        final Long questionId = 1L;
        final String sectionName = "Application questions";

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.delete(questionId, sectionName);
    }

    @Test
    public void testDeleteAllowedIfNoGlobalRolesAtAll() {
        final Long questionId = 1L;
        final String sectionName = "Application questions";

        try {
            classUnderTest.delete(questionId, sectionName);
            fail("Should not have been able to update question without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testDeleteDeniedIfNotCorrectGlobalRoles() {
        final Long questionId = 1L;
        final String sectionName = "Application questions";

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.delete(questionId, sectionName);
                fail("Should not have been able to update question without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testCreateByCompetitionAllowedIfGlobalCompAdminRole() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.createByCompetitionId(2L);
    }

    @Test
    public void testCreateByCompetitionAllowedIfNoGlobalRolesAtAll() {
        try {
            classUnderTest.createByCompetitionId(2L);
            fail("Should not have been able to update question without the global Comp Admin role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
    }

    @Test
    public void testCreateByCompetitionDeniedIfNotCorrectGlobalRoles() {

        List<UserRoleType> nonCompAdminRoles = asList(UserRoleType.values()).stream().filter(type -> type != COMP_ADMIN && type != PROJECT_FINANCE)
                .collect(toList());

        nonCompAdminRoles.forEach(role -> {

            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.createByCompetitionId(3L);
                fail("Should not have been able to update question without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestCompetitionService implements CompetitionSetupQuestionService {

        @Override
        public ServiceResult<CompetitionSetupQuestionResource> createByCompetitionId(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> delete(Long questionId, String sectionName) {
            return null;
        }

        public ServiceResult<CompetitionSetupQuestionResource> getByQuestionId(Long questionId) {
            return null;
        }

        public ServiceResult<CompetitionSetupQuestionResource> update(CompetitionSetupQuestionResource competitionSetupQuestionResource) {
            return null;
        }
    }
}
