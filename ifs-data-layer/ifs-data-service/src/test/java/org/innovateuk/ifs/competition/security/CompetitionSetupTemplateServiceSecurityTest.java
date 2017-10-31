package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupTemplateService;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
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
        return CompetitionSetupTemplateServiceSecurityTest.TestCompetitionSetupTemplateService.class;
    }

    @Test
    public void testAllServiceFunctionsShouldBeAuthorizedForCompAdmin() {

        RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteAssessedQuestionInCompetition(null);
    }

    @Test
    public void testAllServiceFunctionsShouldBeAuthorizedForProjectFinance() {
        RoleResource compAdminRole = newRoleResource().withType(PROJECT_FINANCE).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteAssessedQuestionInCompetition(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testInitializeCompetitionByCompetitionTemplateShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddDefaultAssessedQuestionToCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteAssessedQuestionInCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.deleteAssessedQuestionInCompetition(null);
    }

    public static class TestCompetitionSetupTemplateService implements CompetitionSetupTemplateService {
        public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
            return null;
        }

        public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
            return null;
        }

        public ServiceResult<Void> deleteAssessedQuestionInCompetition(Long questionId) {
            return null;
        }
    }
}
