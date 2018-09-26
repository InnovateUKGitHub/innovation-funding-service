package org.innovateuk.ifs.question.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.question.transactional.QuestionSetupCompetitionService;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder
        .newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

/**
 * Testing the permission rules applied to the secured methods in OrganisationService.  This set of tests tests for the
 * individual rules that are called whenever an OrganisationService method is called.  They do not however test the logic
 * within those rules
 */
public class QuestionSetupCompetitionServiceSecurityTest extends
        BaseServiceSecurityTest<QuestionSetupCompetitionService> {

    @Override
    protected Class<? extends QuestionSetupCompetitionService> getClassUnderTest() {
        return QuestionSetupCompetitionService.class;
    }

    @Test
    public void getByQuestionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getByQuestionId(1L), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void update() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.update(newCompetitionSetupQuestionResource()
                .build()), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void createByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.createByCompetitionId(1L), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void addResearchCategoryQuestionToCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                        classUnderTest.addResearchCategoryQuestionToCompetition(1L), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Test
    public void delete() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.delete(1L), COMP_ADMIN, PROJECT_FINANCE);
    }
}
