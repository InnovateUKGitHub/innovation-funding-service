package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.io.IOUtils;
import org.innovateuk.ifs.bankdetails.BankDetailsService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionInternalUser;
import org.innovateuk.ifs.project.status.ProjectStatusService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.CollectionFunctions;
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
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
        model.addAttribute("model", populateCompetitionProjectStatusViewModel(competitionId, loggedInUser));
        return "project/competition-status";
    }

    @PreAuthorize("hasAuthority('project_finance')")
    @SecuredBySpring(value = "EXPORT_BANK_DETAILS", description = "Project finance users should be able export bank details")
    @RequestMapping(value = "/bank-details/export", method = GET)
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

    private CompetitionProjectStatusViewModel populateCompetitionProjectStatusViewModel(Long competitionId, UserResource userResource) {
        CompetitionProjectsStatusResource competitionProjectsStatus = projectStatusService.getCompetitionStatus(competitionId);
        Map<Long, ProjectStatusPermission> projectStatusPermissionMap
                = projectStatusPermissions(userResource, competitionProjectsStatus.getProjectStatusResources());
        boolean canExportBankDetails = userResource.hasRole(UserRoleType.PROJECT_FINANCE);
        return new CompetitionProjectStatusViewModel(competitionProjectsStatus, canExportBankDetails, projectStatusPermissionMap);
    }

    private Map<Long, ProjectStatusPermission> projectStatusPermissions(UserResource user, List<ProjectStatusResource> projectStatuses) {
        return CollectionFunctions.simpleToLinkedMap(projectStatuses,
                ProjectStatusResource::getApplicationNumber,
                projectStatus -> projectStatusPermission(new ProjectSetupSectionInternalUser(projectStatus), user));
    }

    private ProjectStatusPermission projectStatusPermission(ProjectSetupSectionInternalUser internalUser,
                                                            UserResource userResource) {
        return new ProjectStatusPermission(
                internalUser.canAccessCompaniesHouseSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessProjectDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessMonitoringOfficerSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessBankDetailsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessFinanceChecksSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessSpendProfileSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessOtherDocumentsSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSection(userResource).isAccessibleOrNotRequired(),
                internalUser.canAccessGrantOfferLetterSendSection(userResource).isAccessibleOrNotRequired(),
                internalUser.grantOfferLetterActivityStatus(userResource));
    }
}
