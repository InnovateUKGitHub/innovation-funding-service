package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.application.transactional.SectionStatusService;
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
@RequestMapping("/section-status")
public class SectionStatusController {

    @Autowired
    private SectionStatusService sectionStatusService;

    @GetMapping("/get-completed-sections-by-organisation/{applicationId}")
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsMap(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.getCompletedSections(applicationId).toGetResponse();
    }

    @GetMapping("/get-completed-sections/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                                      @PathVariable("organisationId") final Long organisationId) {

        return sectionStatusService.getCompletedSections(applicationId, organisationId).toGetResponse();
    }

    @PostMapping("/mark-as-complete/{sectionId}/{applicationId}/{markedAsCompleteById}")
    public RestResult<List<ValidationMessages>> markAsComplete(@PathVariable("sectionId") final Long sectionId,
                                                               @PathVariable("applicationId") final Long applicationId,
                                                               @PathVariable("markedAsCompleteById") final Long markedAsCompleteById) {
        return sectionStatusService.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById).toGetResponse();
    }

    @PostMapping("/mark-as-not-required/{sectionId}/{applicationId}/{markedAsNotRequiredById}")
    public RestResult<Void> markAsNotRequired(@PathVariable("sectionId") final Long sectionId,
                                              @PathVariable("applicationId") final Long applicationId,
                                              @PathVariable("markedAsNotRequiredById") final Long markedAsNotRequiredById) {
        return sectionStatusService.markSectionAsNotRequired(sectionId, applicationId, markedAsNotRequiredById).toGetResponse();
    }

    @PostMapping("/mark-as-in-complete/{sectionId}/{applicationId}/{markedAsInCompleteById}")
    public RestResult<Void> markAsInComplete(@PathVariable("sectionId") final Long sectionId,
                                             @PathVariable("applicationId") final Long applicationId,
                                             @PathVariable("markedAsInCompleteById") final Long markedAsInCompleteById) {
        return sectionStatusService.markSectionAsInComplete(sectionId, applicationId, markedAsInCompleteById).toPutResponse();
    }

    @GetMapping("/all-sections-marked-as-complete/{applicationId}")
    public RestResult<Boolean> getCompletedSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.childSectionsAreCompleteForAllOrganisations(null, applicationId, null).toGetResponse();
    }

    @GetMapping("/get-incomplete-sections/{applicationId}")
    public RestResult<List<Long>> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionStatusService.getIncompleteSections(applicationId).toGetResponse();
    }

}
