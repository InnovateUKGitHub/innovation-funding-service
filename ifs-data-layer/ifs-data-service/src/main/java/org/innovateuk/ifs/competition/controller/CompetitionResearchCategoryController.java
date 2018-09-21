package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.innovateuk.ifs.competition.transactional.CompetitionResearchCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/competition-research-category")
public class CompetitionResearchCategoryController {

    private CompetitionResearchCategoryService competitionResearchCategoryService;

    public CompetitionResearchCategoryController(CompetitionResearchCategoryService competitionResearchCategoryService) {
        this.competitionResearchCategoryService = competitionResearchCategoryService;
    }

    @GetMapping("/{id}")
    public RestResult<List<CompetitionResearchCategoryLinkResource>> findByCompetition(@PathVariable("id") final long id) {
        return competitionResearchCategoryService.findByCompetition(id).toGetResponse();
    }
}
