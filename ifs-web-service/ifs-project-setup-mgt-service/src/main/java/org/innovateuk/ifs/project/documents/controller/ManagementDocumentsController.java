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
//TODO - XXX - Rename this class back to normal
public class ManagementDocumentsController {

    @Autowired
    DocumentsPopulator populator;

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/all")
    public String viewAllDocuments(@PathVariable("projectId") long projectId, Model model,
                                   UserResource loggedInUser) {

        model.addAttribute("model", populator.populateAllDocuments(projectId));
        return "project/documents-all";
    }

    //TODO - XXX - Permissions
    //@PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_OTHER_DOCUMENTS_SECTION')")
    @GetMapping("/config/{documentConfigId}")
    public String viewDocument(@PathVariable("projectId") long projectId,
                               @PathVariable("documentConfigId") long documentConfigId,
                               Model model,
                               UserResource loggedInUser) {

        return doViewDocument(projectId, documentConfigId, model);
    }

    private String doViewDocument(long projectId, long documentConfigId, Model model) {

        model.addAttribute("model", populator.populateViewDocument(projectId, documentConfigId));
        return "project/document";
    }

}
