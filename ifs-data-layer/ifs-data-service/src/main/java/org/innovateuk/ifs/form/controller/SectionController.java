package org.innovateuk.ifs.form.controller;

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

    @GetMapping("/getNextSection/{sectionId}")
    public RestResult<SectionResource> getNextSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getNextSection(sectionId).toGetResponse();
    }

    @GetMapping("/getPreviousSection/{sectionId}")
    public RestResult<SectionResource> getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getPreviousSection(sectionId).toGetResponse();
    }

    @GetMapping("/getSectionByQuestionId/{questionId}")
    public RestResult<SectionResource> getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionService.getSectionByQuestionId(questionId).toGetResponse();
    }

    @GetMapping("/getQuestionsForSectionAndSubsections/{sectionId}")
    public RestResult<Set<Long>> getQuestionsForSectionAndSubsections(@PathVariable("sectionId") final Long sectionId){
        return sectionService.getQuestionsForSectionAndSubsections(sectionId).toGetResponse();
    }

    @GetMapping("/getSectionsByCompetitionIdAndType/{competitionId}/{type}")
    public RestResult<List<SectionResource>> getSectionsByCompetitionIdAndType(@PathVariable("competitionId") final Long competitionId, @PathVariable("type") SectionType type) {
        return sectionService.getSectionsByCompetitionIdAndType(competitionId, type).toGetResponse();
    }

    @GetMapping("/getByCompetition/{competitionId}")
    public RestResult<List<SectionResource>> getSectionsByCompetitionId(@PathVariable("competitionId") final Long competitionId) {
        return sectionService.getByCompetitionId(competitionId).toGetResponse();
    }

    @GetMapping("/getByCompetitionIdVisibleForAssessment/{competitionId}")
    public RestResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(@PathVariable("competitionId") long competitionId) {
        return sectionService.getByCompetitionIdVisibleForAssessment(competitionId).toGetResponse();
    }
}
