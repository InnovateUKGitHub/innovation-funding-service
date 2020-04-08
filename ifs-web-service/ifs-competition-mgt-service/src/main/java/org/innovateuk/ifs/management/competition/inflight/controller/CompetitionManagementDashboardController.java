package org.innovateuk.ifs.management.competition.inflight.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.management.dashboard.service.CompetitionDashboardSearchService;
import org.innovateuk.ifs.management.dashboard.viewmodel.*;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.innovateuk.ifs.user.resource.Role.COMPETITION_FINANCE;

@Controller
public class CompetitionManagementDashboardController {
    private static final String TEMPLATE_PATH = "dashboard/";
    private static final String MODEL_ATTR = "model";

    private static final String DEFAULT_PAGE = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    private CompetitionDashboardSearchService competitionDashboardSearchService;

    private CompetitionSetupRestService competitionSetupRestService;

    private BankDetailsRestService bankDetailsRestService;

    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    public CompetitionManagementDashboardController(CompetitionDashboardSearchService competitionDashboardSearchService,
                                                    CompetitionSetupRestService competitionSetupRestService,
                                                    BankDetailsRestService bankDetailsRestService,
                                                    CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService) {
        this.competitionDashboardSearchService = competitionDashboardSearchService;
        this.competitionSetupRestService = competitionSetupRestService;
        this.bankDetailsRestService = bankDetailsRestService;
        this.competitionSetupStakeholderRestService = competitionSetupStakeholderRestService;
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance," +
            " support, innovation lead and stakeholder roles are allowed to view the competition management dashboard")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'comp_finance')")
    @GetMapping("/dashboard")
    public String dashboard(UserResource user) {
        if (user.hasRole(COMPETITION_FINANCE)) {
            return "redirect:/dashboard/project-setup";
        }
        return "redirect:/dashboard/live";
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance," +
            " support, innovation lead and stakeholder roles are allowed to view the list of live competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/dashboard/live")
    @NavigationRoot
    public String live(Model model, UserResource user) {
        Map<CompetitionStatus, List<CompetitionSearchResultItem>> liveCompetitions = competitionDashboardSearchService.getLiveCompetitions();
        model.addAttribute(MODEL_ATTR, new LiveDashboardViewModel(
                liveCompetitions,
                competitionDashboardSearchService.getCompetitionCounts(),
                new DashboardTabsViewModel(user)));
        return TEMPLATE_PATH + "live";
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance," +
            " support, innovation lead and stakeholder roles are allowed to view the list of competitions in project setup")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'comp_finance')")
    @GetMapping("/dashboard/project-setup")
    @NavigationRoot
    public String projectSetup(@RequestParam(defaultValue = DEFAULT_PAGE) int page, Model model, UserResource user) {
        CompetitionSearchResult searchResult = competitionDashboardSearchService.getProjectSetupCompetitions(page);

        Long countBankDetails = 0L;
        boolean projectFinanceUser = isProjectFinanceUser(user);
        if (projectFinanceUser) {
            countBankDetails = bankDetailsRestService.countPendingBankDetailsApprovals().getSuccess();
        }

        model.addAttribute(MODEL_ATTR,
                new ProjectSetupDashboardViewModel(
                        searchResult,
                        competitionDashboardSearchService.getCompetitionCounts(),
                        countBankDetails,
                        new DashboardTabsViewModel(user),
                        projectFinanceUser));

        return TEMPLATE_PATH + "projectSetup";
    }

    private boolean isProjectFinanceUser(UserResource user) {
        return SecurityRuleUtil.isProjectFinanceUser(user);
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance, and support roles are allowed to view the list of upcoming competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support')")
    @GetMapping("/dashboard/upcoming")
    @NavigationRoot
    public String upcoming(Model model, UserResource user) {
        final Map<CompetitionStatus, List<CompetitionSearchResultItem>> upcomingCompetitions = competitionDashboardSearchService.getUpcomingCompetitions();

        model.addAttribute(MODEL_ATTR, new UpcomingDashboardViewModel(upcomingCompetitions,
                competitionDashboardSearchService.getCompetitionCounts(), new DashboardTabsViewModel(user)));

        return TEMPLATE_PATH + "upcoming";
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance," +
            " support, innovation lead and stakeholder roles are allowed to view the list of previous competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'comp_finance')")
    @GetMapping("/dashboard/previous")
    @NavigationRoot
    public String previous(@RequestParam(defaultValue = DEFAULT_PAGE) int page, Model model, UserResource user) {
        model.addAttribute(MODEL_ATTR, new PreviousDashboardViewModel(
                competitionDashboardSearchService.getPreviousCompetitions(page),
                competitionDashboardSearchService.getCompetitionCounts(),
                new DashboardTabsViewModel(user)));

        return TEMPLATE_PATH + "previous";
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance, and support roles are allowed to view the list of non-IFS competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support')")
    @GetMapping("/dashboard/non-ifs")
    @NavigationRoot
    public String nonIfs(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
            Model model, UserResource user) {
        model.addAttribute(MODEL_ATTR, new NonIfsDashboardViewModel(competitionDashboardSearchService.getNonIfsCompetitions(page), competitionDashboardSearchService.getCompetitionCounts(), new DashboardTabsViewModel(user)));
        return TEMPLATE_PATH + "non-ifs";
    }

    @SecuredBySpring(value = "READ", description = "The competition admin, project finance, " +
            "innovation lead, stakeholder, ifs admin and support users are allowed to view the application and competition search pages")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder', 'ifs_administrator')")
    @GetMapping("/dashboard/internal/search")
    public String internalSearch(@RequestParam(name = "searchQuery", defaultValue = "") String searchQuery,
                                 @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                 Model model,
                                 HttpServletRequest request,
                                 UserResource user) {
        String trimmedSearchQuery = StringUtils.normalizeSpace(searchQuery);
        boolean isSearchNumeric = trimmedSearchQuery.chars().allMatch(Character::isDigit);

        if (isSearchNumeric && !trimmedSearchQuery.isEmpty()) {
            return searchApplication(trimmedSearchQuery, page, pageSize, model, request, user);
        } else {
            return searchCompetition(trimmedSearchQuery, page, model, user);
        }
    }

    @SecuredBySpring(value = "READ", description = "The competition admin and project finance roles are allowed to view the page for setting up new competitions")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @GetMapping("/competition/create")
    public String create() {
        CompetitionResource competition = competitionSetupRestService.create().getSuccess();
        return String.format("redirect:/competition/setup/%s", competition.getId());
    }

    private String searchCompetition(String searchQuery, int page, Model model, UserResource user) {
        model.addAttribute(MODEL_ATTR,
                new CompetitionSearchDashboardViewModel(
                        competitionDashboardSearchService.searchCompetitions(searchQuery, page),
                        searchQuery,
                        user));
        return TEMPLATE_PATH + "search";
    }

    private String searchApplication(String searchQuery, int page, int pageSize, Model model, HttpServletRequest request, UserResource user) {
        String existingSearchQuery = Objects.toString(request.getQueryString(), "");

        ApplicationPageResource matchedApplications = competitionDashboardSearchService.wildcardSearchByApplicationId(searchQuery, page, pageSize);

        ApplicationSearchDashboardViewModel viewModel =
                new ApplicationSearchDashboardViewModel(matchedApplications.getContent(),
                        matchedApplications.getTotalElements(),
                        new Pagination(matchedApplications, "search?" + existingSearchQuery),
                        searchQuery);

        model.addAttribute("model", viewModel);

        return TEMPLATE_PATH + "application-search";
    }
}