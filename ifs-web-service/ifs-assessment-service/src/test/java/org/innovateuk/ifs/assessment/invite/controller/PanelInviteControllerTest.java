package org.innovateuk.ifs.assessment.invite.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.PanelInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.PanelInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.populator.RejectCompetitionModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.PanelInviteViewModel;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.assessment.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
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

    private static final String restUrl = "/invite/competition/";

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
                .andExpect(redirectedUrl("/invite-accept/competition/hash/accept"));

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
                .andExpect(view().name("assessor-competition-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(assessmentPanelInviteRestService);
        inOrder.verify(assessmentPanelInviteRestService).checkExistingUser("hash");
        inOrder.verify(assessmentPanelInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notLoggedInAndNotExistingUser() throws Exception {
        setLoggedInUser(null);
        Boolean accept = true;

        when(assessmentPanelInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(FALSE));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/start"));

        verify(assessmentPanelInviteRestService).checkExistingUser("hash");
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(assessmentPanelInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/competition/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(assessmentPanelInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(assessmentPanelInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/competition/{inviteHash}/accept", "notExistHash"))
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
                .andExpect(view().name("assessor-competition-invite"))
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

        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        PanelInviteForm expectedForm = new PanelInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-invite")).andReturn();

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
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), comment);

        when(assessmentPanelInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(assessmentPanelInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_noReason() throws Exception {
        Boolean accept = false;
        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource().withCompetitionName("my competition").build();

        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = String.join(" ", nCopies(100, "comment"));

        PanelInviteForm expectedForm = new PanelInviteForm();
        expectedForm.setAcceptInvitation(accept);
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-invite")).andReturn();

        PanelInviteViewModel model = (PanelInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        verify(assessmentPanelInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        Boolean accept = false;
        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), null);

        when(assessmentPanelInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(assessmentPanelInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_exceedsCharacterSizeLimit() throws Exception {
        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource().withCompetitionName("my competition").build();

        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        PanelInviteForm expectedForm = new PanelInviteForm();
        expectedForm.setAcceptInvitation(accept);
        expectedForm.setRejectReason(newRejectionReasonResource().with(id(1L)).build());
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-invite")).andReturn();

        PanelInviteViewModel model = (PanelInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        PanelInviteForm form = (PanelInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        verify(assessmentPanelInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_exceedsWordLimit() throws Exception {
        AssessmentPanelInviteResource inviteResource = newAssessmentPanelInviteResource().withCompetitionName("my competition").build();

        when(assessmentPanelInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = String.join(" ", nCopies(101, "comment"));
        Boolean accept = false;

        PanelInviteForm expectedForm = new PanelInviteForm();
        expectedForm.setAcceptInvitation(accept);
        expectedForm.setRejectReason(newRejectionReasonResource().with(id(1L)).build());
        expectedForm.setRejectComment(comment);

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "rejectComment"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-invite")).andReturn();

        PanelInviteViewModel model = (PanelInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        PanelInviteForm form = (PanelInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        verify(assessmentPanelInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(assessmentPanelInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), comment);

        when(assessmentPanelInviteRestService.rejectInvite("notExistHash", competitionRejectionResource)).thenReturn(restFailure(notFoundError(AssessmentPanelInviteResource.class, "notExistHash")));
        when(assessmentPanelInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(AssessmentPanelInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(assessmentPanelInviteRestService);
        inOrder.verify(assessmentPanelInviteRestService).rejectInvite("notExistHash", competitionRejectionResource);
        inOrder.verify(assessmentPanelInviteRestService).openInvite("notExistHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectThankYou() throws Exception {
        mockMvc.perform(get(restUrl + "{inviteHash}/reject/thank-you", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-reject"))
                .andReturn();
    }
}
