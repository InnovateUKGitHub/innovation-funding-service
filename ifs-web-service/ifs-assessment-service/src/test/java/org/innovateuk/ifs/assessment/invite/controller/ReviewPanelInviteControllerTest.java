package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.ReviewPanelInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.ReviewPanelInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.ReviewPanelInviteViewModel;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewPanelInviteResourceBuilder.newAssessmentReviewPanelInviteResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ReviewPanelInviteControllerTest extends BaseControllerMockMVCTest<ReviewPanelInviteController> {

    @Spy
    @InjectMocks
    private ReviewPanelInviteModelPopulator reviewPanelInviteModelPopulator;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/panel/";

    @Override
    protected ReviewPanelInviteController supplyControllerUnderTest() {
        return new ReviewPanelInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        when(rejectionReasonRestService.findAllActive()).thenReturn(restSuccess(rejectionReasons));
    }

    @Test
    public void acceptInvite_loggedIn() throws Exception {
        Boolean accept = true;

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite-accept/panel/hash/accept"));

        verifyZeroInteractions(assessmentReviewPanelInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);
        ZonedDateTime panelDate = ZonedDateTime.now();
        Boolean accept = true;

        AssessmentReviewPanelInviteResource inviteResource = newAssessmentReviewPanelInviteResource()
                .withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        ReviewPanelInviteViewModel expectedViewModel = new ReviewPanelInviteViewModel("hash", inviteResource, false);

        when(assessmentReviewPanelInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(assessmentReviewPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessor-panel-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(assessmentReviewPanelInviteRestService);
        inOrder.verify(assessmentReviewPanelInviteRestService).checkExistingUser("hash");
        inOrder.verify(assessmentReviewPanelInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(assessmentReviewPanelInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(assessmentReviewPanelInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(assessmentReviewPanelInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(assessmentReviewPanelInviteRestService).acceptInvite("notExistHash");
    }

    @Test
    public void openInvite() throws Exception {
        ZonedDateTime panelDate = ZonedDateTime.now();

        AssessmentReviewPanelInviteResource inviteResource = newAssessmentReviewPanelInviteResource().withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        ReviewPanelInviteViewModel expectedViewModel = new ReviewPanelInviteViewModel("hash", inviteResource, true);

        when(assessmentReviewPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-invite"))
                .andExpect(model().attribute("model", expectedViewModel));

        verify(assessmentReviewPanelInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(assessmentReviewPanelInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentReviewPanelInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}", "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(assessmentReviewPanelInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void noDecisionMade() throws Exception {
        AssessmentReviewPanelInviteResource inviteResource = newAssessmentReviewPanelInviteResource().withCompetitionName("my competition").build();

        when(assessmentReviewPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        ReviewPanelInviteForm expectedForm = new ReviewPanelInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-panel-invite")).andReturn();

        ReviewPanelInviteViewModel model = (ReviewPanelInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        ReviewPanelInviteForm form = (ReviewPanelInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("acceptInvitation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("acceptInvitation").getDefaultMessage());

        verify(assessmentReviewPanelInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(assessmentReviewPanelInviteRestService);
    }

    @Test
    public void rejectInvite() throws Exception {
        Boolean accept = false;

        when(assessmentReviewPanelInviteRestService.rejectInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/panel/hash/reject/thank-you"));

        verify(assessmentReviewPanelInviteRestService).rejectInvite("hash");
        verifyNoMoreInteractions(assessmentReviewPanelInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        when(assessmentReviewPanelInviteRestService.rejectInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentReviewPanelInviteResource.class, "notExistHash")));
        when(assessmentReviewPanelInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentReviewPanelInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(assessmentReviewPanelInviteRestService);
        inOrder.verify(assessmentReviewPanelInviteRestService).rejectInvite("notExistHash");
        inOrder.verify(assessmentReviewPanelInviteRestService).openInvite("notExistHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectThankYou() throws Exception {
        mockMvc.perform(get(restUrl + "{inviteHash}/reject/thank-you", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-reject"))
                .andReturn();
    }
}
