package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.PartnerOrganisationMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PartnerOrganisationServiceImplTest extends BaseServiceUnitTest<PartnerOrganisationService> {

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    private PartnerOrganisationMapper partnerOrganisationMapperMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    private Long projectId = 123L;
    private List<Organisation> organisations;
    private Application application;
    private Project project;
    private List<User> users;
    private MonitoringOfficer monitoringOfficer;
    private List<ProjectUser> projectUsers;
    private List<PartnerOrganisationResource> partnerOrganisationResources;
    private List<PartnerOrganisation> partnerOrganisations;

    @Before
    public void setup() {
        users = newUser().withId(3L,8L, 71L).withFirstName("James", "Rex", "Orville").withLastName("Reid", "Mill", "Gibbs").build(3);
        organisations = newOrganisation()
                .withId(63L, 55L)
                .withOrganisationType(BUSINESS)
                .withName("Empire", "Ludlow")
                .build(2);

        project = newProject()
                .withId(projectId)
                .withName("Smart ideas for plastic recycling")
                .withProjectUsers(newProjectUser()
                        .withRole(PROJECT_MANAGER, PROJECT_PARTNER)
                        .withUser(users.get(0), users.get(1))
                        .build(2))
                .withApplication(application)
                .withDuration(6L)
                .build();

        partnerOrganisations =newPartnerOrganisation()
                .withId(3L, 18L)
                .withOrganisation(organisations.get(0), organisations.get(1))
                .withLeadOrganisation(true, false)
                .build(2);
        partnerOrganisationResources = newPartnerOrganisationResource()
                .withId(10L, 20L)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId())
                .withLeadOrganisation(true, false)
                .withProject(projectId)
                .build(2);
        application = newApplication().withId(77L).build();

    }

    @Test
    public void getPartnerOrganisation() {
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId())).thenReturn(partnerOrganisations.get(0));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId())).thenReturn(partnerOrganisations.get(1));
        when(partnerOrganisationMapperMock.mapToResource(partnerOrganisations.get(0))).thenReturn(partnerOrganisationResources.get(0));
        when(partnerOrganisationMapperMock.mapToResource(partnerOrganisations.get(1))).thenReturn(partnerOrganisationResources.get(1));

        ServiceResult<PartnerOrganisationResource>result = service.getPartnerOrganisation(projectId, organisations.get(0).getId());
        ServiceResult<PartnerOrganisationResource>result2 = service.getPartnerOrganisation(projectId, organisations.get(1).getId());

        assertEquals(partnerOrganisationResources.get(0), result.getSuccess());
        assertEquals(partnerOrganisationResources.get(1), result2.getSuccess());

        verify(partnerOrganisationRepositoryMock, times(1)).findOneByProjectIdAndOrganisationId(projectId, organisations.get(0).getId());
        verify(partnerOrganisationRepositoryMock, times(1)).findOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId());
        verifyNoMoreInteractions(partnerOrganisationRepositoryMock);
    }

    @Test
    public void getProjectPartnerOrganisations() {
        when(partnerOrganisationRepositoryMock.findByProjectId(projectId)).thenReturn(partnerOrganisations);
        when(partnerOrganisationMapperMock.mapToResource(partnerOrganisations.get(0))).thenReturn(partnerOrganisationResources.get(0));
        when(partnerOrganisationMapperMock.mapToResource(partnerOrganisations.get(1))).thenReturn(partnerOrganisationResources.get(1));

        ServiceResult<List<PartnerOrganisationResource>> result = service.getProjectPartnerOrganisations(projectId);

        assertTrue(result.isSuccess());
        assertEquals(partnerOrganisationResources, result.getSuccess());
        verify(partnerOrganisationRepositoryMock, times(1)).findByProjectId(projectId);
        verifyNoMoreInteractions(partnerOrganisationRepositoryMock);
    }

//    @Test
//    public void removePartnerOrganisation() {
//
//        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(projectId, organisations.get(1).getId())).thenReturn(partnerOrganisations.get(1));
//
//        service.removePartnerOrganisation(projectId, organisations.get(1).getId());
//
//        assertEquals(partnerOrganisationResources.get(0), service.getPartnerOrganisation(projectId, organisations.get(0).getId()).getSuccess());
//        assertNull(service.getPartnerOrganisation(projectId, organisations.get(1).getId()).getSuccess());
//
//    }

    @Override
    protected PartnerOrganisationService supplyServiceUnderTest() {
        return new PartnerOrganisationServiceImpl();
    }
}