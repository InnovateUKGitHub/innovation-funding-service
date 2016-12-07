package com.worth.ifs.management.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.management.model.InviteAssessorsFindModelPopulator;
import com.worth.ifs.management.model.InviteAssessorsInviteModelPopulator;
import com.worth.ifs.management.model.InviteAssessorsOverviewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsFindModelPopulator.populateModel(competition));
        return "assessors/find";
    }

    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public String invite(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsInviteModelPopulator.populateModel(competition));
        return "assessors/invite";
    }

    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    public String overview(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        model.addAttribute("model", inviteAssessorsOverviewModelPopulator.populateModel(competition));
        return "assessors/overview";
    }
}