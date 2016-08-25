package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.RejectCompetitionForm;
import com.worth.ifs.assessment.model.RejectCompetitionModelPopulator;
import com.worth.ifs.assessment.viewmodel.CompetitionInviteViewModel;
import com.worth.ifs.assessment.viewmodel.RejectCompetitionViewModel;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

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
    public void openInvite() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-invite"))
                .andExpect(model().attribute("model", new CompetitionInviteViewModel("my competition")));

        verify(competitionInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().is2xxSuccessful());

        verify(competitionInviteRestService).openInvite("notExistHash");
    }

    @Test
    public void rejectInviteConfirm() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(restSuccess(inviteResource));

        RejectCompetitionForm expectedForm = new RejectCompetitionForm();

        MvcResult result = mockMvc.perform(get(restUrl + "hash/reject/confirm"))
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
        mockMvc.perform(get(restUrl + "notExistHash/reject/confirm"))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService).getInvite("notExistHash");
    }
}