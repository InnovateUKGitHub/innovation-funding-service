package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.project.documents.populator.DocumentsPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller backing the Documents page
 */
@Controller
@RequestMapping("/project/{projectId}/document")
public class DocumentsController {

    @Autowired
    DocumentsPopulator populator;

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/all")
    public String viewAllDocuments(@PathVariable("projectId") Long projectId, Model model,
                                   UserResource loggedInUser) {

        model.addAttribute("model", populator.populate(projectId));
        return "project/documents-all"; // TODO - XXX - Create this view please.
    }
}


