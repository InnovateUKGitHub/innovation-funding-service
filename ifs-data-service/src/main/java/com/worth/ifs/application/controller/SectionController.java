package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.transactional.SectionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    SectionService sectionService;

    @RequestMapping("/getById/{sectionId}")
    public Section getById(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getById(sectionId);
    }

    @RequestMapping("/getCompletedSections/{applicationId}/{organisationId}")
    public Set<Long> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                          @PathVariable("organisationId") final Long organisationId) {
        return sectionService.getCompletedSections(applicationId, organisationId);
    }


    @RequestMapping("/getIncompleteSections/{applicationId}")
    public List<Long> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        return sectionService.getIncompleteSections(applicationId);
    }

    @RequestMapping("findByName/{name}")
    public Section findByName(@PathVariable("name") final String name) {
        return sectionService.findByName(name);
    }




    @RequestMapping("/getNextSection/{sectionId}")
    public Section getNextSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getNextSection(sectionId);
    }


    @RequestMapping("/getPreviousSection/{sectionId}")
    public Section getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        return sectionService.getPreviousSection(sectionId);
    }


    @RequestMapping("/getSectionByQuestionId/{questionId}")
    public Section getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionService.getSectionByQuestionId(questionId);
    }
}
