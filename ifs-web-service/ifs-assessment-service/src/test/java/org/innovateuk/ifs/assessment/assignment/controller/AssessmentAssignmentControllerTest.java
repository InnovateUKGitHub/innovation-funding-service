package org.innovateuk.ifs.assessment.assignment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.assignment.controller.AssessmentAssignmentController;
import org.innovateuk.ifs.assessment.assignment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.assignment.populator.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.assignment.populator.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.assignment.viewmodel.AssessmentAssignmentViewModel;
import org.innovateuk.ifs.assessment.overview.viewmodel.RejectAssessmentViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Comparator.comparingLong;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.PENDING;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentAssignmentControllerTest extends BaseControllerMockMVCTest<AssessmentAssignmentController> {

    @Spy
    @InjectMocks
    private AssessmentAssignmentModelPopulator assessmentAssignmentModelPopulator;

    @Spy
    @InjectMocks
    private RejectAssessmentModelPopulator rejectAssessmentModelPopulator;

    @Mock
    private AssessmentService assessmentService;

    @Override
    protected AssessmentAssignmentController supplyControllerUnderTest() {
        return new AssessmentAssignmentController();
    }

    @Test
    public void viewAssignment() throws Exception {
        long assessmentId = 1L;
        long applicationId = 2L;
        long competitionId = 3L;

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(newAssessmentResource()
                .withApplication(applicationId)
                .withApplicationName("Application name")
                .withCompetition(competitionId)
                .build());

        when(formInputResponseService.getByApplicationIdAndQuestionName(applicationId, "Project summary"))
                .thenReturn(newFormInputResponseResource()
                        .withValue("Project summary")
                        .build());

        OrganisationResource collaboratorOrganisation1 = newOrganisationResource().build();
        OrganisationResource collaboratorOrganisation2 = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        OrganisationResource otherOrganisation = newOrganisationResource().build();

        List<ProcessRoleResource> processRoleResources = newProcessRoleResource()
                .withOrganisation(collaboratorOrganisation1.getId(),
                        leadOrganisation.getId(),
                        collaboratorOrganisation2.getId(),
                        otherOrganisation.getId())
                .withRoleName(COLLABORATOR.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName(), ASSESSOR.getName())
                .build(4);

        when(processRoleService.findProcessRolesByApplicationId(applicationId)).thenReturn(processRoleResources);
        when(organisationRestService.getOrganisationById(collaboratorOrganisation1.getId())).thenReturn(restSuccess(collaboratorOrganisation1));
        when(organisationRestService.getOrganisationById(collaboratorOrganisation2.getId())).thenReturn(restSuccess(collaboratorOrganisation2));
        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));

        SortedSet<OrganisationResource> partners = new TreeSet<>(comparingLong(OrganisationResource::getId));
        partners.add(collaboratorOrganisation1);
        partners.add(leadOrganisation);
        partners.add(collaboratorOrganisation2);

        AssessmentAssignmentViewModel expectedViewModel = new AssessmentAssignmentViewModel(assessmentId,
                competitionId,
                "Application name",
                partners, leadOrganisation, "Project summary");

        mockMvc.perform(get("/{assessmentId}/assignment", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/assessment-invitation")).andReturn();

        InOrder inOrder = inOrder(assessmentService, formInputResponseService, processRoleService, organisationRestService);
        inOrder.verify(assessmentService).getAssignableById(assessmentId);
        inOrder.verify(formInputResponseService).getByApplicationIdAndQuestionName(applicationId, "Project summary");
        inOrder.verify(processRoleService).findProcessRolesByApplicationId(applicationId);
        asList(collaboratorOrganisation1, collaboratorOrganisation2, leadOrganisation).forEach(organisationResource ->
                inOrder.verify(organisationRestService).getOrganisationById(organisationResource.getId()));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptAssignment() throws Exception {
        Long assessmentId = 1L;
        Long competitionId = 2L;

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(assessment);

        mockMvc.perform(post("/{assessmentId}/assignment/accept", assessmentId))
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
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withCompetition(competitionId)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/{assessmentId}/assignment/reject", assessmentId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", reason.name())
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessor/dashboard/competition/%s", competitionId)))
                .andReturn();

        verify(assessmentService).getRejectableById(assessmentId);
        verify(assessmentService).rejectInvitation(assessmentId, reason, comment);
        verifyNoMoreInteractions(assessmentService);
    }

    @Test
    public void rejectAssignment_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(PENDING)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        // The non-js confirmation view should be returned with the fields pre-populated in the form and a global error

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                PENDING
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/reject", assessmentId)
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

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

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
    public void rejectAssignment_noReason() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        String comment = String.join(" ", nCopies(100, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(PENDING)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                PENDING
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/reject", assessmentId)
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

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

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
    public void rejectAssignment_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = RandomStringUtils.random(5001);

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(PENDING)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                PENDING
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/reject", assessmentId)
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

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

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
    public void rejectAssignment_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        AssessmentRejectOutcomeValue reason = CONFLICT_OF_INTEREST;
        String comment = String.join(" ", nCopies(101, "comment"));

        AssessmentResource assessment = newAssessmentResource()
                .with(id(assessmentId))
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(PENDING)
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                PENDING
        );

        MvcResult result = mockMvc.perform(post("/{assessmentId}/assignment/reject", assessmentId)
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

        AssessmentAssignmentForm form = (AssessmentAssignmentForm) result.getModelAndView().getModel().get("form");

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
    public void rejectAssignmentConfirm() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(newAssessmentResource()
                .withId(assessmentId)
                .withApplication(applicationId)
                .withApplicationName("application name")
                .withActivityState(PENDING)
                .build());

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name",
                PENDING
        );

        mockMvc.perform(get("/{assessmentId}/assignment/reject/confirm", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/reject-invitation-confirm"));

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }
}
