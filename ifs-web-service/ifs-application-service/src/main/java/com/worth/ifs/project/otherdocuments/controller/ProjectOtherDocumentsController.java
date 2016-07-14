package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Arrays.asList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller backing the Other Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/other-documents")
public class ProjectOtherDocumentsController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = GET)
    public String viewMonitoringOfficer(@PathVariable("projectId") Long projectId, Model model) {

        ProjectOtherDocumentsViewModel viewModel = getOtherDocumentsViewModel(projectId);
        model.addAttribute("model", viewModel);
        return "project/other-documents";
    }

    private ProjectOtherDocumentsViewModel getOtherDocumentsViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        return new ProjectOtherDocumentsViewModel(projectId, project.getName(),
                new FileDetailsViewModel("file1.pdf", 1005), new FileDetailsViewModel("file2.pdf", 2534),
                false,
                asList("Partner Org 1", "Partner Org 2", "Partner Org 3"));
    }
}
