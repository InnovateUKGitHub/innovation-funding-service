package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceSecurityTest extends BaseServiceSecurityTest<CompetitionInviteService> {

    private static final EnumSet<UserRoleType> ASSESSOR_MANAGEMENT_ROLES = EnumSet.of(COMP_ADMIN, COMP_EXEC);

    private CompetitionInvitePermissionRules competitionInvitePermissionRules;
    private CompetitionInviteLookupStrategy competitionInviteLookupStrategy;

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    private UserPermissionRules userPermissionRules;

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
        userPermissionRules = getMockPermissionRulesBean(UserPermissionRules.class);
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

    @Test
    public void getCreatedInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getCreatedInvites(1L), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void getInvitationOverview() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInvitationOverview(1L), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void getAvailableAssessors() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableAssessors(1L), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void getInviteStatistics() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInviteStatistics(1L), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void inviteUser_existing() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.inviteUser(newExistingUserStagedInviteResource().build()), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void inviteUser_new() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.inviteUser(newNewUserStagedInviteResource().build()), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void inviteNewUsers() throws Exception {

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.inviteNewUsers(
                newNewUserStagedInviteResource().build(2), 1L)
                , COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void sendInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.sendInvite(1L, newEmailContentResource().build()), COMP_ADMIN, COMP_EXEC);
    }

    @Test
    public void deleteInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.deleteInvite("email", 1L), COMP_ADMIN, COMP_EXEC);
    }

    private void runAsRole(UserRoleType roleType, Runnable serviceCall) {
        setLoggedInUser(
                newUserResource()
                        .withRolesGlobal(singletonList(
                                newRoleResource()
                                        .withType(roleType)
                                        .build()
                                )
                        )
                        .build());
        serviceCall.run();
    }

    private void assertAccessDeniedAsRole(UserRoleType roleType, Runnable serviceCall, Runnable verifications) {
        runAsRole(roleType, () -> assertAccessDenied(serviceCall, verifications));
    }

    public static class TestCompetitionInviteService implements CompetitionInviteService {

        @Override
        public ServiceResult<AssessorInviteToSendResource> getCreatedInvite(long inviteId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteResource> getInvite(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteResource> openInvite(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvite(@P("inviteHash") String inviteHash, UserResource userResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectInvite(@P("inviteHash") String inviteHash, RejectionReasonResource rejectionReason, Optional<String> rejectionComment) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> checkExistingUser(@P("inviteHash") String inviteHash) {
            return null;
        }

        @Override
        public ServiceResult<List<AvailableAssessorResource>> getAvailableAssessors(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<AssessorCreatedInviteResource>> getCreatedInvites(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<AssessorInviteOverviewResource>> getInvitationOverview(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInviteResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteNewUsers(List<NewUserStagedInviteResource> newUserStagedInvites, long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<AssessorInviteToSendResource> sendInvite(long inviteId, EmailContent content) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteInvite(String email, long competitionId) {
            return null;
        }
    }
}
