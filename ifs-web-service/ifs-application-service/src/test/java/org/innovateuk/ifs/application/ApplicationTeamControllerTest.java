package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationTeamModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamApplicantRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamOrganisationRowViewModel;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationTeamControllerTest extends BaseControllerMockMVCTest<ApplicationTeamController> {

    @Spy
    @InjectMocks
    private ApplicationTeamModelPopulator applicationTeamModelPopulator;

    @Override
    protected ApplicationTeamController supplyControllerUnderTest() {
        return new ApplicationTeamController();
    }

    @Test
    public void testGetApplicationTeam() throws Exception {
        ApplicationResource applicationResource = newApplicationResource()
                .withName("Application name")
                .build();

        List<ApplicationInviteResource> applicationInviteResourcesOrg1 = newApplicationInviteResource()
                .withNameConfirmed("Jessica Doe", null)
                .withName("Jess Doe", "Ryan Dell")
                .withEmail("jessica.doe@ludlow.com", "ryan.dell@ludlow.com")
                .withStatus(OPENED, InviteStatus.SENT)
                .build(2);

        List<ApplicationInviteResource> applicationInviteResourcesOrg2 = newApplicationInviteResource()
                .withNameConfirmed("Paul Tom")
                .withName("Paul Tom")
                .withEmail("paul.tom@egg.com")
                .withStatus(OPENED)
                .build(1);

        List<ApplicationInviteResource> applicationInviteResourcesOrg3 = newApplicationInviteResource()
                .withNameConfirmed("Paul Davidson")
                .withName("Paul Davidson")
                .withEmail("paul.davidson@empire.com")
                .withStatus(OPENED)
                .build(1);

        List<OrganisationResource> organisations = newOrganisationResource()
                .withName("Ludlow", "EGGS", "Empire Ltd")
                .build(3);

        Map<String, OrganisationResource> organisationsMap = simpleToMap(organisations, OrganisationResource::getName);

        List<InviteOrganisationResource> inviteOrganisationResources = newInviteOrganisationResource()
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId())
                .withOrganisationName(organisations.get(0).getName(), organisations.get(1).getName(), organisations.get(2).getName())
                .withOrganisationNameConfirmed(organisations.get(0).getName(), organisations.get(1).getName(), organisations.get(2).getName())
                .withInviteResources(applicationInviteResourcesOrg1, applicationInviteResourcesOrg2, applicationInviteResourcesOrg3)
                .build(3);

        UserResource leadApplicant = newUserResource()
                .withFirstName("Steve")
                .withLastName("Smith")
                .withEmail("steve.smith@empire.com")
                .build();

        ProcessRoleResource leadApplicantProcessRole = newProcessRoleResource()
                .withUser(leadApplicant)
                .build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        when(applicationService.getLeadOrganisation(applicationResource.getId())).thenReturn(organisationsMap.get("Empire Ltd"));
        when(inviteRestService.getInvitesByApplication(applicationResource.getId())).thenReturn(restSuccess(inviteOrganisationResources));
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(leadApplicantProcessRole);
        when(userService.findById(leadApplicant.getId())).thenReturn(leadApplicant);

        List<ApplicationTeamOrganisationRowViewModel> expectedOrganisations = asList(
                new ApplicationTeamOrganisationRowViewModel(organisationsMap.get("Empire Ltd").getId(), "Empire Ltd", true, asList(
                        new ApplicationTeamApplicantRowViewModel("Steve Smith", "steve.smith@empire.com", true, false),
                        new ApplicationTeamApplicantRowViewModel("Paul Davidson", "paul.davidson@empire.com", false, false)
                )),
                new ApplicationTeamOrganisationRowViewModel(organisationsMap.get("Ludlow").getId(), "Ludlow", false, asList(
                        new ApplicationTeamApplicantRowViewModel("Jessica Doe", "jessica.doe@ludlow.com", false, false),
                        new ApplicationTeamApplicantRowViewModel("Ryan Dell", "ryan.dell@ludlow.com", false, true)
                )),
                new ApplicationTeamOrganisationRowViewModel(organisationsMap.get("EGGS").getId(), "EGGS", false, singletonList(
                        new ApplicationTeamApplicantRowViewModel("Paul Tom", "paul.tom@egg.com", false, false)
                ))
        );

        ApplicationTeamViewModel expectedViewModel = new ApplicationTeamViewModel(
                applicationResource.getId(),
                "Application name",
                expectedOrganisations
        );

        mockMvc.perform(get("/application/{applicationId}/team", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/team"));

        InOrder inOrder = inOrder(applicationService, inviteRestService, userService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verify(applicationService).getLeadOrganisation(applicationResource.getId());
        inOrder.verify(inviteRestService).getInvitesByApplication(applicationResource.getId());
        inOrder.verify(userService).getLeadApplicantProcessRoleOrNull(applicationResource);
        inOrder.verify(userService).findById(leadApplicant.getId());
        inOrder.verifyNoMoreInteractions();
    }
}