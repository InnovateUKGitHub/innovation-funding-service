package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.status.populator.PopulatedCompetitionStatusViewModel;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionOpenQueriesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionPendingSpendProfilesViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

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
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead')")
    public String viewCompetitionStatus(@PathVariable Long competitionId) {
        return format("redirect:/competition/%s/status/all", competitionId);
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead')")
    public String viewCompetitionStatusAll(Model model, UserResource loggedInUser,
                                           @PathVariable Long competitionId,
                                           @RequestParam(name = "applicationSearchString", defaultValue = "") String applicationSearchString) {

        boolean isUserRoleProjectFinance = loggedInUser.hasRole(PROJECT_FINANCE);

        model.addAttribute("model",
                new PopulatedCompetitionStatusViewModel(statusRestService.getCompetitionStatus(competitionId, StringUtils.trim(applicationSearchString)).getSuccess(),
                        loggedInUser,
                        isUserRoleProjectFinance ? competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess() : 0L,
                        isUserRoleProjectFinance ? competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId).getSuccess() : 0,
                        isUserRoleProjectFinance).get());
        return "project/competition-status-all";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/queries")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewCompetitionStatusQueries(Model model, UserResource loggedInUser,
                                              @PathVariable Long competitionId) {
        model.addAttribute("model",
                new CompetitionOpenQueriesViewModel(competitionRestService.getCompetitionById(competitionId).getSuccess(),
                        competitionPostSubmissionRestService.getCompetitionOpenQueries(competitionId).getSuccess(),
                        competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess(),
                        competitionPostSubmissionRestService.countPendingSpendProfiles(competitionId).getSuccess(),
                        true));
        return "project/competition-status-queries";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/pending-spend-profiles")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewPendingSpendProfiles(Model model, UserResource loggedInUser,
                                           @PathVariable Long competitionId) {

        long openQueryCount = competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccess();
        List<SpendProfileStatusResource> pendingSpendProfiles = competitionPostSubmissionRestService.getPendingSpendProfiles(competitionId).getSuccess();

        model.addAttribute("model",
                new CompetitionPendingSpendProfilesViewModel(competitionRestService.getCompetitionById(competitionId).getSuccess(),
                        pendingSpendProfiles,
                        openQueryCount,
                        pendingSpendProfiles.size(),
                        true));
        return "project/competition-pending-spend-profiles";
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
        final ByteArrayResource resource = bankDetailsRestService.downloadByCompetition(competitionId).getSuccess();
        IOUtils.copy(resource.getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }
}
