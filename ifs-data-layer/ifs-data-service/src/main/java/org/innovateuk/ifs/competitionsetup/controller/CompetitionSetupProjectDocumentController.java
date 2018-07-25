package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupProjectDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling project documents during competition setup
 */
@RestController
@RequestMapping("/competition/setup/project-document")
public class CompetitionSetupProjectDocumentController {

    @Autowired
    private CompetitionSetupProjectDocumentService competitionSetupProjectDocumentService;

    @PostMapping("/save")
    public RestResult<ProjectDocumentResource> save(@RequestBody ProjectDocumentResource projectDocumentResource) {
        return competitionSetupProjectDocumentService.save(projectDocumentResource).toGetResponse();
    }
}
