package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupTemplateService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.MockitoAnnotations.initMocks;

public class CompetitionSetupTemplateServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupTemplateService> {
    private CompetitionPermissionRules rules;

    private static String ASSESSED_QUESTIONS_SECTION_NAME = "Application questions";
    private static String PROJECT_DETAILS_SECTION_NAME = "Project details";

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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetitionBySection(null, ASSESSED_QUESTIONS_SECTION_NAME);
        classUnderTest.deleteQuestionInCompetitionBySection(null, PROJECT_DETAILS_SECTION_NAME);
    }

    @Test
    public void testAllServiceFunctionsShouldBeAuthorizedForProjectFinance() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());
        classUnderTest.initializeCompetitionByCompetitionTemplate(null, null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetitionBySection(null, ASSESSED_QUESTIONS_SECTION_NAME);
        classUnderTest.deleteQuestionInCompetitionBySection(null, PROJECT_DETAILS_SECTION_NAME);
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
        classUnderTest.deleteQuestionInCompetitionBySection(null, ASSESSED_QUESTIONS_SECTION_NAME);
        classUnderTest.deleteQuestionInCompetitionBySection(null, PROJECT_DETAILS_SECTION_NAME);
    }

    public static class TestCompetitionSetupTemplateService implements CompetitionSetupTemplateService {
        public ServiceResult<Competition> initializeCompetitionByCompetitionTemplate(Long competitionId, Long competitionTypeId) {
            return null;
        }

        public ServiceResult<Question> addDefaultAssessedQuestionToCompetition(Competition competition) {
            return null;
        }

        public ServiceResult<Void> deleteQuestionInCompetitionBySection(Long questionId, String sectionName) {
            return null;
        }
    }
}
