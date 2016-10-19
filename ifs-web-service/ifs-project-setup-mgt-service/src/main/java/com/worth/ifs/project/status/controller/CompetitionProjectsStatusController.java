package com.worth.ifs.project.status.controller;

import com.worth.ifs.project.sections.ProjectSetupSectionInternalUser;
import com.worth.ifs.project.status.ProjectStatusService;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.viewmodel.CompetitionProjectStatusViewModel;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/competition/{competitionId}/status")
public class CompetitionProjectsStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @RequestMapping(method = GET)
    public String viewCompetitionStatus(
            Model model,
            @ModelAttribute("loggedInUser") UserResource loggedInUser,
            @PathVariable Long competitionId) {

        CompetitionProjectStatusViewModel viewModel = populateCompetitionProjectStatusViewModel(competitionId, loggedInUser);

        model.addAttribute("model", viewModel);
        return "project/competition-status";
    }

    public CompetitionProjectStatusViewModel populateCompetitionProjectStatusViewModel(Long competitionId, UserResource userResource) {
        CompetitionProjectsStatusResource competitionProjectsStatusResource = projectStatusService.getCompetitionStatus(competitionId);

        Map<Long, ProjectStatusPermission> projectStatusPermissionMap = new HashMap<>();

        if(null == competitionProjectsStatusResource.getProjectStatusResources()) {
            return new CompetitionProjectStatusViewModel(
                    competitionProjectsStatusResource, projectStatusPermissionMap
            );
        }

        competitionProjectsStatusResource.getProjectStatusResources().forEach(projectStatusResource -> {
            ProjectSetupSectionInternalUser internalUser = new ProjectSetupSectionInternalUser(projectStatusResource);

            ProjectStatusPermission projectStatusPermission = new ProjectStatusPermission(
                    internalUser.canAccessCompaniesHouseSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessProjectDetailsSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessMonitoringOfficerSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessBankDetailsSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessFinanceChecksSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessSpendProfileSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessOtherDocumentsSection(userResource).isAccessibleOrNotRequired(),
                    internalUser.canAccessGrantOfferLetterSection(userResource).isAccessibleOrNotRequired());

            projectStatusPermissionMap.put(projectStatusResource.getApplicationNumber(), projectStatusPermission);
        });

        CompetitionProjectStatusViewModel competitionProjectStatusViewModel = new CompetitionProjectStatusViewModel(
                competitionProjectsStatusResource, projectStatusPermissionMap
        );

        return competitionProjectStatusViewModel;
    }


}
