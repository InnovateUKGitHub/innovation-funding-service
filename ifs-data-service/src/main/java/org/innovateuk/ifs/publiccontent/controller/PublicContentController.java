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
@RequestMapping("/public-content")
public class PublicContentController {

    @Autowired
    private PublicContentService publicContentService;

    @RequestMapping(value = "find-by-competition-id/{competitionId}", method = RequestMethod.GET)
    public RestResult<PublicContentResource> getCompetitionById(@PathVariable("competitionId") final Long competitionId) {
        return publicContentService.findByCompetitionId(competitionId).toGetResponse();
    }

    @RequestMapping(value = "publish-by-competition-id/{competitionId}", method = RequestMethod.POST)
    public RestResult<Void> publishByCompetition(@PathVariable("competitionId") final Long competitionId) {
        return publicContentService.publishByCompetitionId(competitionId).toPostResponse();
    }

    @RequestMapping(value = "update-section/{section}/{id}", method = RequestMethod.POST)
    public RestResult<Void> updateSection(@PathVariable("id") final Long id,
                                          @PathVariable("section") final PublicContentSectionType section,
                                          @RequestBody final PublicContentResource resource) {
        return publicContentService.updateSection(resource, section).toPostResponse();
    }

    @RequestMapping(value = "mark-section-as-complete/{section}/{id}", method = RequestMethod.POST)
    public RestResult<Void> markSectionAsComplete(@PathVariable("id") final Long id,
                                          @PathVariable("section") final PublicContentSectionType section,
                                          @RequestBody final PublicContentResource resource) {
        return publicContentService.markSectionAsComplete(resource, section).toPostResponse();
    }
}
