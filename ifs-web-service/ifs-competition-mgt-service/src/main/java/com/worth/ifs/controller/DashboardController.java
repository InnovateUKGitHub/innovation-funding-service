package com.worth.ifs.controller;

import com.worth.ifs.application.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {

    @Autowired
    CompetitionService competitionService;

    @RequestMapping(value="/dashboard", method= RequestMethod.GET)
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("competitions", competitionService.getAllCompetitions());
        return "competition/list";
    }
}
