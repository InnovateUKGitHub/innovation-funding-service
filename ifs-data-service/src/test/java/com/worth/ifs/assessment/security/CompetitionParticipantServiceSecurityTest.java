package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.transactional.CompetitionParticipantService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.security.access.method.P;

import java.util.List;

@Ignore("TODO")
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

    public static class TestCompetitionParticipantService implements CompetitionParticipantService {

        @Override
        public ServiceResult<List<CompetitionParticipantResource>> getCompetitionParticipants(@P("user") Long userId, @P("role") CompetitionParticipantRoleResource role, @P("status") ParticipantStatusResource status) {
            return null;
        }
    }
}
