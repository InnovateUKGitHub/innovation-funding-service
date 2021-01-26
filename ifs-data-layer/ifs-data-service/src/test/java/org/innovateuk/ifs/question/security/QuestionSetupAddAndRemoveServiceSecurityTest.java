package org.innovateuk.ifs.question.security;


import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.security.CompetitionPermissionRules;
import org.innovateuk.ifs.question.transactional.template.QuestionSetupAddAndRemoveService;
import org.innovateuk.ifs.question.transactional.template.QuestionSetupAddAndRemoveServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.mockito.MockitoAnnotations.initMocks;

public class QuestionSetupAddAndRemoveServiceSecurityTest extends BaseServiceSecurityTest<QuestionSetupAddAndRemoveService> {

    private CompetitionPermissionRules rules;

    @Override
    protected Class<? extends QuestionSetupAddAndRemoveService> getClassUnderTest() {
        return QuestionSetupAddAndRemoveServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        initMocks(this);
    }

    @Test
    public void allServiceFunctionsShouldBeAuthorizedForCompAdmin() {
        setLoggedInUser(newUserResource().withRoleGlobal(COMP_ADMIN).build());
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetition(1L);
    }

    @Test
    public void allServiceFunctionsShouldBeAuthorizedForProjectFinance() {
        setLoggedInUser(newUserResource().withRoleGlobal(PROJECT_FINANCE).build());
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
        classUnderTest.deleteQuestionInCompetition(1L);
    }

    @Test(expected = AccessDeniedException.class)
    public void addDefaultAssessedQuestionToCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.addDefaultAssessedQuestionToCompetition(null);
    }

    @Test(expected = AccessDeniedException.class)
    public void deleteAssessedQuestionInCompetitionShouldFailForAnonymousUser() {
        setLoggedInUser(null);
        classUnderTest.deleteQuestionInCompetition(1L);
    }
}
