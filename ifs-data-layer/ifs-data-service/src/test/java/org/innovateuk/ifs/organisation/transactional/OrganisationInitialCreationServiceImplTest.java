package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OrganisationInitialCreationServiceImplTest extends BaseServiceUnitTest<OrganisationInitialCreationService> {

    @Mock
    private OrganisationMatchingService organisationMatchingService;

    protected OrganisationInitialCreationService supplyServiceUnderTest() {
        return new OrganisationInitialCreationServiceImpl();
    }

    @Test
    public void createOrMatch_noMatchFoundShouldResultInSaveCall() throws Exception {
        Organisation createdOrganisation = newOrganisation().build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.empty());
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);
        when(organisationMapperMock.mapToResource(any(Organisation.class))).thenReturn(organisationResource);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock,times(1)).save(any(Organisation.class));
    }

    @Test
    public void createOrMatch_matchFoundShouldResultInNoSaveCall() throws Exception {
        Organisation createdOrganisation = newOrganisation().build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.of(newOrganisation().build()));
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);
        when(organisationMapperMock.mapToResource(any(Organisation.class))).thenReturn(organisationResource);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock,times(1)).save(any(Organisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationLinkedShouldResultInNoSaveCall() throws Exception {
        String inviteHash = "hashabc123";
        OrganisationResource organisationResource = newOrganisationResource().build();
        ApplicationInvite invite = newApplicationInvite().withInviteOrganisation(newInviteOrganisation().build()).build();

        when(inviteServiceMock.findOneByHash(inviteHash)).thenReturn(serviceSuccess(invite));
        when(organisationMapperMock.mapToResource(any(Organisation.class))).thenReturn(organisationResource);

        service.createAndLinkByInvite(organisationResource, inviteHash);

        verify(organisationRepositoryMock, times(0)).save(any(Organisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationNotLinkedShouldResultInSaveCall() throws Exception {
        String inviteHash = "hashabc123";
        OrganisationResource organisationResource = newOrganisationResource().build();
        ApplicationInvite invite = newApplicationInvite().withInviteOrganisation(newInviteOrganisation().build()).build();

        when(inviteServiceMock.findOneByHash(inviteHash)).thenReturn(serviceSuccess(invite));
        when(organisationMapperMock.mapToResource(any(Organisation.class))).thenReturn(organisationResource);

        service.createAndLinkByInvite(organisationResource, inviteHash);

        verify(organisationRepositoryMock, times(1)).save(any(Organisation.class));
    }

}