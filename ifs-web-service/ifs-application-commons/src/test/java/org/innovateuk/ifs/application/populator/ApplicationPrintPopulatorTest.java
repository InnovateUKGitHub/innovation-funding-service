package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationPrintPopulatorTest {

    @InjectMocks
    private ApplicationPrintPopulator target;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private SectionService sectionService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private QuestionRestService questionRestService;
    @Mock
    private FormInputResponseService formInputResponseService;
    @Mock
    private FormInputResponseRestService formInputResponseRestService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private InviteService inviteService;
    @Mock
    private FormInputRestService formInputRestService;
    @Mock
    private ApplicantRestService applicantRestService;
    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;
    @Mock
    private UserService userService;
    @Mock
    private FinanceService financeService;
    @Mock
    private FileEntryRestService fileEntryRestService;
    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Test
    public void testPrint() {
        Model model = mock(Model.class);
        UserResource user = UserResourceBuilder.newUserResource().build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(competition.getId()).build();
        Optional<OrganisationResource> userOrganisation = Optional.of(newOrganisationResource()
                .withOrganisationType(RESEARCH.getId())
                .build());

        List<FormInputResponseResource> responses = FormInputResponseResourceBuilder.newFormInputResponseResource().build(2);
        List<ProcessRoleResource> userApplicationRoles = ProcessRoleResourceBuilder.newProcessRoleResource()
            .withUser(user)
            .withRole(Role.LEADAPPLICANT)
            .withOrganisation(userOrganisation.get().getId()).build(1);
        Map<Long, FormInputResponseResource> mappedResponses = mock(Map.class);
        Optional<Boolean> markAsCompleteEnabled = Optional.empty();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(responses));
        when(questionRestService.findByCompetition(competition.getId())).thenReturn(restSuccess(emptyList()));
        when(formInputRestService.getByCompetitionIdAndScope(
                competition.getId(), APPLICATION)).thenReturn(restSuccess(emptyList()));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(),
                QuestionSetupType.RESEARCH_CATEGORY)).thenReturn(restSuccess(QuestionResourceBuilder
                .newQuestionResource().build()));
        when(applicationFinanceRestService.getResearchParticipationPercentage(application.getId())).thenReturn(restSuccess(1D));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(userApplicationRoles));
        when(formInputResponseService.mapFormInputResponsesToFormInput(responses)).thenReturn(mappedResponses);
        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(emptyMap());
        when(organisationRestService.getOrganisationById(userOrganisation.get().getId())).thenReturn(restSuccess(userOrganisation.get()));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(singletonList(userOrganisation.get())));


        target.print(application.getId(), model, user);

        //Verify model attributes set
        verify(model, atLeast(1)).addAttribute("responses", mappedResponses);
        verify(model).addAttribute("currentApplication", application);
        verify(model).addAttribute("currentCompetition", competition);
        verify(model).addAttribute("researchCategoryRequired", true);
        verify(model).addAttribute("userOrganisation", userOrganisation.orElse(null));

    }
}
