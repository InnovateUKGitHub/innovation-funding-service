package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
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

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @Autowired
    private SectionStatusService sectionStatusService;

    @GetMapping("/{sectionId}")
    public RestResult<SectionResource> getById(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getById(sectionId).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @GetMapping("/getCompletedSectionsByOrganisation/{applicationId}")
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsMap(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.getCompletedSections(applicationId).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @GetMapping("/getCompletedSections/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                                      @PathVariable("organisationId") final Long organisationId) {

        return sectionStatusService.getCompletedSections(applicationId, organisationId).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @PostMapping("/markAsComplete/{sectionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("sectionId") final Long sectionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return sectionStatusService.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @PostMapping("/markAsNotRequired/{sectionId}/{applicationId}/{markedAsNotRequiredById}")
    public RestResult<Void> markAsNotRequired(@PathVariable("sectionId") final Long sectionId,
                                              @PathVariable("applicationId") final Long applicationId,
                                              @PathVariable("markedAsNotRequiredById") final Long markedAsNotRequiredById) {
        return sectionStatusService.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @PostMapping("/markAsInComplete/{sectionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("sectionId") final Long sectionId,
                                             @PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        return sectionStatusService.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById).toPutResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @GetMapping("/allSectionsMarkedAsComplete/{applicationId}")
    public RestResult<Boolean> getCompletedSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.childSectionsAreCompleteForAllOrganisations(null, applicationId, null).toGetResponse();
    }

    @Deprecated
    @ZeroDowntime(reference = "IFS-Something", description = "Created new endpoint in SectionStatusController.")
    @GetMapping("/getIncompleteSections/{applicationId}")
    public RestResult<List<Long>> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.getIncompleteSections(applicationId).toGetResponse();
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
