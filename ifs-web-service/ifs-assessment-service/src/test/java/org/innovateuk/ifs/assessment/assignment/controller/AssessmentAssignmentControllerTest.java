package org.innovateuk.ifs.assessment.assignment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.assignment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.assignment.populator.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
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
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentAssignmentControllerTest extends BaseControllerMockMVCTest<AssessmentAssignmentController> {

    private static final long APPLICATION_ID = 2L;

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
        super.setup();

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
        long assessmentId = 1L;
        long competitionId = 3L;

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(newAssessmentResource()
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withCompetition(competitionId)
                .build());

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        mockMvc.perform(get("/{assessmentId}/assignment", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/assessment-invitation")).andReturn();

        InOrder inOrder = inOrder(assessmentService, formInputResponseRestService, processRoleRestService, organisationService);
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssignment() throws Exception {
        Long assessmentId = 1L;
        Long competitionId = 2L;
        Boolean accept = true;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.acceptInvitation(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard/competition/2"));

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(assessmentService).acceptInvitation(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment() throws Exception {
        Long assessmentId = 1L;
        Long competitionId = 2L;
        Boolean accept = false;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("assessmentAccept", accept.toString())
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard/competition/%s", competitionId)))
                .andReturn();

        verify(assessmentService).getAssignableById(assessmentId);
        verify(assessmentService).rejectInvitation(assessmentId, reason, comment);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void rejectAssignment_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        long competitionId = 3L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
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
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(assessmentService).rejectInvitation(assessmentId, reason, comment);
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_noReason() throws Exception {
        Long assessmentId = 1L;
        long competitionId = 3L;
        Boolean accept = false;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
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
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_neitherAcceptOrReject() throws Exception {
        Long assessmentId = 1L;
        long competitionId = 3L;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
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
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        long competitionId = 3L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
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
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectAssignment_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        long competitionId = 3L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(101, "comment"));
        Boolean accept = false;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(APPLICATION_ID)
                .withApplicationName("Application name")
                .withActivityState(PENDING)
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setAssessmentAccept(accept);
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/respond", assessmentId)
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
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseRestService).getByApplicationIdAndQuestionSetupType(APPLICATION_ID, PROJECT_SUMMARY);
        inOrder.verify(processRoleRestService).findProcessRole(APPLICATION_ID);
        inOrder.verify(organisationService).getApplicationOrganisations(processRoleResources);
        inOrder.verify(organisationService).getApplicationLeadOrganisation(processRoleResources);
        inOrder.verifyNoMoreInteractions();
    }
}
