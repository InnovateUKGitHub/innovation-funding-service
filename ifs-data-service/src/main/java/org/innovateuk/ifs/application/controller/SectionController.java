package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.transactional.SectionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @GetMapping("/getCompletedSectionsByOrganisation/{applicationId}")
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsMap(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.getCompletedSections(applicationId).toGetResponse();
    }

    @GetMapping("/getCompletedSections/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                                      @PathVariable("organisationId") final Long organisationId) {

        return sectionService.getCompletedSections(applicationId, organisationId).toGetResponse();
    }

    @PostMapping("/markAsComplete/{sectionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("sectionId") final Long sectionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return sectionService.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById).toGetResponse();
    }

    @PostMapping("/markAsNotRequired/{sectionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<Void> markAsNotRequired(@PathVariable("sectionId") final Long sectionId,
                                              @PathVariable("applicationId") final Long applicationId,
                                              @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return sectionService.markSectionAsNotRequired(sectionId, applicationId, markedAsCompleteById).toGetResponse();
    }

    @PostMapping("/markAsInComplete/{sectionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("sectionId") final Long sectionId,
                                             @PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        return sectionService.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById).toPutResponse();
    }

    @GetMapping("/allSectionsMarkedAsComplete/{applicationId}")
    public RestResult<Boolean> getCompletedSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.childSectionsAreCompleteForAllOrganisations(null, applicationId, null).toGetResponse();
    }

    @GetMapping("/getIncompleteSections/{applicationId}")
    public RestResult<List<Long>> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.getIncompleteSections(applicationId).toGetResponse();
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
}
