package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.service.CompetitionDashboardSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementDashboardController {
    public static final String TEMPLATE_PATH = "dashboard/";

    @Autowired
    private CompetitionDashboardSearchService competitionDashboardSearchService;

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard/live";
    }

    @GetMapping("/dashboard/live")
    public String live(Model model) {
        model.addAttribute("competitions", competitionDashboardSearchService.getLiveCompetitions());
        model.addAttribute("counts", competitionDashboardSearchService.getCompetitionCounts());
        return TEMPLATE_PATH + "live";
    }

    @GetMapping("/dashboard/project-setup")
    public String projectSetup(Model model) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> projectSetupCompetitions = competitionDashboardSearchService.getProjectSetupCompetitions();
        model.addAttribute("competitions", projectSetupCompetitions);
        model.addAttribute("formattedInnovationAreas", formatInnovationAreaNames(projectSetupCompetitions));
        model.addAttribute("counts", competitionDashboardSearchService.getCompetitionCounts());
        return TEMPLATE_PATH + "projectSetup";
    }

    @GetMapping("/dashboard/upcoming")
    public String upcoming(Model model) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> upcomingCompetitions = competitionDashboardSearchService.getUpcomingCompetitions();
        model.addAttribute("competitions", upcomingCompetitions);
        model.addAttribute("formattedInnovationAreas", formatInnovationAreaNames(upcomingCompetitions));
        model.addAttribute("counts", competitionDashboardSearchService.getCompetitionCounts());
        return TEMPLATE_PATH + "upcoming";
    }

    @GetMapping("/dashboard/complete")
    public String complete(Model model) {
        //TODO INFUND-3833
        model.addAttribute("competitions", new ArrayList<CompetitionResource>());
        model.addAttribute("counts", competitionDashboardSearchService.getCompetitionCounts());
        return TEMPLATE_PATH + "complete";
    }


    @GetMapping("/dashboard/non-ifs")
    public String nonIfs(Model model) {
        model.addAttribute("competitions", competitionDashboardSearchService.getNonIfsCompetitions());
        model.addAttribute("counts", competitionDashboardSearchService.getCompetitionCounts());
        return TEMPLATE_PATH + "non-ifs";
    }

    @GetMapping("/dashboard/search")
    public String search(@RequestParam(name = "searchQuery") String searchQuery,
                           @RequestParam(name = "page", defaultValue = "1") int page, Model model) {
        String trimmedSearchQuery = StringUtils.normalizeSpace(searchQuery);
        model.addAttribute("results", competitionDashboardSearchService.searchCompetitions(trimmedSearchQuery, page - 1));
        model.addAttribute("searchQuery", searchQuery);
        return TEMPLATE_PATH + "search";
    }

    @GetMapping("/competition/create")
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
