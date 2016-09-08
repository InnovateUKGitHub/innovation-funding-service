package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.transactional.CompetitionParticipantService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompetitionParticipantServiceSecurityTest extends BaseServiceSecurityTest<CompetitionParticipantService> {

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    @Override
    protected Class<? extends CompetitionParticipantService> getServiceClass() {
        return CompetitionParticipantServiceSecurityTest.TestCompetitionParticipantService.class;
    }


    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
    }

    @Test
    public void getCompetitionParticipants() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        newRoleResource()
                                .withType(UserRoleType.ASSESSOR)
                                .build()
                        )
                ).build();

        setLoggedInUser(assessorUserResource);

        assertTrue(service.getCompetitionParticipants(7L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED).getSuccessObject().isEmpty());

        verify(competitionParticipantPermissionRules, times(3)).userCanViewTheirOwnCompetitionParticipation(any(CompetitionParticipantResource.class), eq(assessorUserResource));
    }

    public static class TestCompetitionParticipantService implements CompetitionParticipantService {

        @Override
        public ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(@P("user") Long userId, @P("role") CompetitionParticipantRoleResource role, @P("status") ParticipantStatusResource status) {
            return serviceSuccess( newCompetitionParticipantResource().build(3) );
        }
    }
}
