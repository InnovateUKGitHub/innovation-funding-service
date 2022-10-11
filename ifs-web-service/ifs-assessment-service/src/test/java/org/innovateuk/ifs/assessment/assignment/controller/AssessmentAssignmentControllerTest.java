package org.innovateuk.ifs.assessment.assignment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.assignment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.assignment.populator.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.String.format;
import static java.util.Collections.nCopies;
import static java.util.Comparator.comparingLong;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.PENDING;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentAssignmentControllerTest extends BaseControllerMockMVCTest<AssessmentAssignmentController> {

    private static final long ASSESSMENT_ID = 1L;
    private static final long APPLICATION_ID = 2L;
    private static final long COMPETITION_ID = 3L;

    private SortedSet<OrganisationResource> partners;
    private List<ProcessRoleResource> processRoleResources;

    private OrganisationResource leadOrganisation;

    private OrganisationResource collaboratorOrganisation1;
    private OrganisationResource collaboratorOrganisation2;

    @Spy
    @InjectMocks
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Mock
    private AssessmentService assessmentService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private OrganisationService organisationService;

    @Override
    protected AssessmentAssignmentController supplyControllerUnderTest() {
        return new AssessmentAssignmentController();
    }

    @Before
    public void setup() {

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(newCompetitionResource().build()));

        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(newApplicationResource().build()));

        when(formInputResponseRestService.getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY))
                .thenReturn(restSuccess(newFormInputResponseResource()
                        .withValue("Project summary")
                        .build()));

        collaboratorOrganisation1 = newOrganisationResource().build();
        collaboratorOrganisation2 = newOrganisationResource().build();
        leadOrganisation = newOrganisationResource().build();

        OrganisationResource otherOrganisation = newOrganisationResource().build();

        processRoleResources = newProcessRoleResource()
                .withOrganisation(collaboratorOrganisation1.getId(),
                        leadOrganisation.getId(),
                        collaboratorOrganisation2.getId(),
                        otherOrganisation.getId())
                .withRole(COLLABORATOR, LEADAPPLICANT, COLLABORATOR, ProcessRoleType.ASSESSOR)
                .build(4);

        partners = new TreeSet<>(comparingLong(OrganisationResource::getId));
        partners.add(collaboratorOrganisation1);
        partners.add(leadOrganisation);
        partners.add(collaboratorOrganisation2);

        when(processRoleRestService.findProcessRole(APPLICATION_ID)).thenReturn(restSuccess(processRoleResources));
        when(organisationService.getApplicationOrganisations(processRoleResources)).thenReturn(partners);
        when(organisationService.getApplicationLeadOrganisation(processRoleResources)).thenReturn(Optional.ofNullable(leadOrganisation));

    }

    @Test
    public void viewAssignment() throws Exception {
        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(newAssessmentResource()
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withCompetition(COMPETITION_ID)
                .build());

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        mockMvc.perform(get("/{assessmentId}/assignment", ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/assessment-invitation")).andReturn();

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssignment() throws Exception {
        Boolean accept = true;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withCompetition(COMPETITION_ID)
                .withApplication(APPLICATION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);
        when(assessmentService.acceptInvitation(ASSESSMENT_ID)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/3"));

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(assessmentService).acceptInvitation(ASSESSMENT_ID);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment() throws Exception {
        Boolean accept = false;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withCompetition(COMPETITION_ID)
                .withApplication(APPLICATION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(ASSESSMENT_ID, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard/competition/%s", COMPETITION_ID)))
                .andReturn();

        verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        verify(assessmentService).rejectInvitation(ASSESSMENT_ID, reason, comment);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void rejectAssignment_eventNotAccepted() throws Exception {
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(COMPETITION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(ASSESSMENT_ID, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form"))
                .andExpect(view().name("assessment/assessment-invitation"))
                .andReturn();

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals(0, bindingResult.getFieldErrorCount());
        assertEquals(ASSESSMENT_REJECTION_FAILED.name(), bindingResult.getGlobalError().getCode());

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(assessmentService).rejectInvitation(ASSESSMENT_ID, reason, comment);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_noReason() throws Exception {
        Boolean accept = false;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(COMPETITION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", "")
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectReasonValid"))
                .andExpect(view().name("assessment/assessment-invitation"))
                .andReturn();

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectReasonValid"));
        assertEquals("Please enter a reason.", bindingResult.getFieldError("rejectReasonValid").getDefaultMessage());

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_neitherAcceptOrReject() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(COMPETITION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", ""))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "assessmentAccept"))
                .andExpect(view().name("assessment/assessment-invitation"))
                .andReturn();

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("assessmentAccept"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("assessmentAccept").getDefaultMessage());

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_exceedsCharacterSizeLimit() throws Exception {
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(COMPETITION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/assessment-invitation"))
                .andReturn();

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_exceedsWordLimit() throws Exception {
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(101, "comment"));
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(ASSESSMENT_ID))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(COMPETITION_ID)
                .build();

        when(assessmentService.getAssignableById(ASSESSMENT_ID)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(ASSESSMENT_ID,
                COMPETITION_ID,
                "Application name",
                false, null,
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", ASSESSMENT_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(view().name("assessment/assessment-invitation"))
                .andReturn();

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(ASSESSMENT_ID);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }
}
