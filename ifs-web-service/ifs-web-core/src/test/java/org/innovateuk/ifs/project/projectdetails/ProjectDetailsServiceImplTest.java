package org.innovateuk.ifs.project.projectdetails;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.invite.service.ProjectInviteRestService;
import org.innovateuk.ifs.project.projectdetails.service.ProjectDetailsRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDetailsServiceImplTest {

    @InjectMocks
    private ProjectDetailsServiceImpl service;

    @Mock
    private ProjectDetailsRestService projectDetailsRestService;

    @Mock
    private ProjectInviteRestService projectInviteRestService;

    @Test
    public void testUpdateFinanceContact() {
        Long projectId = 1L;
        Long organisationId = 2L;
        Long financeContactId = 3L;

        when(projectDetailsRestService.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId)).thenReturn(restSuccess());

        service.updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId);

        verify(projectDetailsRestService).updateFinanceContact(new ProjectOrganisationCompositeId(projectId, organisationId), financeContactId);
    }

    @Test
    public void testUpdateProjectManager() {
        when(projectDetailsRestService.updateProjectManager(1L, 2L)).thenReturn(restSuccess());

        service.updateProjectManager(1L, 2L);

        verify(projectDetailsRestService).updateProjectManager(1L, 2L);
    }

    public void testUpdateProjectStartDate() {
        LocalDate date = LocalDate.now();

        when(projectDetailsRestService.updateProjectStartDate(1L, date)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateProjectStartDate(1L, date);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updateProjectStartDate(1L, date).toServiceResult();
    }

    @Test
    public void testUpdateAddress() {
        Long leadOrgId = 1L;
        Long projectId = 2L;
        AddressResource addressResource = newAddressResource().build();

        when(projectDetailsRestService.updateProjectAddress(leadOrgId, projectId, REGISTERED, addressResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateAddress(leadOrgId, projectId, REGISTERED, addressResource);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).updateProjectAddress(leadOrgId, projectId, REGISTERED, addressResource);
    }

/*    @Test
    public void testSetApplicationDetailsSubmitted() {
        when(projectDetailsRestService.setApplicationDetailsSubmitted(1L)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.setApplicationDetailsSubmitted(1L);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).setApplicationDetailsSubmitted(1L);
    }

    @Test
    public void testIsSubmitAllowed() {
        when(projectDetailsRestService.isSubmitAllowed(1L)).thenReturn(restSuccess(false));

        ServiceResult<Boolean> result = service.isSubmitAllowed(1L);

        assertTrue(result.isSuccess());

        verify(projectDetailsRestService).isSubmitAllowed(1L);
    }*/

    @Test
    public void testInviteProjectFinanceUser() throws Exception {

        InviteProjectResource invite = ProjectInviteResourceBuilder.newInviteProjectResource().build();

        when(projectDetailsRestService.inviteFinanceContact(anyLong(), any())).thenReturn(restSuccess());

        ServiceResult<Void> submitted = service.inviteFinanceContact(1L, invite);

        assertTrue(submitted.isSuccess());

        verify(projectDetailsRestService).inviteFinanceContact(1L, invite);
    }

    @Test
    public void testInviteProjectManagerUser() throws Exception {

        InviteProjectResource invite = ProjectInviteResourceBuilder.newInviteProjectResource().build();

        when(projectDetailsRestService.inviteProjectManager(anyLong(), any())).thenReturn(restSuccess());

        ServiceResult<Void> submitted = service.inviteProjectManager(1L, invite);

        assertTrue(submitted.isSuccess());

        verify(projectDetailsRestService).inviteProjectManager(1L, invite);
    }

    @Test
    public void testSaveProjectInvite() throws Exception {

        InviteProjectResource invite = ProjectInviteResourceBuilder.newInviteProjectResource().build();

        when(projectInviteRestService.saveProjectInvite(invite)).thenReturn(restSuccess());

        ServiceResult<Void> submitted = service.saveProjectInvite(invite);

        assertTrue(submitted.isSuccess());

        verify(projectInviteRestService).saveProjectInvite(invite);

    }
}