package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.terms.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.terms.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTermsControllerTest extends BaseControllerMockMVCTest<ApplicationTermsController> {

    @Mock
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private CompetitionRestService competitionRestServiceMock;
    @Mock
    private SectionService sectionServiceMock;
    @Mock
    private UserRestService userRestServiceMock;
    @Mock
    private ApplicationTermsModelPopulator applicationTermsModelPopulatorMock;

    @Override
    protected ApplicationTermsController supplyControllerUnderTest() {
        return new ApplicationTermsController(
                applicationRestServiceMock,
                competitionRestServiceMock,
                sectionServiceMock,
                userRestServiceMock,
                applicationTermsModelPopulatorMock);
    }

    @Test
    public void getTerms() throws Exception {
        long applicationId = 3L;
        String competitionTermsTemplate = "terms-template";
        boolean collaborativeApplication = false;
        boolean termsAccepted = false;
        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();
        ZonedDateTime termsAcceptedOn = now();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(applicationId, competitionTermsTemplate,
                collaborativeApplication, termsAccepted, loggedInUser.getName(), termsAcceptedOn);

        when(applicationTermsModelPopulatorMock.populate(loggedInUser, applicationId)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/terms-and-conditions", applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/terms-and-conditions"));

        verify(applicationTermsModelPopulatorMock, only()).populate(loggedInUser, applicationId);
    }

    @Test
    public void acceptTerms() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .build();

        SectionResource termsAndConditionsSection = newSectionResource().build();

        ProcessRoleResource processRole = newProcessRoleResource()
                .withUser(getLoggedInUser())
                .withApplication(application.getId())
                .build();

        /*
                ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        SectionResource termsAndConditionsSection = sectionService.getTermsAndConditionsSection(competition.getId());
        ProcessRoleResource processRole = userRestService.findProcessRole(user.getId(), applicationId).getSuccess();
        sectionService.markAsComplete(termsAndConditionsSection.getId(), applicationId, processRole.getId());

         */

        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionServiceMock.getTermsAndConditionsSection(competition.getId())).thenReturn(termsAndConditionsSection);
        when(userRestServiceMock.findProcessRole(processRole.getUser(), processRole.getApplicationId())).thenReturn(restSuccess(processRole));

        mockMvc.perform(post("/application/{applicationId}/terms-and-conditions/accept", application.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/application/{applicationId}/terms-and-conditions", application.getId()));

        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock, sectionServiceMock, userRestServiceMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
        inOrder.verify(sectionServiceMock).getTermsAndConditionsSection(competition.getId());
        inOrder.verify(userRestServiceMock).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(sectionServiceMock).markAsComplete(termsAndConditionsSection.getId(), application.getId(), processRole.getId());
        inOrder.verifyNoMoreInteractions();


    }

    // test migrated
    // current user vs another

//    @Test
//    public void getTerms_collaborative() throws Exception {
//        testGetTerms(true);
//    }



//    private void testGetTerms(boolean collaborative) throws Exception {
//        String termsTemplate = "terms-template";
//
//        GrantTermsAndConditionsResource grantTermsAndConditions =
//                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
//        CompetitionResource competition = newCompetitionResource()
//                .withTermsAndConditions(grantTermsAndConditions)
//                .build();
//        ApplicationResource application = newApplicationResource()
//                .withCompetition(competition.getId())
//                .withCollaborativeProject(collaborative)
//                .build();
//
//        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
//        when(competitionRestServiceMock.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
//
//        mockMvc.perform(get("/application/{applicationId}/terms-and-conditions", application.getId()))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("applicationId", application.getId()))
//                .andExpect(model().attribute("template", grantTermsAndConditions.getTemplate()))
//                .andExpect(model().attribute("collaborative", collaborative))
//                .andExpect(view().name("application/terms-and-conditions"));
//
//        InOrder inOrder = inOrder(applicationRestServiceMock, competitionRestServiceMock);
//        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
//        inOrder.verify(competitionRestServiceMock).getCompetitionById(competition.getId());
//        inOrder.verifyNoMoreInteractions();
//    }
}