package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.application.repository.ResponseRepository;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class SectionServiceImpl extends BaseTransactionalService implements SectionService {

    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ResponseRepository responseRepository;
    @Autowired
    FormInputResponseRepository formInputResponseRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuestionService questionService;

    @Override
    public Section getById(final Long sectionId) {
        return sectionRepository.findOne(sectionId);
    }

    @Override
    public Set<Long> getCompletedSections(final Long applicationId,
                                          final Long organisationId) {
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


    @Override
    public List<Long> getIncompleteSections(final Long applicationId) {
        Application application = applicationRepository.findOne(applicationId);

        List<Section> sections = application.getCompetition().getSections();
        List<Long> incompleteSections = new ArrayList<>();

        for (Section section : sections) {
            boolean sectionIncomplete = false;

            List<Question> questions = section.getAllChildQuestions();
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

    @Override
    public Section findByName(@PathVariable("name") final String name) {
        return sectionRepository.findByName(name);
    }

    private boolean isSectionComplete(Section section, Long applicationId, Long organisationId) {
        boolean sectionIsComplete = isMainSectionComplete(section, applicationId, organisationId);

        // check if section has subsections, if there are subsections let the outcome depend on those subsections
        // and the section itself if it contains questions with mark as complete attached
        if (sectionIsComplete && section.hasChildSections()) {
            sectionIsComplete = section.getChildSections()
                    .stream()
                    .allMatch(s -> isSectionComplete(s, applicationId, organisationId));
        }
        return sectionIsComplete;
    }

    @Override
    public boolean isMainSectionComplete(Section section, Long applicationId, Long organisationId) {
        boolean sectionIsComplete = true;
        for (Question question : section.getQuestions()) {
            if (question.getName() != null && question.getName().equals("FINANCE_SUMMARY_INDICATOR_STRING") && section.getParentSection() != null) {
                if (!childSectionsAreCompleteForAllOrganisations(section.getParentSection(), applicationId, section)) {
                    sectionIsComplete = false;
                }
                break;
            }

            if (!question.isMarkAsCompletedEnabled())
                continue;

            boolean questionMarkedAsComplete = questionService.isMarkedAsComplete(question, applicationId, organisationId);
            // if one of the questions is incomplete then the whole section is incomplete
            if (!questionMarkedAsComplete) {
                sectionIsComplete = false;
                break;
            }
        }
        return sectionIsComplete;
    }

    @Override
    public boolean childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection) {
        boolean allSectionsWithSubsectionsAreComplete = true;

        Application application = applicationRepository.findOne(applicationId);
        List<Section> sections = null;
        // if no parent defined, just check all sections.
        if(parentSection == null){
            sections = sectionRepository.findAll();
        }else{
            sections = parentSection.getChildSections();
        }

        List<ApplicationFinance> applicationFinanceList = application.getApplicationFinances();
        for (Section section : sections) {
            for (ApplicationFinance applicationFinance : applicationFinanceList) {
                if (!this.isMainSectionComplete(section, applicationId, applicationFinance.getOrganisation().getId())) {
                    log.debug(String.format("This section is incomplete: section ID %d / name %s / organisation ID %d ", section.getId(), section.getName(), applicationFinance.getOrganisation().getId()));
                    allSectionsWithSubsectionsAreComplete = false;
                    break;
                }
            }
            if (allSectionsWithSubsectionsAreComplete == false) {
                break;
            }
        }

        return allSectionsWithSubsectionsAreComplete;
    }

    @Override
    public Section getNextSection(@PathVariable("sectionId") final Long sectionId) {
        if (sectionId == null) {
            return null;
        }
        Section section = sectionRepository.findOne(sectionId);
        return getNextSection(section);
    }

    @Override
    public Section getNextSection(Section section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getNextSiblingSection(section);
        } else {
            Section nextSection = sectionRepository.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(section.getCompetition().getId(), section.getPriority());
            return nextSection;
        }
    }

    private Section getNextSiblingSection(Section section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority());

        if (sibling == null) {
            return getNextSection(section.getParentSection());
        } else {
            return sibling;
        }
    }

    @Override
    public Section getPreviousSection(@PathVariable("sectionId") final Long sectionId) {
        if (sectionId == null) {
            return null;
        }
        Section section = sectionRepository.findOne(sectionId);
        return getPreviousSection(section);
    }

    @Override
    public Section getPreviousSection(Section section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getPreviousSiblingSection(section);
        } else {
            return sectionRepository.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(section.getCompetition().getId(), section.getPriority());
        }
    }

    private Section getPreviousSiblingSection(Section section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                section.getCompetition().getId(), section.getParentSection().getId(), section.getPriority());

        if (sibling == null) {
            return getPreviousSection(section.getParentSection());
        } else {
            return sibling;
        }
    }

    @Override
    public Section getSectionByQuestionId(@PathVariable("questionId") final Long questionId) {
        return sectionRepository.findByQuestionsId(questionId);
    }

}
