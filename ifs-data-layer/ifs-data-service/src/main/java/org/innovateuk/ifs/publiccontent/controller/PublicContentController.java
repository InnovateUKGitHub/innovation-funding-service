package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for all public content actions.
 */
@RestController
@RequestMapping("/public-content/")
public class PublicContentController {

    @Autowired
    private PublicContentService publicContentService;

    @GetMapping("find-by-competition-id/{competitionId}")
    public RestResult<PublicContentResource> getCompetitionById(@PathVariable("competitionId") final Long competitionId) {
        return publicContentService.findByCompetitionId(competitionId).toGetResponse();
    }

    @PostMapping("publish-by-competition-id/{competitionId}")
    public RestResult<Void> publishByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return publicContentService.publishByCompetitionId(competitionId).toPostResponse();
    }

    @PostMapping("update-section/{section}/{id}")
    public RestResult<Void> updateSection(@PathVariable("id") final Long id,
                                          @PathVariable("section") final PublicContentSectionType section,
                                          @RequestBody final PublicContentResource resource) {
        return publicContentService.updateSection(resource, section).toPostResponse();
    }

    @PostMapping("mark-section-as-complete/{section}/{id}")
    public RestResult<Void> markSectionAsComplete(@PathVariable("id") final Long id,
                                          @PathVariable("section") final PublicContentSectionType section,
                                          @RequestBody final PublicContentResource resource) {
        return publicContentService.markSectionAsComplete(resource, section).toPostResponse();
    }
}
