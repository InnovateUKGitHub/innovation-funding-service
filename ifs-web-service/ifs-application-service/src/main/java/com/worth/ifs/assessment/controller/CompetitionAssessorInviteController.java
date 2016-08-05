package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.viewmodel.CompetitionAssessorInviteViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for invites to CompetitionAssessors.
 */
@Controller
@RequestMapping("/invite")
public class CompetitionAssessorInviteController {

    @RequestMapping(value = "competition/{hash}", method = RequestMethod.GET)
    public String accessInvite(@PathVariable String hash, HttpServletResponse response, HttpServletRequest request, Model model) {
        model.addAttribute("model", new CompetitionAssessorInviteViewModel("Competition Name"));
        return "access-competition-invite";
    }

}