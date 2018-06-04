package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SectionStatusController exposes completion status of application section data and operations through a REST API.
 */
@RestController
@RequestMapping("/section-status")
public class SectionStatusController {

    @Autowired
    private SectionStatusService sectionStatusService;

    @GetMapping("/get-completed-sections-by-organisation/{applicationId}")
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsMap(@PathVariable("applicationId") final long applicationId) {
        return sectionStatusService.getCompletedSections(applicationId).toGetResponse();
    }

    @GetMapping("/get-completed-sections/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getCompletedSections(@PathVariable("applicationId") final long applicationId,
                                                      @PathVariable("organisationId") final long organisationId) {

        return sectionStatusService.getCompletedSections(applicationId, organisationId).toGetResponse();
    }

    @PostMapping("/mark-as-complete/{sectionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("sectionId") final long sectionId,
                                                               @PathVariable("applicationId") final long applicationId,
                                                               @PathVariable("markedAsCompleteById") final long markedAsCompleteById) {
        return sectionStatusService.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById).toGetResponse();
    }

    @PostMapping("/mark-as-not-required/{sectionId}/{applicationId}/{markedAsNotRequiredById}")
    public RestResult<Void> markAsNotRequired(@PathVariable("sectionId") final long sectionId,
                                              @PathVariable("applicationId") final long applicationId,
                                              @PathVariable("markedAsNotRequiredById") final long markedAsNotRequiredById) {
        return sectionStatusService.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById).toGetResponse();
    }

    @PostMapping("/mark-as-in-complete/{sectionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("sectionId") final long sectionId,
                                             @PathVariable("applicationId") final long applicationId,
                                             @PathVariable("markedAsInCompleteById") final long markedAsInCompleteById) {
        return sectionStatusService.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById).toPutResponse();
    }

    @GetMapping("/all-sections-marked-as-complete/{applicationId}")
    public RestResult<Boolean> getCompletedSections(@PathVariable("applicationId") final long applicationId) {
        return sectionStatusService.childSectionsAreCompleteForAllOrganisations(null, applicationId, null).toGetResponse();
    }

    @GetMapping("/get-incomplete-sections/{applicationId}")
    public RestResult<List<Long>> getIncompleteSections(@PathVariable("applicationId") final long applicationId) {
        return sectionStatusService.getIncompleteSections(applicationId).toGetResponse();
    }

}
