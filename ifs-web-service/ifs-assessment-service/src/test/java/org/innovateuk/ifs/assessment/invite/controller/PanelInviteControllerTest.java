package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.PanelInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.PanelInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.populator.RejectCompetitionModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.PanelInviteViewModel;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
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
import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
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
public class PanelInviteControllerTest extends BaseControllerMockMVCTest<PanelInviteController> {
    @Spy
    @InjectMocks
    private PanelInviteModelPopulator panelInviteModelPopulator;

    @Spy
    @InjectMocks
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/panel/";

    @Override
    protected PanelInviteController supplyControllerUnderTest() {
        return new PanelInviteController();
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

        verifyZeroInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);
        ZonedDateTime panelDate = ZonedDateTime.now();
        Boolean accept = true;

        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource()
                .withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        PanelInviteViewModel expectedViewModel = new PanelInviteViewModel("hash", inviteResource, false);

        when(assessmentPanelInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessor-panel-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(assessmentPanelInviteRestService);
        inOrder.verify(assessmentPanelInviteRestService).checkExistingUser("hash");
        inOrder.verify(assessmentPanelInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(assessmentPanelInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(assessmentPanelInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(assessmentPanelInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/panel/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(assessmentPanelInviteRestService).acceptInvite("notExistHash");
    }

    @Test
    public void openInvite() throws Exception {
        ZonedDateTime panelDate = ZonedDateTime.now();

        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource().withCompetitionName("my competition")
                .withPanelDate(panelDate)
                .build();

        PanelInviteViewModel expectedViewModel = new PanelInviteViewModel("hash", inviteResource, true);

        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-panel-invite"))
                .andExpect(model().attribute("model", expectedViewModel));

        verify(assessmentPanelInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(assessmentPanelInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentPanelInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}", "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(assessmentPanelInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void noDecisionMade() throws Exception {
        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource().withCompetitionName("my competition").build();

        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        PanelInviteForm expectedForm = new PanelInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-panel-invite")).andReturn();

        PanelInviteViewModel model = (PanelInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        PanelInviteForm form = (PanelInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("acceptInvitation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("acceptInvitation").getDefaultMessage());

        verify(assessmentPanelInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite() throws Exception {
        Boolean accept = false;

        when(assessmentPanelInviteRestService.rejectInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/panel/hash/reject/thank-you"));

        verify(assessmentPanelInviteRestService).rejectInvite("hash");
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        when(assessmentPanelInviteRestService.rejectInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentPanelInviteResource.class, "notExistHash")));
        when(assessmentPanelInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentPanelInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(assessmentPanelInviteRestService);
        inOrder.verify(assessmentPanelInviteRestService).rejectInvite("notExistHash");
        inOrder.verify(assessmentPanelInviteRestService).openInvite("notExistHash");
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
