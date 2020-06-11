package org.innovateuk.ifs.project.correspondenceaddress.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/details/project-address")
@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_ADDRESS_PAGE')")
@SecuredBySpring(value = "Controller", description = "A lead can access the project address in project setup stage",  securedType = ProjectCorrespondenceAddressController.class)
public class ProjectCorrespondenceAddressController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public String viewPage(@PathVariable long projectId) {

        ProjectResource project = projectService.getById(projectId);
        return redirectToViewPage(projectId, isLeadInternational(projectId));

    }

    private String redirectToViewPage(long projectId, boolean isLeadInternationalOrganisation) {
        return "redirect:" +
                String.format("/project/%d/details/project-address/%s",
                        projectId,
                        isLeadInternationalOrganisation ? "international" : "UK");
    }

    private boolean isLeadInternational(long projectId) {
        return projectService.getLeadOrganisation(projectId).isInternational();
    }

}


