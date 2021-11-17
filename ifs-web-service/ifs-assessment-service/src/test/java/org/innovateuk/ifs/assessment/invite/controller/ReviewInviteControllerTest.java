package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.ReviewInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.ReviewInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.ReviewInviteViewModel;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
import org.innovateuk.ifs.review.service.ReviewInviteRestService;
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

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:application.properties", "classpath:/application-web-core.properties"} )
public class ReviewInviteControllerTest extends BaseControllerMockMVCTest<ReviewInviteController> {

    @Spy
    @InjectMocks
    private ReviewInviteModelPopulator reviewInviteModelPopulator;

    @Mock
    private RejectionReasonRestService rejectionReasonRestService;

    @Mock
    private ReviewInviteRestService reviewInviteRestService;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/panel/";

    @Override
    protected ReviewInviteController supplyControllerUnderTest() {
        return new ReviewInviteController();
    }

    @Before
    public void setUp() {
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

        verifyZeroInteractions(reviewInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);
        ZonedDateTime panelDate = ZonedDateTime.now();
        Boolean accept = true;

        ReviewInviteResource inviteResource = newReviewInviteResource()
                .withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        ReviewInviteViewModel expectedViewModel = new ReviewInviteViewModel("hash", inviteResource, false);

        when(reviewInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(reviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessor-panel-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(reviewInviteRestService);
        inOrder.verify(reviewInviteRestService).checkExistingUser("hash");
        inOrder.verify(reviewInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(reviewInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(reviewInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(reviewInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(reviewInviteRestService).acceptInvite("notExistHash");
    }

    @Test
    public void openInvite() throws Exception {
        ZonedDateTime panelDate = ZonedDateTime.now();

        ReviewInviteResource inviteResource = newReviewInviteResource().withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        ReviewInviteViewModel expectedViewModel = new ReviewInviteViewModel("hash", inviteResource, true);

        when(reviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-invite"))
                .andExpect(model().attribute("model", expectedViewModel));

        verify(reviewInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(reviewInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(ReviewInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}", "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(reviewInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void noDecisionMade() throws Exception {
        ReviewInviteResource inviteResource = newReviewInviteResource().withCompetitionName("my competition").build();

        when(reviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        ReviewInviteForm expectedForm = new ReviewInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-panel-invite")).andReturn();

        ReviewInviteViewModel model = (ReviewInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        ReviewInviteForm form = (ReviewInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("acceptInvitation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("acceptInvitation").getDefaultMessage());

        verify(reviewInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(reviewInviteRestService);
    }

    @Test
    public void rejectInvite() throws Exception {
        Boolean accept = false;

        when(reviewInviteRestService.rejectInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/panel/hash/reject/thank-you"));

        verify(reviewInviteRestService).rejectInvite("hash");
        verifyNoMoreInteractions(reviewInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        when(reviewInviteRestService.rejectInvite("notExistHash")).thenReturn(restFailure(notFoundError(ReviewInviteResource.class, "notExistHash")));
        when(reviewInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(ReviewInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(reviewInviteRestService);
        inOrder.verify(reviewInviteRestService).rejectInvite("notExistHash");
        inOrder.verify(reviewInviteRestService).openInvite("notExistHash");
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
