package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.management.model.InviteAssessorsFindModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsInviteModelPopulator;
import org.innovateuk.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static java.lang.String.format;

/**
 * This controller will handle all Competition Management requests related to inviting assessors to a Competition.
 */
@Controller
@RequestMapping("/competition/{competitionId}/assessors")
public class CompetitionManagementInviteAssessorsController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private InviteAssessorsFindModelPopulator inviteAssessorsFindModelPopulator;

    @Autowired
    private InviteAssessorsInviteModelPopulator inviteAssessorsInviteModelPopulator;

    @Autowired
    private InviteAssessorsOverviewModelPopulator inviteAssessorsOverviewModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String assessors(@PathVariable("competitionId") Long competitionId) {
        return format("redirect:/competition/%s/assessors/find", competitionId);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public String find(Model model, @PathVariable("competitionId") Long competitionId) {
        return doViewFind(model, competitionId);
    }

    @RequestMapping(value = "/find", params = {"add"}, method = RequestMethod.POST)
    public String addInviteFromFindView(Model model, @PathVariable("competitionId") Long competitionId, @RequestParam(name = "add") String email) {
        inviteUser(email, competitionId).getSuccessObjectOrThrowException();
        return doViewFind(model, competitionId);
    }

    @RequestMapping(value = "/find", params = {"remove"}, method = RequestMethod.POST)
    public String removeInviteFromFindView(Model model, @PathVariable("competitionId") Long competitionId, @RequestParam(name = "remove") String email) {
        deleteInvite(email, competitionId).getSuccessObjectOrThrowException();
        return doViewFind(model, competitionId);
    }

    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public String invite(Model model, @PathVariable("competitionId") Long competitionId) {
        return doViewInvite(model, competitionId);
    }

    @RequestMapping(value = "/invite", params = {"remove"}, method = RequestMethod.POST)
    public String removeInviteFromInviteView(Model model, @PathVariable("competitionId") Long competitionId, @RequestParam(name = "remove") String email) {
        deleteInvite(email, competitionId);
        return doViewInvite(model, competitionId);
    }

    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public String overview(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsOverviewModelPopulator.populateModel(competition));
        return "assessors/overview";
    }

    private ServiceResult<CompetitionInviteResource> inviteUser(String email, Long competitionId) {
        return competitionInviteRestService.inviteUser(new ExistingUserStagedInviteResource(email, competitionId)).toServiceResult();
    }

    private ServiceResult<Void> deleteInvite(String email, Long competitionId) {
        return competitionInviteRestService.deleteInvite(email, competitionId).toServiceResult();
    }

    private String doViewFind(Model model, Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsFindModelPopulator.populateModel(competition));
        return "assessors/find";
    }

    private String doViewInvite(Model model, Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsInviteModelPopulator.populateModel(competition));
        return "assessors/invite";
    }
}
