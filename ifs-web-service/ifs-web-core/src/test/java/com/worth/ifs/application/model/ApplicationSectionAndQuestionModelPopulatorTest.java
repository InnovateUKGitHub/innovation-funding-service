package com.worth.ifs.application.model;

import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.builder.QuestionResourceBuilder;
import com.worth.ifs.application.builder.QuestionStatusResourceBuilder;
import com.worth.ifs.application.builder.SectionResourceBuilder;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.*;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.builder.FormInputResourceBuilder;
import com.worth.ifs.form.builder.FormInputResponseResourceBuilder;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder;
import com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.builder.OrganisationResourceBuilder;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.worth.ifs.application.AbstractApplicationController.FORM_MODEL_ATTRIBUTE;
import static com.worth.ifs.util.CollectionFunctions.*;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSectionAndQuestionModelPopulatorTest {

    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator target;

    @Mock
    protected FormInputService formInputService;

    @Mock
    protected FormInputResponseService formInputResponseService;

    @Mock
    protected QuestionService questionService;

    @Mock
    protected ProcessRoleService processRoleService;

    @Mock
    protected InviteRestService inviteRestService;

    @Mock
    protected SectionService sectionService;

    @Mock
    protected OrganisationService organisationService;

    @Test
    public void testAddMappedSectionsDetails() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        Long userId = 1L;
        Long organisationId = 3L;
        List<SectionResource> allSections = SectionResourceBuilder.newSectionResource().build(3);
        SectionResource parentSection = SectionResourceBuilder.newSectionResource()
                .withChildSections(simpleMap(allSections, SectionResource::getId)).build();

        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId).build();
        Optional<OrganisationResource> userOrganisation = Optional.of(organisationResource);
        Optional<SectionResource> section = Optional.of(parentSection);
        Model model = mock(Model.class);
        when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(allSections);
        when(sectionService.filterParentSections(allSections)).thenReturn(asList(parentSection));

        allSections.forEach(loopSection -> when(sectionService.getById(loopSection.getId())).thenReturn(loopSection));

        target.addMappedSectionsDetails(model, application, competition, section, userOrganisation);

        verify(model).addAttribute(eq("completedSections"), anyMap());
        verify(model).addAttribute(eq("sections"), anyMap());
        verify(model).addAttribute(eq("questionFormInputs"), anyMap());
        verify(model).addAttribute(eq("sectionQuestions"), anyMap());
        verify(model).addAttribute(eq("subSections"), anyMap());
        verify(model).addAttribute(eq("subsectionQuestions"), anyMap());
        verify(model).addAttribute(eq("subSectionQuestionFormInputs"), anyMap());
        verifyNoMoreInteractions(model);
    }

    @Test
    public void testAddAssignableDetails() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
                .withApplicationStatus(ApplicationStatusConstants.OPEN).build();
        Long userId = 1L;
        Long organisationId = 3L;
        Optional<SectionResource> section = Optional.of(SectionResourceBuilder.newSectionResource().build());
        Long questionId = 2L;
        Optional<Long> currentQuestionId = Optional.of(questionId);
        OrganisationResource userOrganisation = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId).build();
        Model model = mock(Model.class);
        QuestionStatusResource questionAssignee = QuestionStatusResourceBuilder.newQuestionStatusResource().build();
        Future<List<ProcessRoleResource>> assignableUsers = mock(Future.class);
        List<QuestionStatusResource> notifications = QuestionStatusResourceBuilder.newQuestionStatusResource().build(3);
        List<InviteOrganisationResource> invites = InviteOrganisationResourceBuilder.newInviteOrganisationResource()
                .withInviteResources(ApplicationInviteResourceBuilder.newApplicationInviteResource().build(1)).build(2);

        when(questionService.getByQuestionIdAndApplicationIdAndOrganisationId(questionId, application.getId(), userOrganisation.getId()))
                .thenReturn(questionAssignee);
        when(processRoleService.findAssignableProcessRoles(application.getId())).thenReturn(assignableUsers);
        when(questionService.getNotificationsForUser(anyList(), eq(userId))).thenReturn(notifications);
        when(inviteRestService.getInvitesByApplication(application.getId())).thenReturn(restSuccess(invites));

        target.addAssignableDetails(model, application, userOrganisation, userId, section, currentQuestionId);

        //Verify model attributes
        verify(model).addAttribute("questionAssignee", questionAssignee);
        verify(model).addAttribute("assignableUsers", assignableUsers);
        verify(model).addAttribute(eq("pendingAssignableUsers"), anyList());
        verify(model).addAttribute(eq("questionAssignees"), anyMap());
        verify(model).addAttribute("notifications", notifications);
        verifyNoMoreInteractions(model);

        verify(questionService).removeNotifications(notifications);
    }

    @Test
    public void testAddQuestionsDetails() {
        Model model = mock(Model.class);
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        Form form = new ApplicationForm();
        List<FormInputResponseResource> responses = FormInputResponseResourceBuilder.newFormInputResponseResource().build(2);
        Map<Long, FormInputResponseResource> mappedResponses = simpleToMap(responses, FormInputResponseResource::getId, Function.identity());

        when(formInputResponseService.getByApplication(application.getId())).thenReturn(responses);
        when(formInputResponseService.mapFormInputResponsesToFormInput(responses)).thenReturn(mappedResponses);

        target.addQuestionsDetails(model, application, form);

        //verify model attributes
        verify(model).addAttribute("responses", mappedResponses);
        verify(model).addAttribute(FORM_MODEL_ATTRIBUTE, form);
        verifyNoMoreInteractions(model);

        //verify form
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((id, response) -> {
            assertThat(values.get(String.valueOf(id)), equalTo(response.getValue()));
        });
    }

    @Test
    public void testAddCompletedDetails() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        Long organisationId = 3L;
        OrganisationResource userOrganisation = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId).build();
        Model model = mock(Model.class);
        Future<Set<Long>> markedAsComplete = mock(Future.class);
        Map<Long, Set<Long>> completedSectionsByOrganisation = new HashMap<>();
        Long completedSectionId = 1L;
        completedSectionsByOrganisation.put(organisationId, asLinkedSet(completedSectionId));
        Boolean allQuestionsCompleted = true;
        SectionResource financeSection = SectionResourceBuilder.newSectionResource().build();
        List<SectionResource> eachOrganisationFinanceSections = SectionResourceBuilder.newSectionResource().build(1);
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>();
        sectionsMarkedAsComplete.add(completedSectionId);

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisation);
        when(questionService.getMarkedAsComplete(application.getId(), organisationId)).thenReturn(markedAsComplete);
        when(sectionService.allSectionsMarkedAsComplete(application.getId())).thenReturn(allQuestionsCompleted);
        when(sectionService.getFinanceSection(application.getCompetition())).thenReturn(financeSection);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.ORGANISATION_FINANCES)).thenReturn(eachOrganisationFinanceSections);

        target.addCompletedDetails(model, application, Optional.of(userOrganisation));

        verify(model).addAttribute("markedAsComplete", markedAsComplete);
        verify(model).addAttribute("completedSectionsByOrganisation", completedSectionsByOrganisation);
        verify(model).addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        verify(model).addAttribute("allQuestionsCompleted", allQuestionsCompleted);
        verify(model).addAttribute("hasFinanceSection", true);
        verify(model).addAttribute("financeSectionId", financeSection.getId());
        verify(model).addAttribute("eachCollaboratorFinanceSectionId", eachOrganisationFinanceSections.get(0).getId());
        verifyNoMoreInteractions(model);
    }

    @Test
    public void testAddSectionDetails() {
        Model model = mock(Model.class);
        long competitionId = 1L;
        List<QuestionResource> sectionQuestions = QuestionResourceBuilder.newQuestionResource().build(1);
        SectionResource currentSection = SectionResourceBuilder.newSectionResource()
                .withCompetition(competitionId)
                .withQuestions(simpleMap(sectionQuestions, QuestionResource::getId)).build();
        List<FormInputResource> responses = FormInputResourceBuilder.newFormInputResource().build(1);
        Map<Long, List<FormInputResource>> questionFormInputs = simpleToMap(sectionQuestions, QuestionResource::getId, question -> responses);
        Map<Long, List<QuestionResource>> currentSectionQuestions = new HashMap<>();
        currentSectionQuestions.put(currentSection.getId(), sectionQuestions);

        when(questionService.findByCompetition(competitionId)).thenReturn(sectionQuestions);
        when(formInputService.findApplicationInputsByQuestion(sectionQuestions.get(0).getId())).thenReturn(responses);

        target.addSectionDetails(model, Optional.of(currentSection));

        verify(model).addAttribute("currentSectionId", currentSection.getId());
        verify(model).addAttribute("currentSection", currentSection);
        verify(model).addAttribute("questionFormInputs", questionFormInputs);
        verify(model).addAttribute("sectionQuestions", currentSectionQuestions);
        verify(model).addAttribute("title", currentSection.getName());
        verifyNoMoreInteractions(model);
    }
}
