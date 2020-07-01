package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.threads.repository.NoteRepository;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartnerOrganisationServiceImplTest extends BaseServiceUnitTest<PartnerOrganisationService> {

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Mock
    private PartnerOrganisationMapper partnerOrganisationMapper;

    @Mock
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Mock
    private RemovePartnerNotificationService removePartnerNotificationService;

    @Mock
    private ProjectPartnerChangeService projectPartnerChangeService;

    private Long projectId = 123L;
    private List<Organisation> organisations;
    private Application application;
    private Project project;
    private List<User> users;
    private List<ProjectUser> projectUsers;
    private List<PartnerOrganisationResource> partnerOrganisationResources;
    private List<PartnerOrganisation> partnerOrganisations;
    private PendingPartnerProgress pendingPartnerProgress;
    private List<ProjectFinance> projectFinance;
    private List<BankDetails> bankDetails;

    @Before
    public void setup() {
        users = newUser().withId(3L, 8L, 71L).withFirstName("James", "Rex").withLastName("Reid", "Mill").build(2);
        organisations = newOrganisation()
                .withOrganisationType(BUSINESS)
                .withName("Empire", "Ludlow")
                .build(2);
        projectUsers = newProjectUser()
                .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                .withUser(users.get(0), users.get(1))
                .build(2);
        project = newProject()
                .withId(projectId)
                .withName("Smart ideas for plastic recycling")
                .withProjectUsers(projectUsers)
                .withApplication(application)
                .withDuration(6L)
                .build();
        partnerOrganisations = newPartnerOrganisation()
                .withId(3L, 18L)
                .withOrganisation(organisations.get(0), organisations.get(1))
                .withProject(project)
                .withLeadOrganisation(true, false)
                .build(2);
        partnerOrganisationResources = newPartnerOrganisationResource()
                .withId(10L, 20L)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId())
                .withLeadOrganisation(true, false)
                .withProject(projectId)
                .build(2);
        application = newApplication().withId(77L).build();
        projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisations.get(0), organisations.get(1))
                .build(2);
        bankDetails = newBankDetails()
                .withOrganisation(organisations.get(0), organisations.get(1))
                .withProject(project)
                .build(2);
    }

    @Test
    public void getPartnerOrganisation() {
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId())).thenReturn(partnerOrganisations.get(0));
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId())).thenReturn(partnerOrganisations.get(1));
        when(partnerOrganisationMapper.mapToResource(partnerOrganisations.get(0))).thenReturn(partnerOrganisationResources.get(0));
        when(partnerOrganisationMapper.mapToResource(partnerOrganisations.get(1))).thenReturn(partnerOrganisationResources.get(1));

        ServiceResult<PartnerOrganisationResource> result = service.getPartnerOrganisation(projectId, organisations.get(0).getId());
        ServiceResult<PartnerOrganisationResource> result2 = service.getPartnerOrganisation(projectId, organisations.get(1).getId());

        assertEquals(partnerOrganisationResources.get(0), result.getSuccess());
        assertEquals(partnerOrganisationResources.get(1), result2.getSuccess());

        verify(partnerOrganisationRepository, times(1)).findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId());
        verify(partnerOrganisationRepository, times(1)).findOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verifyNoMoreInteractions(partnerOrganisationRepository);
    }

    @Test
    public void getProjectPartnerOrganisations() {
        when(partnerOrganisationRepository.findByProjectId(projectId)).thenReturn(partnerOrganisations);
        when(partnerOrganisationMapper.mapToResource(partnerOrganisations.get(0))).thenReturn(partnerOrganisationResources.get(0));
        when(partnerOrganisationMapper.mapToResource(partnerOrganisations.get(1))).thenReturn(partnerOrganisationResources.get(1));

        ServiceResult<List<PartnerOrganisationResource>> result = service.getProjectPartnerOrganisations(projectId);

        assertTrue(result.isSuccess());
        assertEquals(partnerOrganisationResources, result.getSuccess());
        verify(partnerOrganisationRepository, times(1)).findByProjectId(projectId);
        verifyNoMoreInteractions(partnerOrganisationRepository);
    }

    @Test
    public void removeNonLeadPartnerOrganisation() {
        pendingPartnerProgress = new PendingPartnerProgress(partnerOrganisations.get(1));
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisations.get(1).getId());

        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId())).thenReturn(partnerOrganisations.get(1));
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(organisations.get(1).getId(), projectId)).thenReturn(Optional.of(pendingPartnerProgress));
        when(projectFinanceRepository.findByProjectIdAndOrganisationId(projectId, organisations.get(1).getId())).thenReturn(Optional.of(projectFinance.get(1)));
        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisations.get(1).getId())).thenReturn(Optional.of(bankDetails.get(1)));
        when(projectUserRepository.findByProjectIdAndRole(projectId, PROJECT_MANAGER)).thenReturn(Optional.of(projectUsers.get(0)));

        ServiceResult<Void> result = service.removePartnerOrganisation(projectOrganisationCompositeId);

        assertTrue(result.isSuccess());
        verify(partnerOrganisationRepository, times(1)).findOneByProjectIdAndOrganisationId(projectOrganisationCompositeId.getProjectId(), projectOrganisationCompositeId.getOrganisationId());
        verify(pendingPartnerProgressRepository, times(1)).findByOrganisationIdAndProjectId(organisations.get(1).getId(), projectId);
        verify(projectFinanceRepository, times(1)).findByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(bankDetailsRepository, times(1)).findByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(projectUserInviteRepository, times(1)).deleteAllByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(projectPartnerInviteRepository, times(1)).deleteByProjectIdAndInviteOrganisationOrganisationId(projectId, organisations.get(1).getId());
        verify(projectUserRepository, times(1)).deleteAllByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(partnerOrganisationRepository, times(1)).deleteOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(projectFinanceRowRepository, times(1)).deleteAllByTargetId(projectFinance.get(1).getId());
        verify(projectFinanceRepository, times(1)).deleteAllByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verify(noteRepository, times(1)).deleteAllByClassPk(projectFinance.get(1).getId());
        verify(queryRepository, times(1)).deleteAllByClassPk(projectFinance.get(1).getId());
        verify(projectPartnerChangeService, times(1)).updateProjectWhenPartnersChange(projectOrganisationCompositeId.getProjectId());
        verify(removePartnerNotificationService, times(1)).sendNotifications(project, organisations.get(1));
    }

    @Test
    public void removeLeadPartnerOrganisation() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisations.get(0).getId());
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId())).thenReturn(partnerOrganisations.get(0));

        ServiceResult<Void> result = service.removePartnerOrganisation(projectOrganisationCompositeId);

        assertTrue(result.isFailure());
        verify(partnerOrganisationRepository, times(1)).findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId());
        verifyNoMoreInteractions(partnerOrganisationRepository);
    }

    @Override
    protected PartnerOrganisationService supplyServiceUnderTest() {
        return new PartnerOrganisationServiceImpl();
    }
}