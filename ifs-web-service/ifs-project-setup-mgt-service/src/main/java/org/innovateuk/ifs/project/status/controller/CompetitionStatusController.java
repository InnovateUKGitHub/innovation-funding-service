package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.status.populator.CompetitionStatusViewModelPopulator;
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

/**
 * This RestController exposes ways of fetching the current status of a competition projects in a view-friendly
 * format using {@link CompetitionStatusViewModel}
 */
@Controller
@RequestMapping("/competition/{competitionId}/status")
public class CompetitionStatusController {

    @Autowired
    private BankDetailsRestService bankDetailsRestService;
    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;
    @Autowired
    private CompetitionStatusViewModelPopulator competitionStatusViewModelPopulator;

    private static final String MODEL = "model";

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    public String viewCompetitionStatus(@PathVariable Long competitionId) {
        return format("redirect:/competition/%s/status/all", competitionId);
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    public String viewCompetitionStatusAll(Model model, UserResource loggedInUser,
                                           @PathVariable Long competitionId,
                                           @RequestParam(name = "applicationSearchString", defaultValue = "") String applicationSearchString,
                                           @RequestParam(name = "page", defaultValue = "1") int page) {

        CompetitionStatusViewModel viewModel = competitionStatusViewModelPopulator.populate(loggedInUser, competitionId, applicationSearchString, page - 1);
        model.addAttribute(MODEL, viewModel);

        return "project/competition-status-all";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/queries")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewCompetitionStatusQueries(Model model, UserResource loggedInUser,
                                              @PathVariable Long competitionId) {
        model.addAttribute(MODEL,
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

        model.addAttribute(MODEL,
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
