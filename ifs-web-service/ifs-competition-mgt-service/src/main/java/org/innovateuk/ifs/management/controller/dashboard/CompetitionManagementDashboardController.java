package org.innovateuk.ifs.management.controller.dashboard;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.service.CompetitionDashboardSearchService;
import org.innovateuk.ifs.management.viewmodel.dashboard.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
public class CompetitionManagementDashboardController {
    public static final String TEMPLATE_PATH = "dashboard/";
    private static final String MODEL_ATTR = "model";

    @Autowired
    private CompetitionDashboardSearchService competitionDashboardSearchService;

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/dashboard/live";
    }

    @GetMapping("/dashboard/live")
    public String live(Model model, UserResource user){
        boolean supportView = user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD);
        Map<CompetitionStatus, List<CompetitionSearchResultItem>> liveCompetitions = competitionDashboardSearchService.getLiveCompetitions();
        model.addAttribute(MODEL_ATTR, new LiveDashboardViewModel(liveCompetitions, getCompetitionCountResource(liveCompetitions), supportView));
        return TEMPLATE_PATH + "live";
    }

    // IFS-191 filtered view can now have different count according to assigned competitions
    private CompetitionCountResource getCompetitionCountResource(Map<CompetitionStatus, List<CompetitionSearchResultItem>> liveCompetitions){
        CompetitionCountResource competitionCountResource = competitionDashboardSearchService.getCompetitionCounts();
        Long competitionCount = 0L;
        if(liveCompetitions != null){
            competitionCount = liveCompetitions.keySet().stream().mapToLong(status -> liveCompetitions.get(status).size()).sum();
        }
        competitionCountResource.setLiveCount(competitionCount);
        return competitionCountResource;
    }

    @GetMapping("/dashboard/project-setup")
    public String projectSetup(Model model, UserResource user) {
        final List<CompetitionSearchResultItem> projectSetupCompetitions = competitionDashboardSearchService.getProjectSetupCompetitions();
        boolean supportView = user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD);
        model.addAttribute(MODEL_ATTR,
                new ProjectSetupDashboardViewModel(projectSetupCompetitions,
                        competitionDashboardSearchService.getCompetitionCounts(), supportView));

        return TEMPLATE_PATH + "projectSetup";
    }

    @GetMapping("/dashboard/upcoming")
    public String upcoming(Model model) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> upcomingCompetitions = competitionDashboardSearchService.getUpcomingCompetitions();

        model.addAttribute(MODEL_ATTR, new UpcomingDashboardViewModel(upcomingCompetitions,
                competitionDashboardSearchService.getCompetitionCounts(),
                formatInnovationAreaNames(upcomingCompetitions)));

        return TEMPLATE_PATH + "upcoming";
    }

    @GetMapping("/dashboard/previous")
    public String previous(Model model, UserResource user) {
        boolean supportView = user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD);
        model.addAttribute(MODEL_ATTR, new PreviousDashboardViewModel(competitionDashboardSearchService.getPreviousCompetitions().stream().sorted((c1, c2) -> c2.getOpenDate().compareTo(c1.getOpenDate())).collect(Collectors.toList()),
                competitionDashboardSearchService.getCompetitionCounts(), supportView));

        return TEMPLATE_PATH + "previous";
    }

    @GetMapping("/dashboard/non-ifs")
    public String nonIfs(Model model) {
        model.addAttribute(MODEL_ATTR, new NonIFSDashboardViewModel(competitionDashboardSearchService.getNonIfsCompetitions(),
                competitionDashboardSearchService.getCompetitionCounts()));

        return TEMPLATE_PATH + "non-ifs";
    }

    @GetMapping("/dashboard/search")
    public String search(@RequestParam(name = "searchQuery", defaultValue = "") String searchQuery,
                         @RequestParam(name = "page", defaultValue = "1") int page, Model model,
                         UserResource user) {
        String trimmedSearchQuery = StringUtils.normalizeSpace(searchQuery);
        model.addAttribute("results", competitionDashboardSearchService.searchCompetitions(trimmedSearchQuery, page - 1));
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("supportView", user.hasRole(UserRoleType.SUPPORT) || user.hasRole(UserRoleType.INNOVATION_LEAD));
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
