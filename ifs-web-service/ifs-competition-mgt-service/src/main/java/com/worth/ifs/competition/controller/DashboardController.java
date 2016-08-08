package com.worth.ifs.competition.controller;

import com.worth.ifs.application.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    public static final String TEMPLATE_PATH = "competition/";

    @Autowired
    CompetitionService competitionService;

    @RequestMapping(value="", method= RequestMethod.GET)
    public String dashboard() {
        return "redirect:/dashboard/live";
    }

    @RequestMapping(value="/live", method= RequestMethod.GET)
    public String live(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getLiveCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "live";
    }

    @RequestMapping(value="/projectSetup", method= RequestMethod.GET)
    public String projectSetup(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getProjectSetupCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "projectSetup";
    }

    @RequestMapping(value="/upcoming", method= RequestMethod.GET)
    public String upcoming(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getUpcomingCompetitions());
        model.addAttribute("counts", competitionService.getCompetitionCounts());
        return TEMPLATE_PATH + "upcoming";
    }

    @RequestMapping(value="/search", method= RequestMethod.GET)
    public String upcoming(@RequestParam(name = "searchQuery", required = true) String searchQuery,
                           Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.searchCompetitions(searchQuery));
        return TEMPLATE_PATH + "search";
    }

}
