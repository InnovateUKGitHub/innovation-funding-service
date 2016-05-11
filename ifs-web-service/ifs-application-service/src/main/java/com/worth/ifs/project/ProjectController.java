package com.worth.ifs.project;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {

    @RequestMapping("/{projectId}")
    public String projectDetails(Model model, @PathVariable("projectId") final Long projectId,
                                     HttpServletRequest request) {
        return "project/details";
    }
}
