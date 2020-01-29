package org.innovateuk.ifs.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.projectdetails.service.ProjectDetailsRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectDetailsServiceImplTest {

    @InjectMocks
    private ProjectDetailsServiceImpl service;

    @Mock
    private ProjectDetailsRestService projectDetailsRestService;

    @Test
    public void updateFinanceContact() {
        long projectId = 1L;
        long organisationId = 2L;
        long financeContactId = 3L;

        when(projectDetailsRestService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId)).thenReturn(restSuccess());

        service.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId);

        verify(projectDetailsRestService).updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId);
        verifyNoMoreInteractions(projectDetailsRestService);
    }

    @Test
    public void updatePartnerProjectLocation() {
        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "TW14 9QG";

        when(projectDetailsRestService.updatePartnerProjectLocation(projectId, organisationId, postcode)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updatePartnerProjectLocation(projectId, organisationId, postcode);
        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updatePartnerProjectLocation(projectId, organisationId, postcode);
        verifyNoMoreInteractions(projectDetailsRestService);
    }

    @Test
    public void updateProjectManager() {
        when(projectDetailsRestService.updateProjectManager(1L, 2L)).thenReturn(restSuccess());

        service.updateProjectManager(1L, 2L);

        verify(projectDetailsRestService).updateProjectManager(1L, 2L);
        verifyNoMoreInteractions(projectDetailsRestService);
    }

    @Test
    public void updateProjectStartDate() {
        LocalDate date = LocalDate.now();

        when(projectDetailsRestService.updateProjectStartDate(1L, date)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateProjectStartDate(1L, date);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updateProjectStartDate(1L, date);
    }

    @Test
    public void updateProjectDuration() {
        long projectId = 3L;
        long durationInMonths = 18L;

        when(projectDetailsRestService.updateProjectDuration(projectId, durationInMonths)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateProjectDuration(projectId, durationInMonths);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updateProjectDuration(projectId, durationInMonths);
        verifyNoMoreInteractions(projectDetailsRestService);
    }

    @Test
    public void updateAddress() {
        long leadOrgId = 1L;
        long projectId = 2L;
        AddressResource addressResource = newAddressResource().build();

        when(projectDetailsRestService.updateProjectAddress(leadOrgId, projectId, addressResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateAddress(leadOrgId, projectId, addressResource);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updateProjectAddress(leadOrgId, projectId, addressResource);
        verifyNoMoreInteractions(projectDetailsRestService);
    }
}