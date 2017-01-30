package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all public content actions.
 */
@RestController
@RequestMapping("/public-content")
public class PublicContentController {

    @Autowired
    private PublicContentService publicContentService;

    @RequestMapping(value = "find-by-competition-id/{id}", method = RequestMethod.GET)
    public RestResult<PublicContentResource> getCompetitionById(@PathVariable("id") final Long competitionId) {
        return publicContentService.findByCompetitionId(competitionId).toGetResponse();
    }

    @RequestMapping(value = "publish-by-competition-id/{id}", method = RequestMethod.POST)
    public RestResult<Void> publishByCompetition(@PathVariable("id") final Long competitionId) {
        return publicContentService.publishByCompetitionId(competitionId).toPostResponse();
    }
}
