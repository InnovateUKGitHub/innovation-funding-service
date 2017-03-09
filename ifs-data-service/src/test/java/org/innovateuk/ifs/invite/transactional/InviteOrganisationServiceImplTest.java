package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class InviteOrganisationServiceImplTest extends BaseServiceUnitTest<InviteOrganisationServiceImpl> {

    @Override
    protected InviteOrganisationServiceImpl supplyServiceUnderTest() {
        return new InviteOrganisationServiceImpl();
    }

    @Test
    public void getByIdWithInvitesForApplication() throws Exception {

        List<Application> applications = newApplication()
                .build(2);

        List<ApplicationInvite> applicationInvitesUnfiltered = newApplicationInvite()
                .withApplication(applications.get(0), applications.get(0), applications.get(1))
                .build(3);

        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withInvites(applicationInvitesUnfiltered)
                .build();

        List<ApplicationInvite> applicationInvitesFiltered = asList(applicationInvitesUnfiltered.get(0), applicationInvitesUnfiltered.get(1));

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRepositoryMock.findOne(inviteOrganisation.getId())).thenReturn(inviteOrganisation);
        when(inviteOrganisationMapperMock.mapToResource(createInviteOrganisationExpectations(
                inviteOrganisation.getId(),
                applicationInvitesFiltered
        ))).thenReturn(inviteOrganisationResource);

        ServiceResult<InviteOrganisationResource> result = service.getByIdWithInvitesForApplication(
                inviteOrganisation.getId(), applications.get(0).getId());
        assertEquals(inviteOrganisationResource, result.getSuccessObject());

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, inviteOrganisationMapperMock);
        inOrder.verify(inviteOrganisationRepositoryMock).findOne(inviteOrganisation.getId());
        inOrder.verify(inviteOrganisationMapperMock).mapToResource(createInviteOrganisationExpectations(
                inviteOrganisation.getId(),
                applicationInvitesFiltered));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication() throws Exception {
        List<Application> applications = newApplication()
                .build(2);

        List<ApplicationInvite> applicationInvitesUnfiltered = newApplicationInvite()
                .withApplication(applications.get(0), applications.get(0), applications.get(1))
                .build(3);

        List<Organisation> organisations = newOrganisation().build(2);

        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .withOrganisation(null, organisations.get(0), organisations.get(1))
                .withInvites(
                        newApplicationInvite().build(2),
                        newApplicationInvite().build(2),
                        applicationInvitesUnfiltered
                )
                .build(3);

        List<ApplicationInvite> applicationInvitesFiltered = asList(applicationInvitesUnfiltered.get(0), applicationInvitesUnfiltered.get(1));

        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(inviteOrganisationRepositoryMock.findByInvitesApplicationId(applications.get(0).getId())).thenReturn(inviteOrganisations);
        when(inviteOrganisationMapperMock.mapToResource(createInviteOrganisationExpectations(
                inviteOrganisations.get(2).getId(),
                applicationInvitesFiltered
        ))).thenReturn(inviteOrganisationResource);

        ServiceResult<InviteOrganisationResource> result = service.getByOrganisationIdWithInvitesForApplication(
                organisations.get(1).getId(), applications.get(0).getId());
        assertEquals(inviteOrganisationResource, result.getSuccessObject());

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, inviteOrganisationMapperMock);
        inOrder.verify(inviteOrganisationRepositoryMock).findByInvitesApplicationId(applications.get(0).getId());
        inOrder.verify(inviteOrganisationMapperMock).mapToResource(createInviteOrganisationExpectations(
                inviteOrganisations.get(2).getId(),
                applicationInvitesFiltered
        ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getByOrganisationIdWithInvitesForApplication_organisationNotFound() throws Exception {
        long organisationId = Long.MAX_VALUE;
        long applicationId = 1L;

        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .withOrganisation(null, newOrganisation().build())
                .withInvites(
                        newApplicationInvite().build(2),
                        newApplicationInvite().build(2)
                )
                .build(3);

        when(inviteOrganisationRepositoryMock.findByInvitesApplicationId(applicationId)).thenReturn(inviteOrganisations);

        ServiceResult<InviteOrganisationResource> result = service.getByOrganisationIdWithInvitesForApplication(
                organisationId, applicationId);
        assertTrue(result.getFailure().is(notFoundError(InviteOrganisation.class, organisationId, applicationId)));

        InOrder inOrder = inOrder(inviteOrganisationRepositoryMock, inviteOrganisationMapperMock);
        inOrder.verify(inviteOrganisationRepositoryMock).findByInvitesApplicationId(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    private InviteOrganisation createInviteOrganisationExpectations(long inviteOrganisationId, List<ApplicationInvite> applicationInvites) {
        return createLambdaMatcher(inviteOrganisation -> {
            assertEquals(inviteOrganisationId, inviteOrganisation.getId().longValue());
            assertEquals(applicationInvites, inviteOrganisation.getInvites());
        });
    }
}