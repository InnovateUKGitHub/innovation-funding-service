package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.validator.util.ValidationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service focused around the processing of Applications
 */
@Service
public class SectionServiceImpl extends BaseTransactionalService implements SectionService {
    private static final Log LOG = LogFactory.getLog(SectionServiceImpl.class);

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionMapper sectionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionService questionService;

    @Override
    public ServiceResult<SectionResource> getById(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource);
    }

    // TODO DW - INFUND-1555 - remove getSuccessObject call
    @Override
    public ServiceResult<Map<Long, Set<Long>>> getCompletedSections(final Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(this::completedSections);
    }

    private Map<Long, Set<Long>> completedSections(Application application) {
        List<Section> sections = application.getCompetition().getSections();
        List<Organisation> organisations = application.getProcessRoles().stream()
                .filter(p ->
                        p.getRole().getName().equals(UserRoleType.LEADAPPLICANT.getName()) ||
                                p.getRole().getName().equals(UserRoleType.APPLICANT.getName()) ||
                                p.getRole().getName().equals(UserRoleType.COLLABORATOR.getName())
                )
                .map(ProcessRole::getOrganisation).collect(Collectors.toList());
        Map<Long, Set<Long>> organisationMap = new HashMap<>();
        for (Organisation organisation : organisations) {
            Set<Long> completedSections = new LinkedHashSet<>();
            for (Section section : sections) {
                if (this.isSectionComplete(section, application.getId(), organisation.getId()).getSuccessObject()) {
                    completedSections.add(section.getId());
                }
            }
            organisationMap.put(organisation.getId(), completedSections);
        }
        return organisationMap;
    }

    // TODO DW - INFUND-1555 - remove getSuccessObject call
    @Override
    public ServiceResult<Set<Long>> getCompletedSections(final Long applicationId,
                                                         final Long organisationId) {

        return find(() -> getApplication(applicationId), () -> getIncompleteSections(applicationId)).
                andOnSuccess((application, incomplete) -> {

                    Set<Long> completedSections = new LinkedHashSet<>();
                    List<Section> sections = application.getCompetition().getSections();
                    for (Section section : sections) {
                        if (this.isSectionComplete(section, applicationId, organisationId).getSuccessObject()) {
                            completedSections.add(section.getId());
                        }
                    }

                    completedSections = completedSections.stream()
                            .filter(c -> !incomplete.contains(c))
                            .collect(Collectors.toSet());

                    return serviceSuccess(completedSections);
                });
    }

    @Override
    public ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId) {
        Section section = sectionRepository.findOne(sectionId);
        Set<Long> questions = collectAllQuestionFrom(section);
        return serviceSuccess(questions);
    }

    @Override
    public ServiceResult<List<ValidationMessages>> markSectionAsComplete(final Long sectionId,
                                                                         final Long applicationId,
                                                                         final Long markedAsCompleteById) {
        LOG.debug(String.format("markSectionAsComplete %s / %s / %s ", sectionId, applicationId, markedAsCompleteById));
        return find(section(sectionId), application(applicationId)).andOnSuccess((section, application) -> {
            Set<Long> questions = collectAllQuestionFrom(section);

            List<ValidationMessages> sectionIsValid = ValidationUtil.isSectionValid(markedAsCompleteById, section, application);

            if (sectionIsValid.isEmpty()) {
                LOG.debug("======= SECTION IS VALID =======");
                questions.forEach(q -> {
                    questionService.markAsComplete(q, applicationId, markedAsCompleteById);
                    // Assign back to lead applicant.
                    questionService.assign(q, applicationId, application.getLeadApplicantProcessRole().getId(), markedAsCompleteById);
                });
            } else {
                LOG.debug("======= SECTION IS INVALID =======   " + sectionIsValid.size());
                for (ValidationMessages validationMessages : sectionIsValid) {
                    if (validationMessages.getErrors() == null) {
                        LOG.debug("errors: " + validationMessages.getErrors().size());
                    } else {
                        LOG.debug("errors: " + validationMessages.getErrors().size());
                        for (Error error : validationMessages.getErrors()) {
                            LOG.debug(String.format("validation failed: %s / %s / %s", validationMessages.getObjectName(), error.getErrorKey(), error.getErrorMessage()));
                        }
                    }
                }
            }
            LOG.debug("Mark as complete done");
            return serviceSuccess(sectionIsValid);
        });
    }

    @Override
    public ServiceResult<Void> markSectionAsInComplete(final Long sectionId,
                                                       final Long applicationId,
                                                       final Long markedAsCompleteById) {
        Section section = sectionRepository.findOne(sectionId);
        Set<Long> questions = collectAllQuestionFrom(section);

        questions.forEach(q -> {
            questionService.markAsInComplete(q, applicationId, markedAsCompleteById);
        });

        return serviceSuccess();
    }

    private Set<Long> collectAllQuestionFrom(final Section section) {
        final Set<Long> questions = new HashSet<>();

        questions.addAll(section.getQuestions().stream().map(questionMapper::questionToId).collect(Collectors.toSet()));

        if (section.getChildSections() != null) {
            for (Section childSection : section.getChildSections()) {
                questions.addAll(collectAllQuestionFrom(childSection));
            }
        }

        return questions;
    }


    @Override
    public ServiceResult<List<Long>> getIncompleteSections(final Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(this::incompleteSections);
    }

    private List<Long> incompleteSections(Application application) {
        List<Section> sections = application.getCompetition().getSections();
        List<Long> incompleteSections = new ArrayList<>();

        for (Section section : sections) {
            boolean sectionIncomplete = false;

            List<Question> questions = section.fetchAllChildQuestions();
            for (Question question : questions) {
                if (question.getFormInputs().stream().anyMatch(input -> input.getWordCount() != null && input.getWordCount() > 0)) {
                    // if there is a maxWordCount, ensure that no responses have gone over the limit
                    sectionIncomplete = question.getFormInputs().stream().anyMatch(input -> {
                        List<FormInputResponse> responses = formInputResponseRepository.findByApplicationIdAndFormInputId(application.getId(), input.getId());
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
    public ServiceResult<SectionResource> findByName(final String name) {
        return find(sectionRepository.findByName(name), notFoundError(Section.class, name)).
                andOnSuccessReturn(sectionMapper::mapToResource);
    }

    // TODO DW - INFUND-1555 - work out the getSuccessObject call
    private ServiceResult<Boolean> isSectionComplete(Section section, Long applicationId, Long organisationId) {
        return isMainSectionComplete(section, applicationId, organisationId, true).andOnSuccess(sectionIsComplete -> {

            // check if section has subsections, if there are subsections let the outcome depend on those subsections
            // and the section itself if it contains questions with mark as complete attached
            if (sectionIsComplete && section.hasChildSections()) {
                sectionIsComplete = section.getChildSections()
                        .stream()
                        .allMatch(s -> isSectionComplete(s, applicationId, organisationId).getSuccessObject());
            }
            return serviceSuccess(sectionIsComplete);
        });
    }

    // TODO DW - INFUND-1555 - work out the getSuccessObject call
    @Override
    public ServiceResult<Boolean> isMainSectionComplete(Section section, Long applicationId, Long organisationId, boolean ignoreOtherOrganisations) {
        boolean sectionIsComplete = true;
        for (Question question : section.getQuestions()) {
            if (!ignoreOtherOrganisations && question.getName() != null && "FINANCE_SUMMARY_INDICATOR_STRING".equals(question.getName()) && section.getParentSection() != null) {
                if (!childSectionsAreCompleteForAllOrganisations(section.getParentSection(), applicationId, section).getSuccessObject()) {
                    sectionIsComplete = false;
                }
                break;
            }

            if (!question.isMarkAsCompletedEnabled())
                continue;

            boolean questionMarkedAsComplete = questionService.isMarkedAsComplete(question, applicationId, organisationId).getSuccessObject();
            // if one of the questions is incomplete then the whole section is incomplete
            if (!questionMarkedAsComplete) {
                sectionIsComplete = false;
                break;
            }
        }
        return serviceSuccess(sectionIsComplete);
    }

    // TODO DW - INFUND-1555 - work out the getSuccessObject call
    @Override
    public ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection) {
        return getApplication(applicationId).andOnSuccessReturn(application -> childSectionsCompleteForAllOrganisations(application, parentSection));
    }

    private Boolean childSectionsCompleteForAllOrganisations(Application application, Section parentSection) {
        boolean allSectionsWithSubsectionsAreComplete = true;

        List<Section> sections;
        // if no parent defined, just check all sections.
        if (parentSection == null) {
            sections = sectionRepository.findAll();
        } else {
            sections = parentSection.getChildSections();
        }

        List<ApplicationFinance> applicationFinanceList = application.getApplicationFinances();
        for (Section section : sections) {
            for (ApplicationFinance applicationFinance : applicationFinanceList) {
                if (!this.isMainSectionComplete(section, application.getId(), applicationFinance.getOrganisation().getId(), true).getSuccessObject()) {
                    allSectionsWithSubsectionsAreComplete = false;
                    break;
                }
            }
            if (!allSectionsWithSubsectionsAreComplete) {
                break;
            }
        }
        return allSectionsWithSubsectionsAreComplete;
    }

    @Override
    public ServiceResult<SectionResource> getNextSection(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource).andOnSuccess(this::getNextSection);
    }

    @Override
    public ServiceResult<SectionResource> getNextSection(SectionResource section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getNextSiblingSection(section);
        } else {
            Section nextSection = sectionRepository.findFirstByCompetitionIdAndPriorityGreaterThanAndParentSectionIsNullOrderByPriorityAsc(section.getCompetition(), section.getPriority());
            return find(nextSection, notFoundError(Section.class, section.getCompetition(), section.getPriority())).andOnSuccessReturn(sectionMapper::mapToResource);
        }
    }

    private ServiceResult<SectionResource> getNextSiblingSection(SectionResource section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityGreaterThanAndQuestionGroupTrueOrderByPriorityAsc(
                section.getCompetition(), section.getParentSection(), section.getPriority());

        if (sibling == null) {
            return getNextSection(section.getParentSection());
        } else {
            return serviceSuccess(sectionMapper.mapToResource(sibling));
        }
    }

    @Override
    public ServiceResult<SectionResource> getPreviousSection(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource).andOnSuccess(this::getPreviousSection);
    }

    @Override
    public ServiceResult<SectionResource> getPreviousSection(SectionResource section) {
        if (section == null) {
            return null;
        }

        if (section.getParentSection() != null) {
            return getPreviousSiblingSection(section);
        } else {
            Section firstSection = sectionRepository.findFirstByCompetitionIdAndPriorityLessThanAndParentSectionIsNullOrderByPriorityDesc(section.getCompetition(), section.getPriority());

            return find(firstSection, notFoundError(Section.class, section.getCompetition(), section.getPriority())).
                    andOnSuccessReturn(sectionMapper::mapToResource);
        }
    }

    private ServiceResult<SectionResource> getPreviousSiblingSection(SectionResource section) {
        Section sibling = sectionRepository.findFirstByCompetitionIdAndParentSectionIdAndPriorityLessThanAndQuestionGroupTrueOrderByPriorityDesc(
                section.getCompetition(), section.getParentSection(), section.getPriority());

        if (sibling == null) {
            return getPreviousSection(section.getParentSection());
        } else {
            return serviceSuccess(sectionMapper.mapToResource(sibling));
        }
    }

    @Override
    public ServiceResult<SectionResource> getSectionByQuestionId(final Long questionId) {
        return find(sectionRepository.findByQuestionsId(questionId), notFoundError(Section.class, questionId)).
                andOnSuccessReturn(sectionMapper::mapToResource);
    }
}
