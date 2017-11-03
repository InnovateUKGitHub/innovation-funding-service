package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionPendingSpendProfilesResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.populator.PopulatedCompetitionStatusViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionOpenQueriesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionPendingSpendProfilesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;

/**
 * This RestController exposes ways of fetching the current status of a competition projects in a view-friendly
 * format using {@link CompetitionStatusViewModel}
 */
@Controller
@RequestMapping("/competition/{competitionId}/status")
public class CompetitionStatusController {

    @Autowired
    private StatusRestService statusRestService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String viewCompetitionStatus(Model model, UserResource loggedInUser,
                                        @PathVariable Long competitionId) {
        return format("redirect:/competition/%s/status/all", competitionId);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String viewCompetitionStatusAll(Model model, UserResource loggedInUser,
                                           @PathVariable Long competitionId) {

        boolean isUserRoleProjectFinance = loggedInUser.hasRole(UserRoleType.PROJECT_FINANCE);
        //TODO - This call will be replaced with an actual count call after optimisation in the data layer
        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = competitionsRestService.getPendingSpendProfiles(competitionId).getSuccessObjectOrThrowException();

        model.addAttribute("model",
                new PopulatedCompetitionStatusViewModel(statusRestService.getCompetitionStatus(competitionId).getSuccessObjectOrThrowException(),
                        loggedInUser,
                        isUserRoleProjectFinance ? competitionsRestService.getCompetitionOpenQueriesCount(competitionId).getSuccessObjectOrThrowException() : 0L,
                        isUserRoleProjectFinance ? pendingSpendProfiles.size() : 0L,
                        loggedInUser.hasRole(UserRoleType.PROJECT_FINANCE)).get());
        return "project/competition-status-all";
    }

    @GetMapping("/queries")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewCompetitionStatusQueries(Model model, UserResource loggedInUser,
                                              @PathVariable Long competitionId) {

        //TODO - This call will be replaced with an actual count call after optimisation in the data layer
        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = competitionsRestService.getPendingSpendProfiles(competitionId).getSuccessObjectOrThrowException();

        model.addAttribute("model",
                new CompetitionOpenQueriesViewModel(competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException(),
                        competitionsRestService.getCompetitionOpenQueries(competitionId).getSuccessObjectOrThrowException(),
                        competitionsRestService.getCompetitionOpenQueriesCount(competitionId).getSuccessObjectOrThrowException(),
                        pendingSpendProfiles.size(),
                        true));
        return "project/competition-status-queries";
    }

    @GetMapping("/spend-profile")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewPendingSpendProfiles(Model model, UserResource loggedInUser,
                                               @PathVariable Long competitionId) {

        long openQueryCount = competitionsRestService.getCompetitionOpenQueriesCount(competitionId).getSuccessObjectOrThrowException();
        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = competitionsRestService.getPendingSpendProfiles(competitionId).getSuccessObjectOrThrowException();

        model.addAttribute("model",
                new CompetitionPendingSpendProfilesViewModel(competitionsRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException(),
                        pendingSpendProfiles,
                        openQueryCount,
                        pendingSpendProfiles.size(),
                        true));
        return "project/competition-status-spend-profile";
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "EXPORT_BANK_DETAILS", description = "Project finance users should be able export bank details")
    @GetMapping("/bank-details/export")
    public void exportBankDetails(Model model, UserResource loggedInUser,
                                  @PathVariable Long competitionId, HttpServletResponse response) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String filename = String.format("Bank_details_%s_%s.csv", competitionId, ZonedDateTime.now().format(formatter));
        response.setContentType("text/csv");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        final ByteArrayResource resource = bankDetailsRestService.downloadByCompetition(competitionId).getSuccessObjectOrThrowException();
        IOUtils.copy(resource.getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }
}
