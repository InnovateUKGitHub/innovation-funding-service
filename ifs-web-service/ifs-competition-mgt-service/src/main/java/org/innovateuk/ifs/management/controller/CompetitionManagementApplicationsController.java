package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AllApplicationsPageModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.model.SubmittedApplicationsModelPopulator;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private CookieUtil cookieUtil;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationsMenu(Model model, @PathVariable("competitionId") long competitionId) {
		model.addAttribute("model", applicationsMenuModelPopulator.populateModel(competitionId));
		return "competition/applications-menu";
	}

	@RequestMapping(path = "/all", method = RequestMethod.GET)
	public String allApplications(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        model.addAttribute("model", allApplicationsPageModelPopulator.populateModel(competitionId));

        saveRequestUrlToCookie(request, response);

        return "competition/all-applications";
    }

    @RequestMapping(path = "/submitted", method = RequestMethod.GET)
    public String submittedApplications(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        model.addAttribute("model", submittedApplicationsModelPopulator.populateModel(competitionId));

        saveRequestUrlToCookie(request, response);

        return "competition/submitted-applications";
    }

    private void saveRequestUrlToCookie(HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.saveToCookie(
                response,
                CompetitionManagementApplicationController.APPLICATION_OVERVIEW_ORIGIN_URL_KEY,
                HttpUtils.getFullRequestUrl(request)
        );
    }
}
