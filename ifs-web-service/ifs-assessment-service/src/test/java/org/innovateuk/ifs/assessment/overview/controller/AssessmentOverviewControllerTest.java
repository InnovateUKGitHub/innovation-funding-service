package org.innovateuk.ifs.assessment.overview.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.finance.view.OrganisationFinanceOverview;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.assessment.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.model.AssessmentFinancesSummaryModelPopulator;
import org.innovateuk.ifs.assessment.model.AssessmentOverviewModelPopulator;
import org.innovateuk.ifs.assessment.model.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.assessment.overview.form.AssessmentOverviewForm;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentFinancesSummaryModelPopulator;
import org.innovateuk.ifs.assessment.overview.populator.AssessmentOverviewModelPopulator;
import org.innovateuk.ifs.assessment.assignment.populator.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.overview.viewmodel.*;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.common.service.AssessorFormInputResponseService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.ACCEPTED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.OrganisationFinanceOverviewBuilder.newOrganisationFinanceOverviewBuilder;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentOverviewControllerTest extends BaseControllerMockMVCTest<AssessmentOverviewController> {

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseService;

    @Spy
    @InjectMocks
    private AssessmentOverviewModelPopulator assessmentOverviewModelPopulator;

    @Spy
    @InjectMocks
    private AssessmentFinancesSummaryModelPopulator assessmentFinancesSummaryModelPopulator;

    @Spy
    @InjectMocks
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Override
    protected AssessmentOverviewController supplyControllerUnderTest() {
        return new AssessmentOverviewController();
    }

    @Test
    public void getOverview() throws Exception {
        long applicationId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withAssessorAcceptsDate(ZonedDateTime.now().minusDays(2))
                .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(4))
                .build();

        AssessmentResource assessment = newAssessmentResource()
                .withApplication(applicationId)
                .withCompetition(competition.getId())
                .withApplicationName("Using natural gas to heat homes")
                .build();

        QuestionResource questionApplicationDetails = newQuestionResource()
                .withShortName("Application Details")
                .build();

        QuestionResource questionScope = newQuestionResource()
                .withShortName("Scope")
                .build();

        QuestionResource questionBusinessOpportunity = newQuestionResource()
                .withShortName("Business opportunity")
                .withAssessorMaximumScore(10)
                .build();

        QuestionResource questionPotentialMarket = newQuestionResource()
                .withShortName("Potential market")
                .withAssessorMaximumScore(15)
                .build();

        List<QuestionResource> questions = asList(questionApplicationDetails, questionScope, questionBusinessOpportunity, questionPotentialMarket);

        List<SectionResource> sections = newSectionResource()
                .withName("Project details", "Application questions", "Finances")
                .withQuestions(
                        asList(questionApplicationDetails.getId(), questionScope.getId()),
                        asList(questionBusinessOpportunity.getId(), questionPotentialMarket.getId()),
                        emptyList())
                .withAssessorGuidanceDescription("These do not need scoring.",
                        "Each question should be given a score.",
                        "Each partner is required to submit their own finances.")
                .build(3);

        FormInputResource potentialMarketFileEntryFormInput = newFormInputResource()
                .build();

        List<FormInputResource> assessorFormInputsScope = newFormInputResource()
                .withType(TEXTAREA, ASSESSOR_APPLICATION_IN_SCOPE)
                .withQuestion(questionScope.getId())
                .build(2);

        List<FormInputResource> assessorFormInputsBusinessOpportunity = newFormInputResource()
                .withType(TEXTAREA, ASSESSOR_SCORE)
                .withQuestion(questionBusinessOpportunity.getId())
                .build(2);

        List<FormInputResource> assessorFormInputsPotentialMarket = newFormInputResource()
                .withType(TEXTAREA, ASSESSOR_SCORE)
                .withQuestion(questionPotentialMarket.getId())
                .build(2);

        List<FormInputResource> assessorFormInputs = combineLists(assessorFormInputsScope,
                assessorFormInputsBusinessOpportunity, assessorFormInputsPotentialMarket);

        List<FormInputResponseResource> applicantResponses = newFormInputResponseResource()
                .withFormInputs(potentialMarketFileEntryFormInput.getId())
                .withFileEntry(1L)
                .withQuestion(questionPotentialMarket.getId())
                .withFileName("Project-plan.pdf")
                .withFilesizeBytes(112640L)
                .build(1);

        AssessorFormInputResponseResource assessorResponsesScope = newAssessorFormInputResponseResource()
                .withAssessment(assessment.getId())
                .withQuestion(questionScope.getId())
                .withFormInput(assessorFormInputsScope.get(1).getId())
                .withValue("true")
                .build();

        List<AssessorFormInputResponseResource> assessorResponsesBusinessOpportunity = newAssessorFormInputResponseResource()
                .withAssessment(assessment.getId())
                .withQuestion(questionBusinessOpportunity.getId())
                .withFormInput(assessorFormInputsBusinessOpportunity.get(0).getId(),
                        assessorFormInputsBusinessOpportunity.get(1).getId())
                .withValue("Text response", "7")
                .build(2);

        AssessorFormInputResponseResource assessorResponsesPotentialMarket = newAssessorFormInputResponseResource()
                .withAssessment(assessment.getId())
                .withQuestion(questionPotentialMarket.getId())
                .withFormInput(assessorFormInputsPotentialMarket.get(0).getId())
                .withValue("Text response")
                .build();

        List<AssessorFormInputResponseResource> assessorResponses = combineLists(combineLists(assessorResponsesScope,
                assessorResponsesBusinessOpportunity), assessorResponsesPotentialMarket);

        when(assessmentService.getById(assessment.getId())).thenReturn(assessment);
        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(sectionService.getSectionsForCompetitionByType(competition.getId(), SectionType.GENERAL)).thenReturn(sections);
        when(sectionService.filterParentSections(sections)).thenReturn(sections);
        when(questionService.findByCompetition(competition.getId())).thenReturn(questions);
        when(formInputService.findAssessmentInputsByCompetition(competition.getId())).thenReturn(assessorFormInputs);
        when(assessorFormInputResponseService.getAllAssessorFormInputResponses(assessment.getId())).thenReturn(assessorResponses);
        when(formInputResponseService.getByApplication(applicationId)).thenReturn(applicantResponses);

        List<AssessmentOverviewSectionViewModel> expectedSections = asList(
                new AssessmentOverviewSectionViewModel(sections.get(0).getId(),
                        "Project details",
                        "These do not need scoring.",
                        asList(
                                new AssessmentOverviewQuestionViewModel(
                                        questionApplicationDetails.getId(),
                                        questionApplicationDetails.getShortName(),
                                        questionApplicationDetails.getQuestionNumber(),
                                        questionApplicationDetails.getAssessorMaximumScore(),
                                        false,
                                        false,
                                        null,
                                        null),
                                new AssessmentOverviewQuestionViewModel(
                                        questionScope.getId(),
                                        questionScope.getShortName(),
                                        questionScope.getQuestionNumber(),
                                        questionScope.getAssessorMaximumScore(),
                                        true,
                                        false,
                                        TRUE,
                                        null)
                        ),
                        false
                ),
                new AssessmentOverviewSectionViewModel(sections.get(1).getId(),
                        "Application questions",
                        "Each question should be given a score.",
                        asList(
                                new AssessmentOverviewQuestionViewModel(
                                        questionBusinessOpportunity.getId(),
                                        questionBusinessOpportunity.getShortName(),
                                        questionBusinessOpportunity.getQuestionNumber(),
                                        questionBusinessOpportunity.getAssessorMaximumScore(),
                                        true,
                                        true,
                                        null,
                                        "7"),
                                new AssessmentOverviewQuestionViewModel(
                                        questionPotentialMarket.getId(),
                                        questionPotentialMarket.getShortName(),
                                        questionPotentialMarket.getQuestionNumber(),
                                        questionPotentialMarket.getAssessorMaximumScore(),
                                        true,
                                        false,
                                        null,
                                        null)
                        ),
                        false
                ),
                new AssessmentOverviewSectionViewModel((sections.get(2).getId()),
                        "Finances",
                        "Each partner is required to submit their own finances.",
                        emptyList(),
                        true
                )
        );

        List<AssessmentOverviewAppendixViewModel> expectedAppendices = singletonList(
                new AssessmentOverviewAppendixViewModel(
                        potentialMarketFileEntryFormInput.getId(),
                        "Potential market",
                        "Project-plan.pdf",
                        "110 KB")
        );

        AssessmentOverviewViewModel expectedViewModel = new AssessmentOverviewViewModel(
                assessment.getId(),
                applicationId,
                "Using natural gas to heat homes",
                competition.getId(),
                50L,
                3L,
                expectedSections,
                expectedAppendices
        );

        mockMvc.perform(get("/" + assessment.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/application-overview"));

        InOrder inOrder = inOrder(assessmentService, competitionService, sectionService, questionService, formInputService,
                assessorFormInputResponseService, formInputResponseService);
        inOrder.verify(assessmentService).getById(assessment.getId());
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(questionService).findByCompetition(competition.getId());
        inOrder.verify(sectionService).getSectionsForCompetitionByType(competition.getId(), SectionType.GENERAL);
        inOrder.verify(sectionService).filterParentSections(sections);
        inOrder.verify(formInputService).findAssessmentInputsByCompetition(competition.getId());
        inOrder.verify(assessorFormInputResponseService).getAllAssessorFormInputResponses(assessment.getId());
        inOrder.verify(formInputResponseService).getByApplication(applicationId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getFinancesSummary() throws Exception {
        setupCompetition();
        setupApplicationWithRoles();

        ZonedDateTime now = ZonedDateTime.now();

        CompetitionResource competitionResource = newCompetitionResource()
                .withAssessorAcceptsDate(now.minusDays(2))
                .withAssessorDeadlineDate(now.plusDays(4))
                .build();

        ApplicationResource applicationResource = applications.get(0);

        AssessmentResource assessmentResource = newAssessmentResource()
                .withApplication(applicationResource.getId())
                .withApplicationName("Application name")
                .withCompetition(competitionResource.getId())
                .build();

        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);
        when(assessmentService.getById(assessmentResource.getId())).thenReturn(assessmentResource);

        SortedSet<OrganisationResource> orgSet = setupOrganisations();
        List<ApplicationFinanceResource> appFinanceList = setupFinances(applicationResource, orgSet);
        OrganisationFinanceOverview organisationFinanceOverview = newOrganisationFinanceOverviewBuilder()
                .withApplicationId(applicationResource.getId())
                .withOrganisationFinances(appFinanceList)
                .build();

        AssessmentFinancesSummaryViewModel expectedViewModel = new AssessmentFinancesSummaryViewModel(
                assessmentResource.getId(), applicationResource.getId(), "Application name", 3, 50);

        mockMvc.perform(get("/{assessmentId}/finances", assessmentResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("applicationOrganisations", orgSet))
                .andExpect(model().attribute("organisationFinances", organisationFinanceOverview.getFinancesByOrganisation()))
                .andExpect(model().attribute("financeTotal", organisationFinanceOverview.getTotal()))
                .andExpect(view().name("assessment/application-finances-summary"));
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        Long competitionId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard/competition/%s", competitionId)))
                .andReturn();

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verify(assessmentService).rejectInvitation(assessmentId, reason, comment);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_noReason() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(ACCEPTED)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                ACCEPTED
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", "")
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectReason"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectReason"));
        assertEquals("Please enter a reason.", bindingResult.getFieldError("rejectReason").getDefaultMessage());

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = RandomStringUtils.random(5001);

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(ACCEPTED)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                ACCEPTED
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(101, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(ACCEPTED)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                ACCEPTED
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(ACCEPTED)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        // The non-js confirmation view should be returned with the fields pre-populated in the form and a global error

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                ACCEPTED
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/reject-invitation-confirm"))
                .andReturn();

        AssessmentOverviewForm form = (AssessmentOverviewForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals(0, bindingResult.getFieldErrorCount());
        assertEquals(ASSESSMENT_REJECTION_FAILED.name(), bindingResult.getGlobalError().getCode());

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verify(assessmentService).rejectInvitation(assessmentId, reason, comment);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectInvitationConfirm() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(ACCEPTED)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        AssessmentOverviewForm expectedForm = new AssessmentOverviewForm();

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                ACCEPTED
        );

        mockMvc.perform(get("/{assessmentId}/reject/confirm", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/reject-invitation-confirm"));

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void downloadAppendix() throws Exception {
        long assessmentId = 1L;
        long applicationId = 2L;
        long formInputId = 3L;

        ProcessRoleResource assessorRole = newProcessRoleResource().build();
        ByteArrayResource fileContents = new ByteArrayResource("The returned file data".getBytes());
        FileEntryResource fileEntry = newFileEntryResource().withMediaType("text/hello").withFilesizeBytes(1234L).build();
        FormInputResponseFileEntryResource formInputResponseFileEntry =
                new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, assessorRole.getId());

        loginDefaultUser();
        UserResource assessor = getLoggedInUser();

        when(processRoleService.findProcessRole(assessor.getId(), applicationId)).thenReturn(assessorRole);
        when(formInputResponseService.getFile(formInputId,
                applicationId,
                assessorRole.getId()))
                .thenReturn(restSuccess(fileContents));
        when(formInputResponseService.getFileDetails(formInputId, applicationId, assessorRole.getId()))
                .thenReturn(restSuccess(formInputResponseFileEntry));

        mockMvc.perform(get("/{assessmentId}/application/{applicationId}/formInput/{formInputId}/download",
                    assessmentId,
                    applicationId,
                    formInputId))
                .andExpect(status().isOk())
                .andExpect(content().string("The returned file data"))
                .andExpect(header().string("Content-Type", "text/hello"))
                .andExpect(header().longValue("Content-Length", "The returned file data".length()));

        verify(processRoleService).findProcessRole(assessor.getId(), applicationId);
        verify(formInputResponseService).getFile(formInputId, applicationId, assessorRole.getId());
        verify(formInputResponseService).getFileDetails(formInputId, applicationId, assessorRole.getId());
    }

    private List<ApplicationFinanceResource> setupFinances(ApplicationResource app, SortedSet<OrganisationResource> orgSet) {
        List<OrganisationResource> orgList = orgSet.stream().collect(Collectors.toList());
        List<ApplicationFinanceResource> appFinanceList = new ArrayList<>();
        appFinanceList.add(new ApplicationFinanceResource(1L, orgList.get(0).getId(), app.getId(), 2L));
        appFinanceList.add(new ApplicationFinanceResource(2L, orgList.get(1).getId(), app.getId(), 2L));

        when(financeService.getApplicationFinanceTotals(app.getId())).thenReturn(appFinanceList);

        when(applicationFinanceRestService.getResearchParticipationPercentage(anyLong())).thenReturn(restSuccess(0.0));
        when(financeHandler.getFinanceFormHandler(1L)).thenReturn(defaultFinanceFormHandler);
        when(financeHandler.getFinanceModelManager(1L)).thenReturn(defaultFinanceModelManager);

        return appFinanceList;
    }

    private SortedSet<OrganisationResource> setupOrganisations() {
        OrganisationResource org1 = newOrganisationResource().withId(1L).withName("Empire Ltd").build();
        OrganisationResource org2 = newOrganisationResource().withId(2L).withName("Ludlow").build();
        Comparator<OrganisationResource> compareById = Comparator.comparingLong(OrganisationResource::getId);
        SortedSet<OrganisationResource> orgSet = new TreeSet<>(compareById);
        orgSet.add(org1);
        orgSet.add(org2);

        return orgSet;
    }
}
