package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.user.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationPrintPopulatorTest {

    @InjectMocks
    private ApplicationPrintPopulator target;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;;

    @Test
    public void testPrint() {
        Long applicationId = 1L;
        Model model = mock(Model.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserResource user = UserResourceBuilder.newUserResource().build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competition.getId()).build();
        List<FormInputResponseResource> responses = FormInputResponseResourceBuilder.newFormInputResponseResource().build(2);
        List<ProcessRoleResource> userApplicationRoles = ProcessRoleResourceBuilder.newProcessRoleResource().build(1);
        Optional<OrganisationResource> userOrganisation = Optional.of(OrganisationResourceBuilder.newOrganisationResource().build());
        Map<Long, FormInputResponseResource> mappedResponses = mock(Map.class);


        when(userAuthenticationService.getAuthenticatedUser(request)).thenReturn(user);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(formInputResponseRestService.getResponsesByApplicationId(applicationId)).thenReturn(restSuccess(responses));
        when(processRoleService.findProcessRolesByApplicationId(application.getId())).thenReturn(userApplicationRoles);
        when(applicationModelPopulator.getUserOrganisation(user.getId(), userApplicationRoles)).thenReturn(userOrganisation);
        when(formInputResponseService.mapFormInputResponsesToFormInput(responses)).thenReturn(mappedResponses);

        target.print(applicationId, model, request);

        //Verify model attributes set
        verify(model).addAttribute("responses", mappedResponses);
        verify(model).addAttribute("currentApplication", application);
        verify(model).addAttribute("currentCompetition", competition);
        verify(model).addAttribute("userOrganisation", userOrganisation.orElse(null));

        //verify populators called
        verify(organisationDetailsModelPopulator).populateModel(model, application.getId(), userApplicationRoles);
        verify(applicationSectionAndQuestionModelPopulator).addQuestionsDetails(model, application, null);
        verify(applicationModelPopulator).addUserDetails(model, application, user.getId());
        verify(applicationModelPopulator).addApplicationInputs(application, model);
        verify(applicationSectionAndQuestionModelPopulator).addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation);
        verify(applicationFinanceOverviewModelManager).addFinanceDetails(model, competition.getId(), applicationId, userOrganisation.map(OrganisationResource::getId));

    }


}
