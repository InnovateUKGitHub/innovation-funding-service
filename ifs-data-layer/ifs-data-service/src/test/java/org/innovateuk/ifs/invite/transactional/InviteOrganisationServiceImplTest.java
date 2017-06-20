package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Test;
import org.mockito.InOrder;

import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class InviteOrganisationServiceImplTest extends BaseServiceUnitTest<InviteOrganisationServiceImpl> {

    @Override
    protected InviteOrganisationServiceImpl supplyServiceUnderTest() {
        return new InviteOrganisationServiceImpl();
    }

    @Test
    public void getById() throws Exception {
        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRepositoryMock.findOne(inviteOrganisation.getId())).thenReturn(inviteOrganisation);
        when(inviteOrganisationMapperMock.mapToResource(inviteOrganisation)).thenReturn(inviteOrganisationResource);

        ServiceResult<InviteOrganisationResource> result = service.getById(inviteOrganisation.getId());
        assertEquals(inviteOrganisationResource, result.getSuccessObject());

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, inviteOrganisationMapperMock);
        inOrder.verify(inviteOrganisationRepositoryMock).findOne(inviteOrganisation.getId());
        inOrder.verify(inviteOrganisationMapperMock).mapToResource(inviteOrganisation);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        long organisationId = 1L;
        long applicationId = 2L;

        InviteOrganisation inviteOrganisation = newInviteOrganisation().build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRepositoryMock.findOneByOrganisationIdAndInvitesApplicationId(organisationId, applicationId)).thenReturn(inviteOrganisation);
        when(inviteOrganisationMapperMock.mapToResource(inviteOrganisation)).thenReturn(inviteOrganisationResource);

        ServiceResult<InviteOrganisationResource> result = service.getByOrganisationIdWithInvitesForApplication(organisationId, applicationId);
        assertEquals(inviteOrganisationResource, result.getSuccessObject());

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, inviteOrganisationMapperMock);
        inOrder.verify(inviteOrganisationRepositoryMock).findOneByOrganisationIdAndInvitesApplicationId(organisationId, applicationId);
        inOrder.verify(inviteOrganisationMapperMock).mapToResource(inviteOrganisation);
        inOrder.verifyNoMoreInteractions();
    }
}