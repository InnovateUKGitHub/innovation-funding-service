package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrganisationInitialCreationServiceImplTest extends BaseServiceUnitTest<OrganisationInitialCreationService> {

    @Mock
    private OrganisationMatchingServiceImpl organisationMatchingService;

    @Mock
    private OrganisationMapper organisationMapperMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ApplicationInviteService applicationInviteServiceMock;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepositoryMock;

    protected OrganisationInitialCreationService supplyServiceUnderTest() {
        return new OrganisationInitialCreationServiceImpl();
    }

    private OrganisationResource organisationResource;
    private Organisation organisation;

    @Before
    public void setUp() {
        organisationResource = newOrganisationResource().build();
        organisation = newOrganisation().build();

        when(organisationMapperMock.mapToResource(any(Organisation.class))).thenReturn(organisationResource);
        when(organisationMapperMock.mapToDomain(any(OrganisationResource.class))).thenReturn(organisation);
    }

    @Test
    public void createOrMatch_noMatchFound_organisationSouldBeSaved() {
        Organisation createdOrganisation = newOrganisation().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.empty());
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock).save(any(Organisation.class));
    }

    @Test
    public void createOrMatch_matchFound_organisationShouldNotBeSaved() {
        Organisation createdOrganisation = newOrganisation().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.of(newOrganisation().build()));
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock, never()).save(any(Organisation.class));
    }
}