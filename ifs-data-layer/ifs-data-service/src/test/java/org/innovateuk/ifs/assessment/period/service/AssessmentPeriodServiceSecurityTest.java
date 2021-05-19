package org.innovateuk.ifs.assessment.period.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.period.security.AssessmentPeriodPermissionRule;
import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodService;
import org.innovateuk.ifs.assessment.period.transactional.AssessmentPeriodServiceImpl;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentPeriodServiceSecurityTest extends BaseServiceSecurityTest<AssessmentPeriodService> {

    private static Long COMPETITION_ID = 1L;
    private AssessmentPeriodPermissionRule assessmentPeriodPermissionRule;
    private CompetitionLookupStrategy competitionLookupStrategy;

    @Override
    protected Class<? extends AssessmentPeriodService> getClassUnderTest() {
        return AssessmentPeriodServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentPeriodPermissionRule = getMockPermissionRulesBean(AssessmentPeriodPermissionRule.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);
    }

    @Test
    public void compAdminCanReadAssessmentPeriod() {
        UserResource compAdminUser = newUserResource()
                .withRoleGlobal(Role.COMP_ADMIN)
                .build();

        setLoggedInUser(compAdminUser);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionLookupStrategy.getCompetitionResource(COMPETITION_ID)).thenReturn(competitionResource);

        assertAccessDenied(
                () -> classUnderTest.getAssessmentPeriodByCompetitionId(COMPETITION_ID),
                () -> {
                    verify(assessmentPeriodPermissionRule).compAdminCanReadAssessmentPeriod(eq(competitionResource), isA(UserResource.class));
                }
        );
    }

    @Test
    public void userCanReadAssessmentPeriod() {
        UserResource compAdminUser = newUserResource()
                .withRoleGlobal(Role.ASSESSOR)
                .build();

        setLoggedInUser(compAdminUser);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionLookupStrategy.getCompetitionResource(COMPETITION_ID)).thenReturn(competitionResource);

        assertAccessDenied(
                () -> classUnderTest.getAssessmentPeriodByCompetitionId(COMPETITION_ID),
                () -> {
                    verify(assessmentPeriodPermissionRule).userCanReadAssessmentPeriod(eq(competitionResource), isA(UserResource.class));
                }
        );
    }
}
