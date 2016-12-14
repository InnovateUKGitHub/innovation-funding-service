package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.model.SendInvitePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.mockito.Mockito.when;

/**
 * This controller will handle all Competition Management requests related to sending competition invites to assessors
 */
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementSendInviteControllerTest extends BaseControllerMockMVCTest<CompetitionManagementSendInviteController> {



    @Override
    protected CompetitionManagementSendInviteController supplyControllerUnderTest() {
        return new CompetitionManagementSendInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setup();

    }

    @Test
    public void inviteEmail() throws Exception {
        long inviteId = 4L;
        CompetitionInviteResource invite = newCompetitionInviteResource().build();
        when(competitionInviteRestService.getCreated(inviteId)).thenReturn(restSuccess(invite));
    }

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private SendInvitePopulator sendInvitePopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String inviteEmail(Model model, @PathVariable("inviteId") long inviteId) {
        CompetitionInviteResource invite = competitionInviteRestService.getCreated(inviteId).getSuccessObjectOrThrowException();
        model.addAttribute("model", sendInvitePopulator.populateModel(invite));
        return "assessors/send-invites";
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String sendEmail(Model model,
                            @PathVariable("inviteId") long inviteId,
                            @ModelAttribute("form") @Valid SendInviteForm form) {
        RestResult<Void> result = competitionInviteRestService.sendInvite(inviteId);
        return "redirect:/competition/assessors/invite/" + inviteId;
    }
}
