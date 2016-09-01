package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.security.access.method.P;

import java.util.Optional;

@Ignore("TODO")
public class CompetitionInviteServiceSecurityTest extends BaseServiceSecurityTest<CompetitionInviteService> {

    private CompetitionInvitePermissionRules competitionInvitePermissionRules;
    private CompetitionInviteLookupStrategy competitionInviteLookupStrategy;

    @Override
    protected Class<? extends CompetitionInviteService> getServiceClass() {
        return TestCompetitionInviteService.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionInvitePermissionRules = getMockPermissionRulesBean(CompetitionInvitePermissionRules.class);
        competitionInviteLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionInviteLookupStrategy.class);
    }

    public static class TestCompetitionInviteService implements CompetitionInviteService {

        @Override
        public ServiceResult<CompetitionInviteResource> getInvite(@P("inviteHash") String inviteHash) { return null; }

        @Override
        public ServiceResult<CompetitionInviteResource> openInvite(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectInvite(@P("inviteHash") String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment) { return null; }

        @Override
        public ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash) {
            return null;
        }
    }
}
