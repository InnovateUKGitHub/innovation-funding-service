package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController.ApplicationOverviewOrigin;
import org.innovateuk.ifs.management.model.AllApplicationsPageModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.model.SubmittedApplicationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController.buildOriginQueryString;

/**
 * This controller will handle all requests that are related to the applications of a Competition within Competition Management.
 */
@Controller
@RequestMapping("/competition/{competitionId}/applications")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementApplicationsController {

    @Autowired
	private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @Autowired
    private AllApplicationsPageModelPopulator allApplicationsPageModelPopulator;

    @Autowired
    private SubmittedApplicationsModelPopulator submittedApplicationsModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationsMenu(Model model, @PathVariable("competitionId") long competitionId) {
		model.addAttribute("model", applicationsMenuModelPopulator.populateModel(competitionId));
		return "competition/applications-menu";
	}

	@RequestMapping(path = "/all", method = RequestMethod.GET)
	public String allApplications(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam MultiValueMap<String, String> queryParams) {
        model.addAttribute("model", allApplicationsPageModelPopulator.populateModel(competitionId));
        model.addAttribute("originQuery", buildOriginQueryString(ApplicationOverviewOrigin.ALL_APPLICATIONS, queryParams));

        return "competition/all-applications";
    }

    @RequestMapping(path = "/submitted", method = RequestMethod.GET)
    public String submittedApplications(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam MultiValueMap<String, String> queryParams) {
        model.addAttribute("model", submittedApplicationsModelPopulator.populateModel(competitionId));
        model.addAttribute("originQuery", buildOriginQueryString(ApplicationOverviewOrigin.SUBMITTED_APPLICATIONS, queryParams));

        return "competition/submitted-applications";
    }
}
