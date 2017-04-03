package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.innovateuk.ifs.bankdetails.BankDetailsService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.status.ProjectStatusService;
import org.innovateuk.ifs.project.status.populator.PopulatedCompetitionProjectsStatusViewModel;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This RestController exposes ways of fetching the current status of a competition projects in a view-friendly
 * format  using {@link CompetitionProjectStatusViewModel}
 */
@Controller
@RequestMapping("/competition/{competitionId}/status")
public class CompetitionProjectsStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @Autowired
    private BankDetailsService bankDetailsService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin')")
    public String viewCompetitionStatus(Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                        @PathVariable Long competitionId) {
        model.addAttribute("model",
                new PopulatedCompetitionProjectsStatusViewModel(projectStatusService.getCompetitionStatus(competitionId), loggedInUser).get());
        return "project/competition-status";
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "EXPORT_BANK_DETAILS", description = "Project finance users should be able export bank details")
    @GetMapping("/bank-details/export")
    public void exportBankDetails(Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                  @PathVariable Long competitionId, HttpServletResponse response) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String filename = String.format("Bank_details_%s_%s.csv", competitionId, LocalDateTime.now().format(formatter));
        response.setContentType("text/csv");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        final ByteArrayResource resource = bankDetailsService.downloadByCompetition(competitionId);
        IOUtils.copy(resource.getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }
}
