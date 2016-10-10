package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.Optional;

import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.ASSESSOR;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceSecurityTest extends BaseServiceSecurityTest<CompetitionInviteService> {

    private CompetitionInvitePermissionRules competitionInvitePermissionRules;
    private CompetitionInviteLookupStrategy competitionInviteLookupStrategy;

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    @Override
    protected Class<? extends CompetitionInviteService> getClassUnderTest() {
        return TestCompetitionInviteService.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionInvitePermissionRules = getMockPermissionRulesBean(CompetitionInvitePermissionRules.class);
        competitionInviteLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionInviteLookupStrategy.class);
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
    }

    @Test
    public void acceptInvite() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        newRoleResource()
                                .withType(ASSESSOR)
                                .build()
                        )
                ).build();
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource().build();

        when(competitionParticipantLookupStrategy.getCompetitionParticipantResource("hash"))
                .thenReturn(competitionParticipantResource);
        when(competitionParticipantPermissionRules.userCanAcceptCompetitionInvite(competitionParticipantResource, assessorUserResource))
                .thenReturn(true);

        setLoggedInUser(assessorUserResource);

        classUnderTest.acceptInvite("hash", getLoggedInUser());

        verify(competitionParticipantLookupStrategy, only()).getCompetitionParticipantResource("hash");
        verify(competitionParticipantPermissionRules, only()).userCanAcceptCompetitionInvite(competitionParticipantResource, assessorUserResource);
    }

    @Test
    public void acceptInvite_notLoggedIn() {
        setLoggedInUser(null);
        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash", getLoggedInUser()),
                () -> {
                    verify(competitionParticipantLookupStrategy, only()).getCompetitionParticipantResource("hash");
                    verifyZeroInteractions(competitionParticipantPermissionRules);
                }
        );
    }

    @Test
    public void acceptInvite_notSameUser() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        newRoleResource()
                                .withType(ASSESSOR)
                                .build()
                        )
                ).build();
        CompetitionParticipantResource competitionParticipantResource = newCompetitionParticipantResource().build();
        when(competitionParticipantLookupStrategy.getCompetitionParticipantResource("hash"))
                .thenReturn(competitionParticipantResource);
        when(competitionParticipantPermissionRules.userCanAcceptCompetitionInvite(competitionParticipantResource, assessorUserResource))
                .thenReturn(false);

        setLoggedInUser(assessorUserResource);

        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash", getLoggedInUser()),
                () -> {
                    verify(competitionParticipantLookupStrategy, only()).getCompetitionParticipantResource("hash");
                    verify(competitionParticipantPermissionRules, only()).userCanAcceptCompetitionInvite(competitionParticipantResource, assessorUserResource);
                }
        );
    }

    @Test
    public void acceptInvite_hashNotExists() {
        UserResource assessorUserResource = newUserResource()
                .withRolesGlobal(singletonList(
                        newRoleResource()
                                .withType(ASSESSOR)
                                .build()
                        )
                ).build();

        when(competitionParticipantLookupStrategy.getCompetitionParticipantResource("hash not exists")).thenReturn(null);

        setLoggedInUser(assessorUserResource);

        assertAccessDenied(
                () -> classUnderTest.acceptInvite("hash not exists", getLoggedInUser()),
                () -> {
                    verify(competitionParticipantLookupStrategy, only()).getCompetitionParticipantResource("hash not exists");
                    verifyZeroInteractions(competitionParticipantPermissionRules);
                }
        );
    }

    public static class TestCompetitionInviteService implements CompetitionInviteService {

        @Override
        public ServiceResult<CompetitionInviteResource> getInvite(@P("inviteHash") String inviteHash) { return null; }

        @Override
        public ServiceResult<CompetitionInviteResource> openInvite(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash, UserResource userResource) {
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
