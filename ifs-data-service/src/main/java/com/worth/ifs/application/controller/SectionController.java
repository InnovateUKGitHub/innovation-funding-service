package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.repository.SectionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ApplicationController exposes Application data through a REST API.
 */
@RestController
@RequestMapping("/section")
public class SectionController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    QuestionController questionController;

    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/getCompletedSections/{applicationId}/{organisationId}")
    public Set<Long> getCompletedSections(@PathVariable("applicationId") final Long applicationId,
                                          @PathVariable("organisationId") final Long organisationId) {
        Set<Long> completedSections = new LinkedHashSet<>();
        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = application.getCompetition().getSections();
        for (Section section : sections) {
            if (this.isSectionComplete(section, applicationId, organisationId)) {
                completedSections.add(section.getId());
            }
        }
        List<Long> incomplete = this.getIncompleteSections(applicationId);

        completedSections = completedSections.stream()
                .filter(c -> !incomplete.contains(c))
                .collect(Collectors.toSet());

        return completedSections;
    }


    @RequestMapping("/getIncompleteSections/{applicationId}")
    public List<Long> getIncompleteSections(@PathVariable("applicationId") final Long applicationId) {
        Application application = applicationRepository.findOne(applicationId);

        List<Section> sections = application.getCompetition().getSections();
        List<Long> incompleteSections = new ArrayList<>();

        for (Section section : sections) {
            boolean sectionIncomplete = false;

            List<Question> questions = section.getQuestions();
            for (Question question : questions) {
                if (question.getWordCount() != null && question.getWordCount() > 0) {
                    // there is a maxWordCount.
                    Response response = responseRepository.findByApplicationIdAndQuestionId(applicationId, question.getId());
                    if (response != null && response.getWordCountLeft() < 0) {
                        // gone over the limit !
                        sectionIncomplete = true;
                        break;
                    } else {
                        sectionIncomplete = false;
                    }
                } else {
                    // no wordcount.
                    sectionIncomplete = false;
                }
            }
            if (sectionIncomplete) {
                incompleteSections.add(section.getId());
            }
        }

        return incompleteSections;
    }

    @RequestMapping("findByName/{name}")
    public Section findByName(@PathVariable("name") final String name) {
        return sectionRepository.findByName(name);
    }

    private boolean isSectionComplete(Section section, Long applicationId, Long organisationId) {
        boolean sectionIsComplete = true;
        sectionIsComplete = isMainSectionComplete(section, applicationId, organisationId);

        // check if section has subsections, if there are subsections let the outcome depend on those subsections
        // and the section itself if it contains questions with mark as complete attached
        if (sectionIsComplete && section.hasChildSections()) {
            sectionIsComplete = section.getChildSections()
                    .stream()
                    .allMatch(s -> isSectionComplete(s, applicationId, organisationId));
            log.debug("section : " + section.getName() + " id: " + section.getId() + " complete: " + sectionIsComplete + " for org: " + organisationId);
        }
        return sectionIsComplete;
    }

    /**
     * get questions for the sections and filter out the ones that have marked as completed turned on
     * @param section
     * @param organisationId
     * @return
     */
    public boolean isMainSectionComplete(Section section, Long applicationId, Long organisationId) {
        boolean sectionIsComplete = true;
        for(Question question : section.getQuestions()) {
            if(!question.isMarkAsCompletedEnabled())
                continue;

            boolean questionMarkedAsComplete = questionController.isMarkedAsComplete(question, applicationId, organisationId);
            // if one of the questions is incomplete then the whole section is incomplete
            if(!questionMarkedAsComplete) {
                sectionIsComplete = false;
                break;
            }
        }
        return sectionIsComplete;
    }

}
