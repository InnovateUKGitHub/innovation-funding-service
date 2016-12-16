package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementDashboardController {
    public static final String TEMPLATE_PATH = "dashboard/";

    @Autowired
    CompetitionService competitionService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard() {
        return "redirect:/dashboard/live";
    }

    @RequestMapping(value="/dashboard/live", method= RequestMethod.GET)
    public String live(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getLiveCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "live";
    }

    @RequestMapping(value="/dashboard/project-setup", method= RequestMethod.GET)
    public String projectSetup(Model model, HttpServletRequest request) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> projectSetupCompetitions = competitionService.getProjectSetupCompetitions();
        model.addAttribute("competitions", projectSetupCompetitions);
        model.addAttribute("formattedInnovationAreas", formatInnovationAreaNames(projectSetupCompetitions));
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "projectSetup";
    }

    @RequestMapping(value="/dashboard/upcoming", method= RequestMethod.GET)
    public String upcoming(Model model, HttpServletRequest request) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> upcomingCompetitions = competitionService.getUpcomingCompetitions();
        model.addAttribute("competitions", upcomingCompetitions);
        model.addAttribute("formattedInnovationAreas", formatInnovationAreaNames(upcomingCompetitions));
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "upcoming";
    }

    @RequestMapping(value="/dashboard/complete", method= RequestMethod.GET)
    public String complete(Model model, HttpServletRequest request) {
        //TODO INFUND-3833
        model.addAttribute("competitions", new ArrayList<CompetitionResource>());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "complete";
    }

    @RequestMapping(value="/dashboard/search", method= RequestMethod.GET)
    public String search(@RequestParam(name = "searchQuery") String searchQuery,
                           @RequestParam(name = "page", defaultValue = "1") int page, Model model, HttpServletRequest request) {
        model.addAttribute("results", competitionService.searchCompetitions(searchQuery, page - 1));
        model.addAttribute("searchQuery", searchQuery);
        return TEMPLATE_PATH + "search";
    }

    @RequestMapping("/competition/create")
    public String create(){
        CompetitionResource competition = competitionService.create();
        return String.format("redirect:/competition/setup/%s", competition.getId());
    }

    private List<String> formatInnovationAreaNames(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitionTypes) {

        List<String> formattedList = new ArrayList<>();

        for (Map.Entry<CompetitionStatus, List<CompetitionSearchResultItem>> entry : competitionTypes.entrySet()) {
            for (CompetitionSearchResultItem competition : entry.getValue()) {
                formattedList.add(competition.getInnovationAreaNames().stream().collect(joining(", ")));
            }
        }
        return formattedList;
    }
}
