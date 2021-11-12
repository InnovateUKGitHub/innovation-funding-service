package org.innovateuk.ifs.assessment.invite.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.invite.form.CompetitionInviteForm;
import org.innovateuk.ifs.assessment.invite.populator.CompetitionInviteModelPopulator;
import org.innovateuk.ifs.assessment.invite.viewmodel.CompetitionInviteViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.invite.service.RejectionReasonRestService;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
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


@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = { "classpath:/application.properties", "classpath:/application-web-core.properties"} )
public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

    @Spy
    @InjectMocks
    private CompetitionInviteModelPopulator competitionInviteModelPopulator;

    @Mock
    private RejectionReasonRestService rejectionReasonRestService;

    @Mock
    private CompetitionInviteRestService competitionInviteRestService;

    private List<RejectionReasonResource> rejectionReasons = newRejectionReasonResource()
            .withReason("Reason 1", "Reason 2")
            .build(2);

    private static final String restUrl = "/invite/competition/";

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
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
                .andExpect(redirectedUrl("/invite-accept/competition/hash/accept"));

        verifyZeroInteractions(competitionInviteRestService);
    }

    @Test
    public void acceptInvite_notLoggedInAndExistingUser() throws Exception {
        setLoggedInUser(null);
        ZonedDateTime acceptsDate = ZonedDateTime.now();
        ZonedDateTime deadlineDate = ZonedDateTime.now().plusDays(1);
        ZonedDateTime briefingDate = ZonedDateTime.now().plusDays(2);
        BigDecimal assessorPay = BigDecimal.TEN;
        Boolean accept = true;

        CompetitionInviteResource inviteResource = newCompetitionInviteResource()
                .withCompetitionName("my competition")
                .withAcceptsDate(acceptsDate).withDeadlineDate(deadlineDate)
                .withBriefingDate(briefingDate).withAssessorPay(assessorPay)
                .build();

        CompetitionInviteViewModel expectedViewModel = new CompetitionInviteViewModel("hash", inviteResource, false);

        when(competitionInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(TRUE));
        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
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
        Boolean accept = true;

        when(competitionInviteRestService.checkExistingUser("hash")).thenReturn(restSuccess(FALSE));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration/hash/start"));

        verify(competitionInviteRestService).checkExistingUser("hash");
    }

    @Test
    public void confirmAcceptInvite() throws Exception {
        when(competitionInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(get("/invite-accept/competition/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(competitionInviteRestService).acceptInvite("hash");
    }

    @Test
    public void confirmAcceptInvite_hashNotExists() throws Exception {
        when(competitionInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/invite-accept/competition/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService).acceptInvite("notExistHash");
    }

    @Test
    public void openInvite() throws Exception {
        ZonedDateTime acceptsDate = ZonedDateTime.now();
        ZonedDateTime deadlineDate = ZonedDateTime.now().plusDays(1);
        ZonedDateTime briefingDate = ZonedDateTime.now().plusDays(2);
        BigDecimal assessorPay = BigDecimal.TEN;
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition")
                .withAcceptsDate(acceptsDate).withDeadlineDate(deadlineDate)
                .withBriefingDate(briefingDate).withAssessorPay(assessorPay).build();

        CompetitionInviteViewModel expectedViewModel = new CompetitionInviteViewModel("hash", inviteResource, true);

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
    public void noDecisionMade() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        CompetitionInviteForm expectedForm = new CompetitionInviteForm();

        MvcResult result = mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "acceptInvitation"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("rejectionReasons", rejectionReasons))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-invite")).andReturn();

        CompetitionInviteViewModel model = (CompetitionInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        CompetitionInviteForm form = (CompetitionInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("acceptInvitation"));
        assertEquals("Please indicate your decision.", bindingResult.getFieldError("acceptInvitation").getDefaultMessage());

        verify(competitionInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), comment);

        when(competitionInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(competitionInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_noReason() throws Exception {
        Boolean accept = false;
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = String.join(" ", nCopies(100, "comment"));

        CompetitionInviteForm expectedForm = new CompetitionInviteForm();
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

        CompetitionInviteViewModel model = (CompetitionInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        verify(competitionInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        Boolean accept = false;
        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), null);

        when(competitionInviteRestService.rejectInvite("hash", competitionRejectionResource)).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "hash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/invite/competition/hash/reject/thank-you"));

        verify(competitionInviteRestService).rejectInvite("hash", competitionRejectionResource);
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_exceedsCharacterSizeLimit() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = RandomStringUtils.random(5001);
        Boolean accept = false;

        CompetitionInviteForm expectedForm = new CompetitionInviteForm();
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

        CompetitionInviteViewModel model = (CompetitionInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        CompetitionInviteForm form = (CompetitionInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(5000, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        verify(competitionInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_exceedsWordLimit() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));

        String comment = String.join(" ", nCopies(101, "comment"));
        Boolean accept = false;

        CompetitionInviteForm expectedForm = new CompetitionInviteForm();
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

        CompetitionInviteViewModel model = (CompetitionInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("hash", model.getCompetitionInviteHash());
        assertEquals("my competition", model.getCompetitionName());

        CompetitionInviteForm form = (CompetitionInviteForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("rejectComment"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("rejectComment").getDefaultMessage());
        assertEquals(100, bindingResult.getFieldError("rejectComment").getArguments()[1]);

        verify(competitionInviteRestService).openInvite("hash");
        verifyNoMoreInteractions(competitionInviteRestService);
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        Boolean accept = false;

        CompetitionRejectionResource competitionRejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .with(id(1L))
                .build(), comment);

        when(competitionInviteRestService.rejectInvite("notExistHash", competitionRejectionResource)).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        when(competitionInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "{inviteHash}/decision", "notExistHash")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("acceptInvitation", accept.toString())
                .param("rejectReason", "1")
                .param("rejectComment", comment))
                .andExpect(status().isNotFound());

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).rejectInvite("notExistHash", competitionRejectionResource);
        inOrder.verify(competitionInviteRestService).openInvite("notExistHash");
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
