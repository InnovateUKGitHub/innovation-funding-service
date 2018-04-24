package org.innovateuk.ifs.question.security;


import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.security.CompetitionPermissionRules;
import org.innovateuk.ifs.question.transactional.QuestionSetupTemplateService;
import org.innovateuk.ifs.question.transactional.QuestionSetupTemplateServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.MockitoAnnotations.initMocks;

public class QuestionSetupTemplateServiceSecurityTest extends BaseServiceSecurityTest<QuestionSetupTemplateService> {

    private CompetitionPermissionRules rules;

    @Override
    protected Class<? extends QuestionSetupTemplateService> getClassUnderTest() {
        return QuestionSetupTemplateServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        initMocks(this);
    }

    @Test
    public void testAllServiceFunctionsShouldBeAuthorizedForCompAdmin() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetition(null);
    }

    @Test
    public void testAllServiceFunctionsShouldBeAuthorizedForProjectFinance() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetition(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAddDefaultAssessedQuestionToCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDeleteAssessedQuestionInCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.deleteQuestionInCompetition(null);
    }
}
