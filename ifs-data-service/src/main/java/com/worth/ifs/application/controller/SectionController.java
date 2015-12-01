package com.worth.ifs.application.controller;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SectionController exposes Application data and operations through a REST API.
 */
@RestController
@RequestMapping("/section")
public class SectionController {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    FormInputResponseRepository formInputResponseRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    QuestionController questionController;

    private final Log log = LogFactory.getLog(getClass());

    private final static String FINANCE_SUMMARY_QUESTION_NAME_STRING = "FINANCE_SUMMARY_INDICATOR_STRING";

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
                if (question.getFormInputs().stream().anyMatch(input -> input.getWordCount() != null && input.getWordCount() > 0)) {

                    // if there is a maxWordCount, ensure that no responses have gone over the limit
                    sectionIncomplete = question.getFormInputs().stream().anyMatch(input -> {
                        List<FormInputResponse> responses = formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, input.getId());
                        return responses.stream().anyMatch(response -> response.getWordCountLeft() < 0);
                    });

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
     */
    public boolean isMainSectionComplete(Section section, Long applicationId, Long organisationId) {
        boolean sectionIsComplete = true;
        for(Question question : section.getQuestions()) {
            if(question.getName()!=null && question.getName().equals(FINANCE_SUMMARY_QUESTION_NAME_STRING)){
                if(!childSectionsAreCompleteForAllOrganisations(section.getParentSection(), applicationId, section)) {
                    sectionIsComplete = false;
                }
                break;
            }

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

    public boolean childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection) {
        boolean allSectionsWithSubsectionsAreComplete = true;

        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = parentSection.getChildSections();

        List<ApplicationFinance> applicationFinanceList = application.getApplicationFinances();

        for (Section section : sections) {
            //Only check the sections that have subsections
            if(section.hasChildSections() && !section.equals(excludedSection)) {
                //Check if section is complete for all organisations that participate in the application
                for(ApplicationFinance applicationFinance : applicationFinanceList) {
                    if (!this.isSectionComplete(section, applicationId, applicationFinance.getOrganisation().getId())) {
                        allSectionsWithSubsectionsAreComplete=false;
                        break;
                    }
                }
            }
        }

        return allSectionsWithSubsectionsAreComplete;
    }
}
