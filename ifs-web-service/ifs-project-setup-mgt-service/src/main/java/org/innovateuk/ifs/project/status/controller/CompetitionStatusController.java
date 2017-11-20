package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.status.populator.PopulatedCompetitionStatusViewModel;
import org.innovateuk.ifs.project.status.service.StatusRestService;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionOpenQueriesViewModel;
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
    private CompetitionRestService CompetitionRestService;

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
                                           @PathVariable Long competitionId) {
        model.addAttribute("model",
                new PopulatedCompetitionStatusViewModel(statusRestService.getCompetitionStatus(competitionId).getSuccessObjectOrThrowException(),
                        loggedInUser,
                        loggedInUser.hasRole(UserRoleType.PROJECT_FINANCE) ? competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccessObjectOrThrowException() : 0L,
                        loggedInUser.hasRole(UserRoleType.PROJECT_FINANCE)).get());
        return "project/competition-status-all";
    }

    @SecuredBySpring(value = "TODO", description = "TODO")
    @GetMapping("/queries")
    @PreAuthorize("hasAnyAuthority('project_finance')")
    public String viewCompetitionStatusQueries(Model model, UserResource loggedInUser,
                                              @PathVariable Long competitionId) {
        model.addAttribute("model",
                new CompetitionOpenQueriesViewModel(CompetitionRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException(),
                        competitionPostSubmissionRestService.getCompetitionOpenQueries(competitionId).getSuccessObjectOrThrowException(),
                        competitionPostSubmissionRestService.getCompetitionOpenQueriesCount(competitionId).getSuccessObjectOrThrowException(),
                        true));
        return "project/competition-status-queries";
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
