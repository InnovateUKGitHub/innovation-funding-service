package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.builder.FormInputResourceBuilder;
import org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder;
import org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.builder.OrganisationResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationSectionAndQuestionModelPopulatorTest {

    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator target;

    @Mock
    protected FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    protected FormInputResponseRestService formInputResponseRestService;

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

    @Mock
    private CategoryRestService categoryRestService;

    @Test
    public void testAddMappedSectionsDetails() {
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource().build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        Long organisationId = 3L;
        List<SectionResource> allSections = newSectionResource().build(3);
        SectionResource parentSection = newSectionResource()
                .withChildSections(simpleMap(allSections, SectionResource::getId)).build();

        OrganisationResource organisationResource = OrganisationResourceBuilder.newOrganisationResource()
                .withId(organisationId).build();
        Optional<OrganisationResource> userOrganisation = Optional.of(organisationResource);
        Optional<SectionResource> section = Optional.of(parentSection);
        Model model = mock(Model.class);
        when(sectionService.getAllByCompetitionId(competition.getId())).thenReturn(allSections);
        when(sectionService.filterParentSections(allSections)).thenReturn(asList(parentSection));
        when(formInputRestService.getByCompetitionIdAndScope(competition.getId(), APPLICATION)).thenReturn(restSuccess(
                newFormInputResource().build(1)));

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
                .withApplicationStatus(ApplicationStatus.OPEN).build();
        Long userId = 1L;
        Long organisationId = 3L;
        Optional<SectionResource> section = Optional.of(newSectionResource().build());
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

        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(responses));
        when(formInputResponseService.mapFormInputResponsesToFormInput(responses)).thenReturn(mappedResponses);

        target.addQuestionsDetails(model, application, form);

        //verify model attributes
        verify(model).addAttribute("responses", mappedResponses);
        verify(model).addAttribute(ApplicationSectionAndQuestionModelPopulator.MODEL_ATTRIBUTE_FORM, form);
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
        SectionResource financeSection = newSectionResource().build();
        List<SectionResource> eachOrganisationFinanceSections = newSectionResource().build(1);
        Set<Long> sectionsMarkedAsComplete = new TreeSet<>();
        sectionsMarkedAsComplete.add(completedSectionId);
        List<ResearchCategoryResource> categoryResources = ResearchCategoryResourceBuilder.newResearchCategoryResource().build(3);

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisation);
        when(questionService.getMarkedAsComplete(application.getId(), organisationId)).thenReturn(markedAsComplete);
        when(sectionService.allSectionsMarkedAsComplete(application.getId())).thenReturn(allQuestionsCompleted);
        when(sectionService.getFinanceSection(application.getCompetition())).thenReturn(financeSection);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(eachOrganisationFinanceSections);
        when(categoryRestService.getResearchCategories()).thenReturn(restSuccess(categoryResources));

        target.addCompletedDetails(model, application, Optional.of(userOrganisation));

        verify(model).addAttribute("markedAsComplete", markedAsComplete);
        verify(model).addAttribute("completedSectionsByOrganisation", completedSectionsByOrganisation);
        verify(model).addAttribute("sectionsMarkedAsComplete", sectionsMarkedAsComplete);
        verify(model).addAttribute("allQuestionsCompleted", allQuestionsCompleted);
        verify(model).addAttribute("hasFinanceSection", true);
        verify(model).addAttribute("financeSectionId", financeSection.getId());
        verify(model).addAttribute("eachCollaboratorFinanceSectionId", eachOrganisationFinanceSections.get(0).getId());
        verify(model).addAttribute("researchCategories", categoryResources);

        verifyNoMoreInteractions(model);
    }

    @Test
    public void testAddSectionDetails() {
        Model model = mock(Model.class);
        long competitionId = 1L;
        List<QuestionResource> sectionQuestions = QuestionResourceBuilder.newQuestionResource().build(1);
        SectionResource currentSection = newSectionResource()
                .withCompetition(competitionId)
                .withQuestions(simpleMap(sectionQuestions, QuestionResource::getId)).build();
        List<FormInputResource> responses = newFormInputResource().build(1);
        Map<Long, List<FormInputResource>> questionFormInputs = simpleToMap(sectionQuestions, QuestionResource::getId, question -> responses);
        Map<Long, List<QuestionResource>> currentSectionQuestions = new HashMap<>();
        currentSectionQuestions.put(currentSection.getId(), sectionQuestions);

        when(questionService.findByCompetition(competitionId)).thenReturn(sectionQuestions);
        when(formInputRestService.getByQuestionIdAndScope(sectionQuestions.get(0).getId(), APPLICATION)).thenReturn(restSuccess(responses));

        target.addSectionDetails(model, Optional.of(currentSection));

        verify(model).addAttribute("currentSectionId", currentSection.getId());
        verify(model).addAttribute("currentSection", currentSection);
        verify(model).addAttribute("questionFormInputs", questionFormInputs);
        verify(model).addAttribute("sectionQuestions", currentSectionQuestions);
        verify(model).addAttribute("title", currentSection.getName());
        verifyNoMoreInteractions(model);
    }
}
