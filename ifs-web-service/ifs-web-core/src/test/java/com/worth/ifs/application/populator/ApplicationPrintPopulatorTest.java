package com.worth.ifs.application.populator;

import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.builder.SectionResourceBuilder;
import com.worth.ifs.application.finance.view.FinanceHandler;
import com.worth.ifs.application.finance.view.FinanceModelManager;
import com.worth.ifs.application.finance.view.FinanceOverviewModelManager;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.*;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.builder.FormInputResourceBuilder;
import com.worth.ifs.form.builder.FormInputResponseResourceBuilder;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.model.OrganisationDetailsModelPopulator;
import com.worth.ifs.user.builder.OrganisationResourceBuilder;
import com.worth.ifs.user.builder.ProcessRoleResourceBuilder;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.application.AbstractApplicationController.FORM_MODEL_ATTRIBUTE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
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
    private FinanceOverviewModelManager financeOverviewModelManager;

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
        when(formInputResponseService.getByApplication(applicationId)).thenReturn(responses);
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
        verify(financeOverviewModelManager).addFinanceDetails(model, competition.getId(), applicationId);




    }


}
