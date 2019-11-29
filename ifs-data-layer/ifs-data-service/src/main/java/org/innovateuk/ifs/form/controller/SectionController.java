package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-child-sections/{parentId}")
    public RestResult<List<SectionResource>> getChildSectionsByParentId(@PathVariable("parentId") final Long parentId) {
        return sectionService.getChildSectionsByParentId(parentId).toGetResponse();
    }

    @GetMapping("/get-next-section/{sectionId}")
    public RestResult<SectionResource> getNextSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getNextSection(sectionId).toGetResponse();
    }

    @GetMapping("/get-previous-section/{sectionId}")
    public RestResult<SectionResource> getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getPreviousSection(sectionId).toGetResponse();
    }

    @GetMapping("/get-section-by-question-id/{questionId}")
    public RestResult<SectionResource> getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionService.getSectionByQuestionId(questionId).toGetResponse();
    }

    @GetMapping("/get-questions-for-section-and-subsections/{sectionId}")
    public RestResult<Set<Long>> getQuestionsForSectionAndSubsections(@PathVariable("sectionId") final Long sectionId){
        return sectionService.getQuestionsForSectionAndSubsections(sectionId).toGetResponse();
    }

    @GetMapping("/get-sections-by-competition-id-and-type/{competitionId}/{type}")
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(@PathVariable("competitionId") final Long competitionId, @PathVariable("type") SectionType type) {
        return sectionService.getSectionsByCompetitionIdAndType(competitionId, type).toGetResponse();
    }

    @GetMapping("/get-by-competition/{competitionId}")
    public RestResult<List<SectionResource>> getSectionsByCompetitionId(@PathVariable("competitionId") final Long competitionId) {
        return sectionService.getByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/get-by-competition-id-visible-for-assessment/{competitionId}")
    public RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(@PathVariable("competitionId") long competitionId) {
        return sectionService.getByCompetitionIdVisibleForAssessment(competitionId).toGetResponse();
    }
}
