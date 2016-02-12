package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.transactional.SectionService;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/getById/{sectionId}")
    public RestResult<Section> getById(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getById(sectionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getCompletedSectionsByOrganisation/{applicationId}")
    public RestResult<Map<Long, Set<Long>>> getCompletedSectionsMap(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.getCompletedSections(applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getCompletedSections/{applicationId}/{organisationId}")
    public RestResult<Set<Long>> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                          @PathVariable("organisationId") final Long organisationId) {

        return sectionService.getCompletedSections(applicationId, organisationId).toDefaultRestResultForGet();
    }

    @RequestMapping("/allSectionsMarkedAsComplete/{applicationId}")
    public RestResult<Boolean> getCompletedSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.childSectionsAreCompleteForAllOrganisations(null, applicationId, null).toDefaultRestResultForGet();
    }

    @RequestMapping("/getIncompleteSections/{applicationId}")
    public RestResult<List<Long>> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.getIncompleteSections(applicationId).toDefaultRestResultForGet();
    }

    @RequestMapping("findByName/{name}")
    public RestResult<Section> findByName(@PathVariable("name") final String name) {
        return sectionService.findByName(name).toDefaultRestResultForGet();
    }

    @RequestMapping("/getNextSection/{sectionId}")
    public RestResult<Section> getNextSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getNextSection(sectionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getPreviousSection/{sectionId}")
    public RestResult<Section> getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getPreviousSection(sectionId).toDefaultRestResultForGet();
    }

    @RequestMapping("/getSectionByQuestionId/{questionId}")
    public RestResult<Section> getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionService.getSectionByQuestionId(questionId).toDefaultRestResultForGet();
    }
}
