package com.worth.ifs.competition.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSearchResultItem;
import com.worth.ifs.project.status.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    public static final String TEMPLATE_PATH = "competition/";

    @Autowired
    CompetitionService competitionService;

    @Autowired
    private ProjectStatusService projectStatusService;

    @RequestMapping(value="", method= RequestMethod.GET)
    public String dashboard() {
        return "redirect:/dashboard/live";
    }

    @RequestMapping(value="/live", method= RequestMethod.GET)
    public String live(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getLiveCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "live";
    }

    @RequestMapping(value="/projectSetup", method= RequestMethod.GET)
    public String projectSetup(Model model, HttpServletRequest request) {

        model.addAttribute("competitions", competitionService.getProjectSetupCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());

        // Get the number of projects that are in setup for each competition => return as map
        model.addAttribute("projectsCount", competitionService.getProjectSetupCompetitions().get(CompetitionResource.Status.PROJECT_SETUP).stream().collect(
                Collectors.toMap(x -> x.getId(), x -> projectStatusService.getCompetitionStatus(x.getId()).getProjectStatusResources().size())));

        return TEMPLATE_PATH + "projectSetup";
    }

    @RequestMapping(value="/upcoming", method= RequestMethod.GET)
    public String upcoming(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getUpcomingCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "upcoming";
    }

    @RequestMapping(value="/complete", method= RequestMethod.GET)
    public String complete(Model model, HttpServletRequest request) {
        //TODO INFUND-3833
        model.addAttribute("competitions", new ArrayList<CompetitionResource>());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "complete";
    }

    @RequestMapping(value="/search", method= RequestMethod.GET)
    public String search(@RequestParam(name = "searchQuery") String searchQuery,
                           @RequestParam(name = "page", defaultValue = "1") int page, Model model, HttpServletRequest request) {
        model.addAttribute("results", competitionService.searchCompetitions(searchQuery, page - 1));
        model.addAttribute("searchQuery", searchQuery);
        return TEMPLATE_PATH + "search";
    }

}
