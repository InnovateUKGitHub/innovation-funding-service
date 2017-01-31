package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all requests that are related to the applications of a Competition within Competition Management.
 */
@Controller
@RequestMapping("/competition/{competitionId}/applications")
public class CompetitionManagementApplicationsController {
    
    @Autowired
	private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String applicationsMenu(Model model, @PathVariable("competitionId") Long competitionId) {
		model.addAttribute("model", applicationsMenuModelPopulator.populateModel(competitionId));
		return "competition/applications-menu";
	}
}
