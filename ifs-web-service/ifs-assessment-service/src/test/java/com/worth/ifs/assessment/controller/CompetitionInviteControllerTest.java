package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.RejectCompetitionForm;
import com.worth.ifs.assessment.model.CompetitionInviteModelPopulator;
import com.worth.ifs.assessment.model.RejectCompetitionModelPopulator;
import com.worth.ifs.assessment.viewmodel.CompetitionInviteViewModel;
import com.worth.ifs.assessment.viewmodel.RejectCompetitionViewModel;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
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

import java.util.List;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {
    @Spy
    @InjectMocks
    private CompetitionInviteModelPopulator competitionInviteModelPopulator;

    @Spy
    @InjectMocks
    private RejectCompetitionModelPopulator rejectCompetitionModelPopulator;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/competition/";

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        when(rejectionReasonRestService.findAllActive()).thenReturn(restSuccess(rejectionReasons));
    }

    @Test
    public void acceptInvite_loggedIn() throws Exception {
        mockMvc.perform(post(restUrl + "{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite-accept/competition/hash/accept"));

        verifyZeroInteractions(competitionInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);

        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        CompetitionInviteViewModel expectedViewModel = new CompetitionInviteViewModel("hash", "my competition");

        when(competitionInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/accept", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessor-competition-accept-user-exists-but-not-logged-in"));

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).checkExistingUser("hash");
        inOrder.verify(competitionInviteRestService).openInvite("hash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptInvite_notLoggedInAndNotExistingUser() throws Exception {
        setLoggedInUser(null);

        when(competitionInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(FALSE));

        mockMvc.perform(post(restUrl + "{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/start"));

        verify(competitionInviteRestService).checkExistingUser("hash");
    }

    @Test
    public void openInvite() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        CompetitionInviteViewModel expectedViewModel = new CompetitionInviteViewModel("hash", "my competition");

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-invite"))
                .andExpect(model().attribute("model", expectedViewModel));

        verify(competitionInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}", "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void rejectInvite() throws Exception {
        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), "comment");

        when(competitionInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/reject", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", "1")
                .param("rejectComment", "comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(competitionInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_noReason() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(restSuccess(inviteResource));

        RejectCompetitionForm expectedForm = new RejectCompetitionForm();
        expectedForm.setRejectComment("comment");

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/reject", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectComment", "comment"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-reject-confirm")).andReturn();

        RejectCompetitionViewModel model = (RejectCompetitionViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        verify(competitionInviteRestService).getInvite("hash");
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), null);

        when(competitionInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/reject", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(competitionInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), "comment");

        when(competitionInviteRestService.rejectInvite("notExistHash", competitionRejectionResource)).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        when(competitionInviteRestService.getInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/reject", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("rejectReason", "1")
                .param("rejectComment", "comment"))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).rejectInvite("notExistHash", competitionRejectionResource);
        inOrder.verify(competitionInviteRestService).getInvite("notExistHash");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void rejectThankYou() throws Exception {
        mockMvc.perform(get(restUrl + "{inviteHash}/reject/thank-you", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-reject"))
                .andReturn();
    }

    @Test
    public void rejectInviteConfirm() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(restSuccess(inviteResource));

        RejectCompetitionForm expectedForm = new RejectCompetitionForm();

        MvcResult result = mockMvc.perform(get(restUrl + "{inviteHash}/reject/confirm", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-reject-confirm"))
                .andReturn();

        RejectCompetitionViewModel model = (RejectCompetitionViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        verify(competitionInviteRestService).getInvite("hash");
    }

    @Test
    public void rejectInviteConfirm_hashNotExists() throws Exception {
        when(competitionInviteRestService.getInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "{inviteHash}/reject/confirm", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService).getInvite("notExistHash");
    }
}