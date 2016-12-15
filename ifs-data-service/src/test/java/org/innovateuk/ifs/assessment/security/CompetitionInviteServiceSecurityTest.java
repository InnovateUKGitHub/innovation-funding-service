package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.StreamFunctions.toStream;
import static org.mockito.Mockito.*;

public class CompetitionInviteServiceSecurityTest extends BaseServiceSecurityTest<CompetitionInviteService> {

    private static final List<UserRoleType> ASSESSOR_MANAGEMENT_ROLES = asList(COMP_ADMIN, COMP_EXEC);

    private static final List<UserRoleType> NON_ASSESSOR_MANAGEMENT_ROLES = toStream(UserRoleType.values())
            .filter(userRoleType -> !ASSESSOR_MANAGEMENT_ROLES.contains(userRoleType) )
            .collect(toList());

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
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.getCreatedInvites(1L);
        });

    }

    @Test
    public void getCreatedInvites_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.getCreatedInvites(1L),
                    () -> {}
            );
        });

    }

    @Test
    public void getInvitationOverview() {
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.getInvitationOverview(1L);
        });

    }

    @Test
    public void getInvitationOverview_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.getInvitationOverview(1L),
                    () -> {}
            );
        });
    }

    @Test
    public void getAvailableAssessors() {
        setLoggedInUser(null);

        assertAccessDenied(() -> classUnderTest.getAvailableAssessors(1L), () -> {
            verifyNoMoreInteractions(userPermissionRules);
        });
    }

    @Test
    public void inviteUser_existing() {
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.inviteUser(newExistingUserStagedInviteResource().build());
        });
    }

    @Test
    public void inviteUser_existing_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.inviteUser(newExistingUserStagedInviteResource().build()),
                    () -> {}
            );
        });
    }

    @Test
    public void inviteUser_new() {
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.inviteUser(newNewUserStagedInviteResource().build());
        });
    }

    @Test
    public void inviteUser_new_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.inviteUser(newNewUserStagedInviteResource().build()),
                    () -> {}
            );
        });
    }

    @Test
    public void sendInvite() {
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.sendInvite(1L);
        });
    }

    @Test
    public void sendInvite_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.sendInvite(1L),
                    () -> {}
            );
        });
    }

    @Test
    public void deleteInvite() {
        ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            classUnderTest.deleteInvite("email", 1L);
        });
    }

    @Test
    public void deleteInvite_nonAssessorManagement() {
        NON_ASSESSOR_MANAGEMENT_ROLES.forEach(roleType -> {
            setLoggedInUser(
                    newUserResource()
                            .withRolesGlobal(singletonList(newRoleResource()
                                    .withType(roleType)
                                    .build())
                            )
                            .build());
            assertAccessDenied(
                    () -> classUnderTest.deleteInvite("email", 1L),
                    () -> {}
            );
        });
    }

    public static class TestCompetitionInviteService implements CompetitionInviteService {

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
        public ServiceResult<CompetitionInviteResource> inviteUser(NewUserStagedInviteResource stagedInvite) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInviteResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> sendInvite(long inviteId) {
            return null;
        }

        @Override
        public ServiceResult<Void> deleteInvite(String email, long competitionId) {
            return null;
        }
    }
}
