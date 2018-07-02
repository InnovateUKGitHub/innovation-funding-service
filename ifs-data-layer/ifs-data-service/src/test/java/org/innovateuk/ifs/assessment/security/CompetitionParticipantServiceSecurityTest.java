package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantServiceImpl;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceSecurityTest extends BaseServiceSecurityTest<CompetitionParticipantService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 3;

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    @Override
    protected Class<? extends CompetitionParticipantService> getClassUnderTest() {
        return CompetitionParticipantServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
    }

    @Test
    public void getCompetitionParticipants() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(Role.ASSESSOR)
                ).build();

        setLoggedInUser(assessorUserResource);

        when(classUnderTestMock.getCompetitionParticipants(7L, CompetitionParticipantRoleResource.ASSESSOR))
                .thenReturn(serviceSuccess(newCompetitionParticipantResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        assertTrue(classUnderTest.getCompetitionParticipants(7L, CompetitionParticipantRoleResource.ASSESSOR).getSuccess().isEmpty());

        verify(competitionParticipantPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .userCanViewTheirOwnCompetitionParticipation(any(CompetitionParticipantResource.class), eq(assessorUserResource));
    }
}
