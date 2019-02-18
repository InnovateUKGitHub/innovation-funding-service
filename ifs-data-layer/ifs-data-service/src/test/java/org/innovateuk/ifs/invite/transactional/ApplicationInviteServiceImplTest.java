package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressServiceImpl;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationInviteServiceImplTest {

    @Mock
    private ApplicationInviteMapper applicationInviteMapper;

    @Mock
    private InviteOrganisationMapper inviteOrganisationMapper;

    @Mock
    private QuestionReassignmentService questionReassignmentServiceMock;

    @Mock
    private ApplicationProgressServiceImpl applicationProgressServiceMock;

    @Mock
    private ApplicationInviteRepository applicationInviteRepositoryMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private ApplicationInviteNotificationService applicationInviteNotificationServiceMock;

    @InjectMocks
    private ApplicationInviteServiceImpl inviteService = new ApplicationInviteServiceImpl();

    @Before
    public void setup() {
        when(applicationInviteRepositoryMock.save(any(ApplicationInvite.class))).thenReturn(new ApplicationInvite());
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
    }

    @Test
    public void findOneByHash() {
        String hash = "123abc";
        ApplicationInvite applicationInvite = newApplicationInvite().build();

        when(applicationInviteRepositoryMock.getByHash(hash)).thenReturn(applicationInvite);

        ServiceResult<ApplicationInvite> result = inviteService.findOneByHash(hash);

        assertThat(result.getSuccess()).isEqualTo(applicationInvite);
    }

    @Test
    public void createApplicationInvites() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail", "testemail1", "testemail2", "testemail3", "testemail4")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        InviteOrganisation saveInviteOrganisationExpectations = argThat(lambdaMatches(inviteOrganisation -> {
            assertThat(inviteOrganisation.getOrganisationName()).isEqualTo("new organisation");
            return true;
        }));

        when(inviteOrganisationRepositoryMock.save(saveInviteOrganisationExpectations))
                .thenReturn(saveInviteOrganisationExpectations);
        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(emptyList());

        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class)))
                .thenReturn(newInviteOrganisation().build(inviteResources.size()));

        List<ApplicationInvite> savedInvites = ApplicationInviteBuilder.newApplicationInvite().build(5);

        List<ApplicationInvite> saveInvitesExpectations = argThat(lambdaMatches(invites -> {
            assertThat(invites).hasSize(5);
            assertThat(invites.get(0).getName()).isEqualTo("testname");
            return true;
        }));


        when(applicationInviteRepositoryMock.saveAll(saveInvitesExpectations)).thenReturn(savedInvites);
        when(applicationRepositoryMock.findById(isA(Long.class))).thenReturn(Optional.of(newApplication().withId(1L).build()));
        when(applicationInviteNotificationServiceMock.inviteCollaborators(isA(List.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result =
                inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertThat(result.isSuccess()).isTrue();

        verify(inviteOrganisationRepositoryMock).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, times(5)).save(isA(ApplicationInvite.class));
        verify(applicationInviteNotificationServiceMock, times(1)).inviteCollaborators(isA(List.class));
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoApplicationId() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoEmailAddress() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withId(1L)
                .withName("testname")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidInvitesNoName() {

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withId(1L)
                .withEmail("testemail")
                .build(5);

        assertInvalidInvites(inviteResources);
    }

    @Test
    public void createApplicationInvitesWithInvalidOrganisationInviteNoOrganisationName() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(5);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class)))
                .thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(applicationRepositoryMock.findById(isA(Long.class)))
                .thenReturn(Optional.of(newApplication().withId(1L).build()));

        ServiceResult<Void> result =
                inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertThat(result.isFailure()).isTrue();

        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).saveAll(isA(List.class));
    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasNoOrganisationResultsInNewInviteOrganisationWithoutOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withInviteResources(inviteResources)
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class)))
                .thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(applicationRepositoryMock.findById(isA(Long.class))).thenReturn(Optional.of(newApplication().withId(1L).build()));
        when(applicationInviteNotificationServiceMock.inviteCollaborators(isA(List.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result =
                inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());

        assertThat(inviteOrganisationResource.getOrganisationName()).isEqualTo(argument.getValue().getOrganisationName());
        assertThat(argument.getValue().getOrganisation()).isNull();

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));
        verify(applicationInviteNotificationServiceMock, times(1)).inviteCollaborators(isA(List.class));
    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasOrganisationButNoInviteOrganisationExistsResultsInNewInviteOrganisationWithOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withOrganisation(2L)
                .withInviteResources(inviteResources)
                .build();

        Organisation organisation = newOrganisation().withId(3L).build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class))).thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(organisationRepositoryMock.findById(inviteOrganisationResource.getOrganisation())).thenReturn(Optional.of(organisation));
        when(applicationRepositoryMock.findById(isA(Long.class))).thenReturn(Optional.of(newApplication().withId(1L).build()));
        when(applicationInviteNotificationServiceMock.inviteCollaborators(isA(List.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result =
                inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());

        assertThat(organisation.getId())
                .isEqualTo(argument.getValue().getOrganisation().getId());
        assertThat(inviteOrganisationResource.getOrganisationName())
                .isEqualTo(argument.getValue().getOrganisationName());

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));
        verify(applicationInviteNotificationServiceMock, times(1)).inviteCollaborators(isA(List.class));
    }

    @Test
    public void createApplicationInvites_inviteOrganisationResourceHasOrganisationAndInviteOrganisationExistsResultsInExistingInviteOrganisationWithOrganisationId() {
        Long applicationId = 1L;

        List<ApplicationInviteResource> inviteResources = newApplicationInviteResource()
                .withApplication(1L)
                .withName("testname")
                .withEmail("testemail")
                .build(1);

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withOrganisationName("An invite organisation with an organisation")
                .withOrganisation(3L)
                .withInviteResources(inviteResources)
                .build();

        Organisation organisation = newOrganisation().withName("Already existing organisation name").withId(3L).build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().withOrganisationName("Already existing invite organisation name").withOrganisation(organisation).build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(asList());
        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class)))
                .thenReturn(newInviteOrganisation().build(inviteResources.size()));
        when(organisationRepositoryMock.findById(inviteOrganisationResource.getOrganisation())).thenReturn(Optional.of(organisation));
        when(inviteOrganisationRepositoryMock.findOneByOrganisationIdAndInvitesApplicationId(
                inviteOrganisation.getOrganisation().getId(),
                applicationId
        ))
                .thenReturn(inviteOrganisation);

        when(applicationRepositoryMock.findById(isA(Long.class))).thenReturn(Optional.of(newApplication().withId(1L).build()));
        when(applicationInviteNotificationServiceMock.inviteCollaborators(isA(List.class))).thenReturn(serviceSuccess());

        ServiceResult<Void> result =
                inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));

        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<InviteOrganisation> argument = ArgumentCaptor.forClass(InviteOrganisation.class);
        verify(inviteOrganisationRepositoryMock, times(1)).save(argument.capture());

        assertThat(inviteOrganisation.getOrganisation().getName())
                .isEqualTo(argument.getValue().getOrganisation().getName());
        assertThat(inviteOrganisation.getOrganisationName())
                .isEqualTo(argument.getValue().getOrganisationName());

        verify(applicationInviteRepositoryMock, times(1)).save(isA(ApplicationInvite.class));
        verify(applicationInviteNotificationServiceMock, times(1)).inviteCollaborators(isA(List.class));
    }

    @Test
    public void getInviteOrganisationByHash() {
        Competition competition = newCompetition().build();
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        ProcessRole leadApplicantProcessRole = newProcessRole()
                .withUser(user)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisationId(organisation.getId())
                .build();
        Application application = newApplication()
                .withCompetition(competition)
                .withProcessRoles(leadApplicantProcessRole)
                .build();
        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        ApplicationInvite invite = ApplicationInviteBuilder.newApplicationInvite()
                .withInviteOrganisation(inviteOrganisation)
                .withApplication(application)
                .build();
        ApplicationInviteResource inviteResource = newApplicationInviteResource()
                .withApplication(application.getId())
                .build();

        InviteOrganisationResource expectedInviteOrganisationResource = newInviteOrganisationResource().
                withId(inviteOrganisation.getId()).
                withInviteResources(singletonList(inviteResource)).
                build();

        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(invite);
        when(inviteOrganisationRepositoryMock.findById(inviteOrganisation.getId())).thenReturn(Optional.of(inviteOrganisation));
        when(inviteOrganisationMapper.mapToResource(inviteOrganisation)).thenReturn(expectedInviteOrganisationResource);

        InviteOrganisationResource inviteOrganisationResource =
                inviteService.getInviteOrganisationByHash("an organisation hash").getSuccess();

        assertThat(expectedInviteOrganisationResource).isEqualTo(inviteOrganisationResource);
    }

    @Test
    public void getInviteOrganisationByHashButInviteOrganisationNotFound() {

        when(applicationInviteRepositoryMock.getByHash("an organisation hash")).thenReturn(null);

        ServiceResult<InviteOrganisationResource> organisationInvite =
                inviteService.getInviteOrganisationByHash("an organisation hash");
        assertThat(organisationInvite.isFailure()).isTrue();
        assertThat(
                organisationInvite
                        .getFailure()
                        .is(notFoundError(ApplicationInvite.class, "an organisation hash"))
        )
                .isTrue();
    }

    @Test
    public void removeApplicationInvite_deletesInviteOrganisationAndOrganisationOnLastInvite() throws Exception {
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        List<ProcessRole> inviteProcessRoles = newProcessRole()
                .withOrganisationId(organisation.getId())
                .withRole(Role.COLLABORATOR)
                .build(1);

        Application application = newApplication()
                .withProcessRoles(inviteProcessRoles.get(0))
                .build();

        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation)
                .build();

        ApplicationInvite invite = newApplicationInvite()
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(inviteOrganisation)
                .build();

        when(applicationInviteMapper.mapIdToDomain(invite.getId())).thenReturn(invite);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId()))
                .thenReturn(inviteProcessRoles);
        when(applicationProgressServiceMock.updateApplicationProgress(application.getId()))
                .thenReturn(serviceSuccess(BigDecimal.valueOf(35L)));

        ServiceResult<Void> applicationInviteResult = inviteService.removeApplicationInvite(invite.getId());

        InOrder inOrder = inOrder(
                questionReassignmentServiceMock,
                processRoleRepositoryMock,
                inviteOrganisationRepositoryMock
        );
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(
                invite.getTarget().getId(),
                inviteProcessRoles,
                invite.getTarget().getLeadApplicantProcessRole()
        );
        inOrder.verify(processRoleRepositoryMock).deleteAll(inviteProcessRoles);
        inOrder.verify(inviteOrganisationRepositoryMock).delete(invite.getInviteOrganisation());
        inOrder.verifyNoMoreInteractions();

        assertThat(applicationInviteResult.isSuccess()).isTrue();
    }

    @Test
    public void removeApplicationInvite_deletesInviteFromInviteOrganisationButNotOrganisationFinanceData() throws Exception {
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        List<ProcessRole> inviteProcessRoles = newProcessRole()
                .withOrganisationId(organisation.getId())
                .withRole(Role.COLLABORATOR)
                .build(1);

        Application application = newApplication()
                .withProcessRoles(inviteProcessRoles.get(0))
                .build();

        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation)
                .build();

        List<ApplicationInvite> applicationInvites = newApplicationInvite()
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(inviteOrganisation)
                .build(2);

        ApplicationInvite applicationInviteToDelete = applicationInvites.get(0);
        inviteOrganisation.setInvites(applicationInvites);

        when(applicationInviteMapper.mapIdToDomain(applicationInviteToDelete.getId()))
                .thenReturn(applicationInviteToDelete);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId()))
                .thenReturn(inviteProcessRoles);

        ServiceResult<Void> applicationInviteResult =
                inviteService.removeApplicationInvite(applicationInviteToDelete.getId());

        InOrder inOrder = inOrder(questionReassignmentServiceMock, processRoleRepositoryMock);
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(
                applicationInviteToDelete.getTarget().getId(),
                inviteProcessRoles,
                applicationInviteToDelete.getTarget().getLeadApplicantProcessRole()
        );
        inOrder.verify(processRoleRepositoryMock).deleteAll(inviteProcessRoles);
        inOrder.verifyNoMoreInteractions();

        assertThat(applicationInviteResult.isSuccess()).isTrue();
    }

    @Test
    public void removeApplicationInvite_deletesInviteFromInviteOrganisationAndOrganisationFinanceDataIfLastActiveUser() {
        User user = newUser().build();
        Organisation organisation = newOrganisation().build();

        List<ProcessRole> inviteProcessRoles = newProcessRole()
                .withOrganisationId(organisation.getId())
                .withRole(Role.COLLABORATOR)
                .build(1);

        Application application = newApplication()
                .withProcessRoles(inviteProcessRoles.get(0))
                .build();

        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation)
                .build();

        List<ApplicationInvite> applicationInvites = newApplicationInvite()
                .withUser(user)
                .withApplication(application)
                .withInviteOrganisation(inviteOrganisation)
                .build(2);

        ApplicationInvite applicationInviteToDelete = applicationInvites.get(0);
        inviteOrganisation.setInvites(applicationInvites);

        ApplicationFinance applicationFinance = newApplicationFinance().build();

        when(applicationInviteMapper.mapIdToDomain(applicationInviteToDelete.getId()))
                .thenReturn(applicationInviteToDelete);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId()))
                .thenReturn(inviteProcessRoles);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(
                application.getId(),
                organisation.getId()
        ))
                .thenReturn(applicationFinance);
        when(applicationProgressServiceMock.updateApplicationProgress(application.getId()))
                .thenReturn(serviceSuccess(BigDecimal.valueOf(35L)));

        ServiceResult<Void> applicationInviteResult =
                inviteService.removeApplicationInvite(applicationInviteToDelete.getId());

        inviteOrganisation.getInvites().remove(applicationInviteToDelete);

        InOrder inOrder = inOrder(
                questionReassignmentServiceMock,
                processRoleRepositoryMock,
                applicationFinanceRepositoryMock
        );
        inOrder.verify(questionReassignmentServiceMock).reassignCollaboratorResponsesAndQuestionStatuses(
                applicationInviteToDelete.getTarget().getId(),
                inviteProcessRoles,
                applicationInviteToDelete.getTarget().getLeadApplicantProcessRole()
        );
        inOrder.verify(processRoleRepositoryMock).deleteAll(inviteProcessRoles);
        inOrder.verify(applicationFinanceRepositoryMock).findByApplicationIdAndOrganisationId(
                application.getId(),
                organisation.getId()
        );
        inOrder.verify(applicationFinanceRepositoryMock).delete(applicationFinance);
        inOrder.verifyNoMoreInteractions();

        assertThat(applicationInviteResult.isSuccess()).isTrue();
        assertThat(inviteOrganisation.getOrganisation()).isNull();
    }

    private void assertInvalidInvites(List<ApplicationInviteResource> inviteResources) {
        Long applicationId = 1L;

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource()
                .withInviteResources(inviteResources)
                .withOrganisationName("new organisation")
                .build();

        when(inviteOrganisationMapper.mapToResource(isA(List.class))).thenReturn(singletonList(inviteOrganisationResource));
        when(applicationRepositoryMock.findById(null)).thenReturn(Optional.of(newApplication().withId(1L).build()));
        when(inviteOrganisationRepositoryMock.findAllById(isA(List.class))).thenReturn(newInviteOrganisation().build(1));

        ServiceResult<Void> result = inviteService.createApplicationInvites(inviteOrganisationResource, Optional.of(applicationId));
        assertThat(result.isFailure()).isTrue();

        verify(inviteOrganisationRepositoryMock, never()).save(isA(InviteOrganisation.class));
        verify(applicationInviteRepositoryMock, never()).saveAll(isA(List.class));
    }
}
