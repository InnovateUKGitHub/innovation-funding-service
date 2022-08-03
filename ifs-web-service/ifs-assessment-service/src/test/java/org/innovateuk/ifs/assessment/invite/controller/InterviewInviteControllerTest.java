package org.innovateuk.ifs.assessment.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.InterviewInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.InterviewInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.InterviewInviteViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.invite.resource.InterviewInviteResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
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

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:application.yml", "classpath:/application-web-core.properties"} )
public class InterviewInviteControllerTest extends BaseControllerMockMVCTest<InterviewInviteController> {

    @Spy
    @InjectMocks
    private InterviewInviteModelPopulator interviewInviteModelPopulator;

    @Mock
    private InterviewInviteRestService interviewInviteRestService;

    @Mock
    private RejectionReasonRestService rejectionReasonRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    private final List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/interview/";

    @Override
    protected InterviewInviteController supplyControllerUnderTest() {
        return new InterviewInviteController();
    }

    @Before
    public void setUp() {
        when(rejectionReasonRestService.findAllActive()).thenReturn(restSuccess(rejectionReasons));
    }

    @Test
    public void acceptInvite_loggedIn() throws Exception {
        boolean accept = true;

        InterviewInviteResource inviteResource = newInterviewInviteResource()
                .withCompetitionName("my competition")
                .build();

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(publicContentItemRestService.getItemByCompetitionId(inviteResource.getCompetitionId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", Boolean.toString(accept)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite-accept/interview/hash/accept"));

        verifyNoInteractions(interviewInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);
        boolean accept = true;

        InterviewInviteResource inviteResource = newInterviewInviteResource()
                .withCompetitionName("my competition")
                .build();

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        InterviewInviteViewModel expectedViewModel = new InterviewInviteViewModel("hash", inviteResource, false, null);

        when(interviewInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(interviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        when(publicContentItemRestService.getItemByCompetitionId(inviteResource.getCompetitionId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", Boolean.toString(accept)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessor-interview-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(interviewInviteRestService);
        inOrder.verify(interviewInviteRestService).checkExistingUser("hash");
        inOrder.verify(interviewInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(interviewInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/interview/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(interviewInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(interviewInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/interview/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(interviewInviteRestService).acceptInvite("notExistHash");
    }

    @Test
    public void openInvite() throws Exception {
        InterviewInviteResource inviteResource = newInterviewInviteResource()
                .withCompetitionName("my competition")
                .build();

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        InterviewInviteViewModel expectedViewModel = new InterviewInviteViewModel("hash", inviteResource, true, null);

        when(interviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        when(publicContentItemRestService.getItemByCompetitionId(inviteResource.getCompetitionId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(get(restUrl + "{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-interview-invite"))
                .andExpect(model().attribute("model", expectedViewModel));

        verify(interviewInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(interviewInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(ReviewInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}", "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(interviewInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void noDecisionMade() throws Exception {
        InterviewInviteResource inviteResource = newInterviewInviteResource().withCompetitionName("my competition").build();
        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(interviewInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        when(publicContentItemRestService.getItemByCompetitionId(inviteResource.getCompetitionId())).thenReturn(restSuccess(publicContentItemResource));

        InterviewInviteForm expectedForm = new InterviewInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-interview-invite")).andReturn();

        InterviewInviteViewModel model = (InterviewInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getPanelInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        InterviewInviteForm form = (InterviewInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("acceptInvitation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("acceptInvitation").getDefaultMessage());

        verify(interviewInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(interviewInviteRestService);
    }

    @Test
    public void rejectInvite() throws Exception {
        boolean accept = false;

        when(interviewInviteRestService.rejectInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", Boolean.toString(accept)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/interview/hash/reject/thank-you"));

        verify(interviewInviteRestService).rejectInvite("hash");
        verifyNoMoreInteractions(interviewInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        boolean accept = false;

        when(interviewInviteRestService.rejectInvite("notExistHash")).thenReturn(restFailure(notFoundError(InterviewInviteResource.class, "notExistHash")));
        when(interviewInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(InterviewInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", Boolean.toString(accept)))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(interviewInviteRestService);
        inOrder.verify(interviewInviteRestService).rejectInvite("notExistHash");
        inOrder.verify(interviewInviteRestService).openInvite("notExistHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectThankYou() throws Exception {
        mockMvc.perform(get(restUrl + "{inviteHash}/reject/thank-you", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-interview-reject"))
                .andReturn();
    }
}
