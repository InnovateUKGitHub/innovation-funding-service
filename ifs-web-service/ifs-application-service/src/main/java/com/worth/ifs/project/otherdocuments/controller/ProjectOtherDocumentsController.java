package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/other-documents")
public class ProjectOtherDocumentsController {

    @RequestMapping(method = GET)
    public String viewMonitoringOfficer(@PathVariable("projectId") Long projectId, Model model) {

        ProjectOtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(projectId);
        model.addAttribute("model", viewModel);
        return "project/other-documents";
    }

    private ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId) {
        return new ProjectOtherDocumentsViewModel(projectId);
    }
}
