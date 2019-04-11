package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * SectionController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/section")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @GetMapping("/{sectionId}")
    public RestResult<SectionResource> getById(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getById(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getNextSection/{sectionId}", "/get-next-section/{sectionId}"})
    public RestResult<SectionResource> getNextSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getNextSection(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getPreviousSection/{sectionId}", "/get-previous-section/{sectionId}"})
    public RestResult<SectionResource> getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getPreviousSection(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getSectionByQuestionId/{questionId}", "/get-section-by-question-id/{questionId}"})
    public RestResult<SectionResource> getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionService.getSectionByQuestionId(questionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getQuestionsForSectionAndSubsections/{sectionId}", "/get-questions-for-section-and-subsections/{sectionId}"})
    public RestResult<Set<Long>> getQuestionsForSectionAndSubsections(@PathVariable("sectionId") final Long sectionId){
        return sectionService.getQuestionsForSectionAndSubsections(sectionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getSectionsByCompetitionIdAndType/{competitionId}/{type}", "/get-sections-by-competition-id-and-type/{competitionId}/{type}"})
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(@PathVariable("competitionId") final Long competitionId, @PathVariable("type") SectionType type) {
        return sectionService.getSectionsByCompetitionIdAndType(competitionId, type).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getByCompetition/{competitionId}", "/get-by-competition/{competitionId}"})
    public RestResult<List<SectionResource>> getSectionsByCompetitionId(@PathVariable("competitionId") final Long competitionId) {
        return sectionService.getByCompetitionId(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getByCompetitionIdVisibleForAssessment/{competitionId}", "/get-by-competition-id-visible-for-assessment/{competitionId}"})
    public RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(@PathVariable("competitionId") long competitionId) {
        return sectionService.getByCompetitionIdVisibleForAssessment(competitionId).toGetResponse();
    }
}
