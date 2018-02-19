package org.innovateuk.ifs.assessment.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InterviewPanelInviteServiceImplTest extends BaseServiceUnitTest<InterviewPanelInviteServiceImpl> {

    @Override
    protected InterviewPanelInviteServiceImpl supplyServiceUnderTest() {
        return new InterviewPanelInviteServiceImpl();
    }

    @Test
    public void getAvailableApplications() {
        long competitionId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        int totalApplications = 2;
        String leadOrganisationName = "lead org";

        Pageable pageRequest = new PageRequest(pageNumber, pageSize);

        Organisation leadOrganisation = newOrganisation()
                .withName(leadOrganisationName)
                .build();

        List<Application> expectedApplications =
                newApplication()
                        .withProcessRoles(
                                newProcessRole()
                                        .withRole(newRole().withType(UserRoleType.LEADAPPLICANT).build())
                                        .withOrganisationId(leadOrganisation.getId())
                                        .build()
                        )
                        .build(totalApplications);

        Page<Application> expectedPage = new PageImpl<>(expectedApplications, pageRequest, totalApplications);

        when(applicationRepositoryMock.findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageRequest)).thenReturn(expectedPage);
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);

        AvailableApplicationPageResource availableApplicationPageResource = service.getAvailableApplications(competitionId, pageRequest).getSuccess();

        assertEquals(pageNumber, availableApplicationPageResource.getNumber());
        assertEquals(pageSize, availableApplicationPageResource.getSize());
        assertEquals(totalApplications, availableApplicationPageResource.getTotalElements());

        assertAvailableApplicationResourcesMatch(leadOrganisation, expectedApplications, availableApplicationPageResource);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageRequest);
        inOrder.verify(organisationRepositoryMock, times(totalApplications)).findOne(leadOrganisation.getId());
        inOrder.verifyNoMoreInteractions();
    }
    @Test
    public void getStagedApplications() {
    }

    @Test
    public void getAvailableApplicationIds() {
    }

    @Test
    public void assignApplications() {
    }

    private static void assertAvailableApplicationResourcesMatch(Organisation leadOrganisation, List<Application> expectedApplications, AvailableApplicationPageResource availableApplicationPageResource) {
        assertEquals(expectedApplications.size(), availableApplicationPageResource.getContent().size());

        for (int i=0; i<expectedApplications.size(); i++) {
            final AvailableApplicationResource availableApplicationResource = availableApplicationPageResource.getContent().get(i);
            final Application expectedApplication = expectedApplications.get(i);
            assertAvailableApplicationResourceMatches(expectedApplication, availableApplicationResource, leadOrganisation);
        }
    }

    private static void assertAvailableApplicationResourceMatches(Application application, AvailableApplicationResource availableApplicationResource, Organisation leadOrganisation) {
        assertEquals(application.getId(), (Long) availableApplicationResource.getId());
        assertEquals(application.getName(), availableApplicationResource.getName());
        assertEquals(leadOrganisation.getName(), availableApplicationResource.getLeadOrganisation(), leadOrganisation.getName());
    }

}