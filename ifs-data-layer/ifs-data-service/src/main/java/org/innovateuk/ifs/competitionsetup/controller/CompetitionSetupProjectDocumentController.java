package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupProjectDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/save-all")
    public RestResult<List<ProjectDocumentResource>> save(@RequestBody List<ProjectDocumentResource> projectDocumentResources) {
        return competitionSetupProjectDocumentService.saveAll(projectDocumentResources).toGetResponse();
    }

    @GetMapping("/{id}")
    public RestResult<ProjectDocumentResource> findOne(@PathVariable("id") final long id) {
        return competitionSetupProjectDocumentService.findOne(id).toGetResponse();
    }

    @GetMapping("/findByCompetitionId/{competitionId}")
    public RestResult<List<ProjectDocumentResource>> findByCompetitionId(@PathVariable("competitionId") final long competitionId) {
        return competitionSetupProjectDocumentService.findByCompetitionId(competitionId).toGetResponse();
    }

    @DeleteMapping("/{id}")
    public RestResult<Void> delete(@PathVariable("id") final long id) {
        return competitionSetupProjectDocumentService.delete(id).toGetResponse();
    }
}
