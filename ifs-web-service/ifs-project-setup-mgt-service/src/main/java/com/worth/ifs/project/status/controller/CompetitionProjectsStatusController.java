package com.worth.ifs.project.status.controller;

import com.worth.ifs.project.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/competition/{competitionId}/status")
public class CompetitionProjectsStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @RequestMapping(method = GET)
    public String viewCompetitionStatus(
            Model model,
            @PathVariable Long competitionId) {
        CompetitionProjectsStatusResource competitionProjectsStatusResource = projectStatusService.getCompetitionStatus(competitionId);
        model.addAttribute("model", competitionProjectsStatusResource);
        return "project/competition-status";
    }
}
