package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.form.AssessmentAssignmentForm;
import org.innovateuk.ifs.assessment.model.AssessmentAssignmentModelPopulator;
import org.innovateuk.ifs.assessment.model.RejectAssessmentModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.viewmodel.RejectAssessmentViewModel;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static java.lang.String.format;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.idBasedValues;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
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
    public void viewAssessmentAssignment() throws Exception {
        Long assessmentId = 1L;
        Long applicationId = 2L;
        Long formInput = 11L;
        Long competitionId = 3L;

        FormInputResponseResource applicantResponse =
                newFormInputResponseResource()
                        .withFormInputs(formInput)
                        .with(idBasedValues("Value "))
                        .build();

        when(assessmentService.getAssignableById(assessmentId)).thenReturn(newAssessmentResource().withApplication(applicationId).build());
        when(competitionService.getById(competitionId)).thenReturn(newCompetitionResource().build());
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());
        when(formInputResponseService.getByFormInputIdAndApplication(formInput, applicationId)).thenReturn(restSuccess(singletonList(applicantResponse)));

        mockMvc.perform(get("/{assessmentId}/assignment", assessmentId))
                .andExpect(status().isOk())
                .andExpect(view().name("assessment/assessment-invitation"));
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
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);
        when(assessmentService.rejectInvitation(assessmentId, reason, comment)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        // The non-js confirmation view should be returned with the fields pre-populated in the form and a global error

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name"
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
                .andExpect(view().name("assessment/assessment-reject-confirm"))
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
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name"
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
                .andExpect(view().name("assessment/assessment-reject-confirm"))
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
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name"
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
                .andExpect(view().name("assessment/assessment-reject-confirm"))
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
                .build();

        when(assessmentService.getRejectableById(assessmentId)).thenReturn(assessment);

        // The non-js confirmation view should be returned with the comment pre-populated in the form and an error for the missing reason

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();
        expectedForm.setRejectReason(reason);
        expectedForm.setRejectComment(comment);

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name"
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
                .andExpect(view().name("assessment/assessment-reject-confirm"))
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
                .build());

        AssessmentAssignmentForm expectedForm = new AssessmentAssignmentForm();

        RejectAssessmentViewModel expectedViewModel = new RejectAssessmentViewModel(assessmentId,
                applicationId,
                "application name"
        );

        mockMvc.perform(get("/{assessmentId}/assignment/reject/confirm", assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessment/assessment-reject-confirm"));

        InOrder inOrder = inOrder(assessmentService);
        inOrder.verify(assessmentService).getRejectableById(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }
}
