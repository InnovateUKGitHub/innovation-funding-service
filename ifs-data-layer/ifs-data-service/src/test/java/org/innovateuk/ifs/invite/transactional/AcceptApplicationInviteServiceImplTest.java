package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AcceptApplicationInviteServiceImplTest {

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;
    @Mock
    private ApplicationInviteRepository applicationInviteRepositoryMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;
    @Mock
    private ApplicationProgressService applicationProgressService;
    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @InjectMocks
    private AcceptApplicationInviteServiceImpl service = new AcceptApplicationInviteServiceImpl();

    private final String testInviteHash = "abcdef";

    @Test
    public void acceptInvite_failsOnEmailAddressMismatch() {
        ApplicationInvite invite = newApplicationInvite()
                .withEmail("james@test.com")
                .build();
        User user = newUser()
                .withEmailAddress("bob@test.com")
                .build();

        when(applicationInviteRepositoryMock.getByHash(testInviteHash)).thenReturn(invite);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        ServiceResult<Void> result = service.acceptInvite(testInviteHash, user.getId());

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void acceptInvite_replaceWithExistingCollaboratorInviteOrganisation() {
        InviteOrganisation inviteOrganisationToBeReplaced = newInviteOrganisation().build();

        ApplicationInvite invite = createAndExpectInvite(inviteOrganisationToBeReplaced);
        User user = createAndExpectInviteUser();
        Organisation usersCurrentOrganisation = createAndExpectUsersCurrentOrganisation(user);

        InviteOrganisation collaboratorInviteOrganisation = newInviteOrganisation()
                .withOrganisation(usersCurrentOrganisation)
                .build();

        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                usersCurrentOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.of(collaboratorInviteOrganisation));

        ServiceResult<Void> result = service.acceptInvite(testInviteHash, user.getId());

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, applicationInviteRepositoryMock);
        inOrder.verify(inviteOrganisationRepositoryMock).saveAndFlush(inviteOrganisationToBeReplaced);
        inOrder.verify(inviteOrganisationRepositoryMock).delete(inviteOrganisationToBeReplaced);

        assertThat(result.isSuccess()).isTrue();
        assertThat(invite.getInviteOrganisation())
                .isEqualToComparingFieldByField(collaboratorInviteOrganisation);
    }

    @Test
    public void acceptInvite_previousInviteOrganisationIsNotDeletedIfThereAreOtherInvitesAttached() {
        InviteOrganisation inviteOrganisationToBeReplaced = newInviteOrganisation()
                .withInvites(newApplicationInvite().build(2))
                .build();

        ApplicationInvite invite = createAndExpectInvite(inviteOrganisationToBeReplaced);
        User user = createAndExpectInviteUser();
        Organisation usersCurrentOrganisation = createAndExpectUsersCurrentOrganisation(user);

        InviteOrganisation collaboratorInviteOrganisation = newInviteOrganisation()
                .withOrganisation(usersCurrentOrganisation)
                .build();

        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                usersCurrentOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.of(collaboratorInviteOrganisation));

        ServiceResult<Void> result = service.acceptInvite(testInviteHash, user.getId());

        verify(inviteOrganisationRepositoryMock, never()).delete(inviteOrganisationToBeReplaced);

        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void acceptInvite_assignsUsersCurrentOrganisationIfNoCollaboratorOrganisationExists() {
        ApplicationInvite invite = createAndExpectInvite(newInviteOrganisation().build());
        User user = createAndExpectInviteUser();
        Organisation usersCurrentOrganisation = createAndExpectUsersCurrentOrganisation(user);

        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                usersCurrentOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.empty());

        ServiceResult<Void> result = service.acceptInvite(testInviteHash, user.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(invite.getInviteOrganisation().getOrganisation())
                .isEqualToComparingFieldByField(usersCurrentOrganisation);
    }

    @Test
    public void acceptInvite_processRoleIsCreatedAndApplicationProgressIsUpdated() {
        ApplicationInvite invite = createAndExpectInvite(newInviteOrganisation().build());
        User user = createAndExpectInviteUser();
        Organisation usersCurrentOrganisation = createAndExpectUsersCurrentOrganisation(user);

        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                usersCurrentOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.empty());

        ProcessRole expectedProcessRole = new ProcessRole(
                user,
                invite.getTarget().getId(),
                Role.COLLABORATOR,
                usersCurrentOrganisation.getId()
        );

        when(processRoleRepositoryMock.save(expectedProcessRole)).thenReturn(expectedProcessRole);
        when(applicationProgressService.updateApplicationProgress(invite.getTarget().getId()))
                .thenReturn(serviceSuccess(BigDecimal.ONE));

        ServiceResult<Void> result = service.acceptInvite(testInviteHash, user.getId());

        verify(processRoleRepositoryMock).save(expectedProcessRole);
        verify(applicationProgressService).updateApplicationProgress(invite.getTarget().getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(invite.getTarget().getProcessRoles())
                .contains(expectedProcessRole);
    }

    private User createAndExpectInviteUser() {
        User user = newUser()
                .withEmailAddress("james@test.com")
                .build();

        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

        return user;
    }

    private ApplicationInvite createAndExpectInvite(InviteOrganisation inviteOrganisation) {
        ApplicationInvite invite = newApplicationInvite()
                .withEmail("james@test.com")
                .withApplication(newApplication().build())
                .withInviteOrganisation(inviteOrganisation)
                .build();

        when(applicationInviteRepositoryMock.getByHash(testInviteHash)).thenReturn(invite);

        return invite;
    }

    private Organisation createAndExpectUsersCurrentOrganisation(User user) {
        Organisation usersOrganisation = newOrganisation()
                .withUser(singletonList(user))
                .build();

        when(organisationRepositoryMock.findFirstByUsers(user)).thenReturn(Optional.of(usersOrganisation));

        return usersOrganisation;
    }
}
