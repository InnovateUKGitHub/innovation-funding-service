package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.transactional.ApplicationInviteService;
import org.innovateuk.ifs.organisation.mapper.OrganisationMapper;
import org.innovateuk.ifs.organisation.service.OrganisationMatchingServiceImpl;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
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

    private final String testInviteHash = "hashabc123";
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
    public void createOrMatch_noMatchFound_organisationSouldBeSaved() throws Exception {
        Organisation createdOrganisation = newOrganisation().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.empty());
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock).save(any(Organisation.class));
    }

    @Test
    public void createOrMatch_matchFound_organisationShouldNotBeSaved() throws Exception {
        Organisation createdOrganisation = newOrganisation().build();

        when(organisationMatchingService.findOrganisationMatch(any())).thenReturn(Optional.of(newOrganisation().build()));
        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);

        ServiceResult<OrganisationResource> result = service.createOrMatch(organisationResource);

        assertTrue(result.isSuccess());

        verify(organisationRepositoryMock, never()).save(any(Organisation.class));
    }

    @Test
    public void createAndLinkByInvite_linkedOrganisationShouldNotBeCreated() throws Exception {
        ApplicationInvite inviteWithExistingOrganisation = newApplicationInvite()
                .withInviteOrganisation(
                        newInviteOrganisation()
                                .withOrganisation(newOrganisation().build())
                                .build()
                )
                .build();

        when(applicationInviteServiceMock.findOneByHash(testInviteHash)).thenReturn(serviceSuccess(inviteWithExistingOrganisation));

        service.createAndLinkByInvite(organisationResource, testInviteHash);

        verify(organisationRepositoryMock, never()).save(any(Organisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationCreatedAndLinkedIfNoMatchingOrganisationFound() throws Exception {
        ApplicationInvite invite = newApplicationInvite()
                .withInviteOrganisation(newInviteOrganisation().build())
                .build();

        when(applicationInviteServiceMock.findOneByHash(testInviteHash)).thenReturn(serviceSuccess(invite));
        when(organisationMatchingService.findOrganisationMatch(organisationResource)).thenReturn(Optional.empty());

        expectOrganisationToBeCreatedAndLinked();

        service.createAndLinkByInvite(organisationResource, testInviteHash);

        verify(organisationRepositoryMock).save(any(Organisation.class));
        verify(inviteOrganisationRepositoryMock).save(any(InviteOrganisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationCreatedAndLinkedIfMatchingOrganisationExistsOnApplication() {
        ApplicationInvite invite = createApplicationInviteWithLeadOrganisationId(2L);

        Organisation matchingOrganisation = newOrganisation()
                .withId(1L)
                .build();

        when(applicationInviteServiceMock.findOneByHash(testInviteHash)).thenReturn(serviceSuccess(invite));

        when(organisationMatchingService.findOrganisationMatch(organisationResource))
                .thenReturn(Optional.of(matchingOrganisation));
        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                matchingOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.of(
                        newInviteOrganisation()
                                .withOrganisation(matchingOrganisation)
                                .build()
                ));

        expectOrganisationToBeCreatedAndLinked();

        service.createAndLinkByInvite(organisationResource, testInviteHash);

        verify(organisationRepositoryMock).save(any(Organisation.class));
        verify(inviteOrganisationRepositoryMock).save(any(InviteOrganisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationCreatedAndLinkedIfMatchingOrganisationIsLead() {
        long leadOrganisationId = 1L;

        ApplicationInvite invite = createApplicationInviteWithLeadOrganisationId(leadOrganisationId);

        Organisation matchingOrganisation = newOrganisation()
                .withId(leadOrganisationId)
                .build();

        when(applicationInviteServiceMock.findOneByHash(testInviteHash)).thenReturn(serviceSuccess(invite));

        when(organisationMatchingService.findOrganisationMatch(organisationResource))
                .thenReturn(Optional.of(matchingOrganisation));
        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                matchingOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.empty());

        when(inviteOrganisationRepositoryMock.save(any(InviteOrganisation.class)))
                .thenReturn(
                        newInviteOrganisation()
                                .withOrganisation(newOrganisation().build())
                                .build()
                );

        service.createAndLinkByInvite(organisationResource, testInviteHash);

        verify(organisationRepositoryMock).save(any(Organisation.class));
        verify(inviteOrganisationRepositoryMock).save(any(InviteOrganisation.class));
    }

    @Test
    public void createAndLinkByInvite_organisationOnlyLinkedIfMatchingOrganisationIsNotLeadAndDoesNotExistOnApplication() {
        ApplicationInvite invite = createApplicationInviteWithLeadOrganisationId(2L);

        Organisation matchingOrganisation = newOrganisation()
                .withId(1L)
                .build();

        when(applicationInviteServiceMock.findOneByHash(testInviteHash)).thenReturn(serviceSuccess(invite));

        when(organisationMatchingService.findOrganisationMatch(organisationResource))
                .thenReturn(Optional.of(matchingOrganisation));
        when(inviteOrganisationRepositoryMock.findFirstByOrganisationIdAndInvitesApplicationId(
                matchingOrganisation.getId(),
                invite.getTarget().getId()
        ))
                .thenReturn(Optional.empty());

        when(inviteOrganisationRepositoryMock.save(any(InviteOrganisation.class)))
                .thenReturn(
                        newInviteOrganisation()
                                .withOrganisation(newOrganisation().build())
                                .build()
                );

        service.createAndLinkByInvite(organisationResource, testInviteHash);

        verify(organisationRepositoryMock, never()).save(any(Organisation.class));
        verify(inviteOrganisationRepositoryMock).save(any(InviteOrganisation.class));
    }

    private ApplicationInvite createApplicationInviteWithLeadOrganisationId(long organisationId) {
        return newApplicationInvite()
                .withApplication(
                        newApplication()
                                .withProcessRoles(
                                        newProcessRole()
                                                .withRole(Role.LEADAPPLICANT)
                                                .withOrganisationId(organisationId)
                                                .build()
                                )
                                .build()
                )
                .withInviteOrganisation(newInviteOrganisation().build())
                .build();
    }

    private void expectOrganisationToBeCreatedAndLinked() {
        Organisation createdOrganisation = newOrganisation().build();

        when(organisationRepositoryMock.save(any(Organisation.class))).thenReturn(createdOrganisation);
        when(inviteOrganisationRepositoryMock.save(any(InviteOrganisation.class)))
                .thenReturn(
                        newInviteOrganisation()
                                .withOrganisation(createdOrganisation)
                                .build()
                );
    }
}