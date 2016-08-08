package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.mapper.QuestionMapper;
import com.worth.ifs.application.mapper.SectionMapper;
import com.worth.ifs.application.repository.SectionRepository;
import com.worth.ifs.application.resource.QuestionApplicationCompositeId;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.validator.util.ValidationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.aggregate;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.tuple.Pair.of;

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

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    public ServiceResult<SectionResource> getById(final Long sectionId) {
        return getSection(sectionId).andOnSuccessReturn(sectionMapper::mapToResource);
    }

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
                .map(ProcessRole::getOrganisation).collect(toList());
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

    @Override
    public ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId) {
        return find(application(applicationId), () -> getIncompleteSections(applicationId)).
                andOnSuccess((application, incomplete) -> {
                    final List<ServiceResult<Pair<Long, Boolean>>> unaggregatedSectionsAndStatus = new ArrayList<>();
                    for (final Section section : application.getCompetition().getSections()) {
                        unaggregatedSectionsAndStatus.add(isSectionComplete(section, applicationId, organisationId).andOnSuccessReturn(isComplete -> of(section.getId(), isComplete)));
                    }
                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedSectionsAndStatus = aggregate(unaggregatedSectionsAndStatus);
                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedCompleteSectionsAndStatus = aggregatedSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> simpleFilter(sectionsWithStatus, Pair::getValue));
                    final ServiceResult<Set<Long>> aggregatedCompleteSections = aggregatedCompleteSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> new HashSet(simpleMap(sectionsWithStatus, Pair::getKey)));
                    return aggregatedCompleteSections;
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

            List<ValidationMessages> sectionIsValid = validationUtil.isSectionValid(markedAsCompleteById, section, application);

            if (sectionIsValid.isEmpty()) {
                LOG.debug("======= SECTION IS VALID =======");
                questions.forEach(q -> {
                    questionService.markAsComplete(new QuestionApplicationCompositeId(q, applicationId), markedAsCompleteById);
                    // Assign back to lead applicant.
                    questionService.assign(new QuestionApplicationCompositeId(q, applicationId), application.getLeadApplicantProcessRole().getId(), markedAsCompleteById);
                });
            } else {
                LOG.debug("======= SECTION IS INVALID =======   " + sectionIsValid.size());
            }
            return serviceSuccess(sectionIsValid);
        });
    }

    @Override
    public ServiceResult<Void> markSectionAsInComplete(final Long sectionId,
                                                       final Long applicationId,
                                                       final Long markedAsInCompleteById) {
        Section section = sectionRepository.findOne(sectionId);
        Set<Long> questions = collectAllQuestionFrom(section);

        questions.forEach(q ->
            questionService.markAsInComplete(new QuestionApplicationCompositeId(q, applicationId), markedAsInCompleteById)
        );

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
                List<FormInput> formInputs = simpleFilter(question.getFormInputs(), input -> FormInputScope.APPLICATION.equals(input.getScope()));
                if (formInputs.stream().anyMatch(input -> input.getWordCount() != null && input.getWordCount() > 0)) {
                    // if there is a maxWordCount, ensure that no responses have gone over the limit
                    sectionIncomplete = formInputs.stream().anyMatch(input -> {
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
    public ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(final Long competitionId, final SectionType type) {
        return getCompetition(competitionId).andOnSuccessReturn(comp -> sectionsOfType(comp, type));
    }

    private List<SectionResource> sectionsOfType(Competition competition, SectionType type) {
        return competition.getSections().stream()
                .filter(s -> s.isType(type))
                .map(sectionMapper::mapToResource)
                .collect(toList());
    }

    private ServiceResult<Boolean> isSectionComplete(Section section, Long applicationId, Long organisationId) {
        return isMainSectionComplete(section, applicationId, organisationId, true).andOnSuccess(
                mainSectionComplete -> {
                    // If there are child sections are they complete?
                    if (mainSectionComplete && section.hasChildSections()) {
                        for (final Section childSection : section.getChildSections()) {
                            final ServiceResult<Boolean> sectionComplete = isSectionComplete(childSection, applicationId, organisationId);
                            if (sectionComplete.isFailure()) {
                                return sectionComplete;
                            } else if (!sectionComplete.getSuccessObject()) {
                                return serviceSuccess(false);
                            }

                        }
                    }
                    return serviceSuccess(mainSectionComplete);
                });
    }

    private ServiceResult<Boolean> isMainSectionComplete(Section section, Long applicationId, Long organisationId, boolean ignoreOtherOrganisations) {
        for (Question question : section.getQuestions()) {
            if (!ignoreOtherOrganisations && question.getName() != null && "FINANCE_SUMMARY_INDICATOR_STRING".equals(question.getName()) && section.getParentSection() != null) {
                final ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations = childSectionsAreCompleteForAllOrganisations(section.getParentSection(), applicationId, section);
                if (childSectionsAreCompleteForAllOrganisations.isFailure()) {
                    return childSectionsAreCompleteForAllOrganisations;
                } else if (!childSectionsAreCompleteForAllOrganisations.getSuccessObject()) {
                    return serviceSuccess(false);
                }
            }

            if (question.isMarkAsCompletedEnabled()) {
                final ServiceResult<Boolean> markedAsComplete = questionService.isMarkedAsComplete(question, applicationId, organisationId);
                // if one of the questions is incomplete then the whole section is incomplete
                if (markedAsComplete.isFailure()) {
                    return markedAsComplete;
                } else if (!markedAsComplete.getSuccessObject()) {
                    return serviceSuccess(false);
                }
            }
        }
        return serviceSuccess(true);
    }


    @Override
    public ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, Long applicationId, Section excludedSection) {
        return getApplication(applicationId).andOnSuccess(application -> childSectionsCompleteForAllOrganisations(application, parentSection));
    }

    private ServiceResult<Boolean> childSectionsCompleteForAllOrganisations(Application application, Section parentSection) {
        boolean allSectionsWithSubsectionsAreComplete = true;

        List<Section> sections;
        // if no parent defined, just check all sections.
        if (parentSection == null) {
            sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(application.getCompetition().getId());
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
        return serviceSuccess(allSectionsWithSubsectionsAreComplete);
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

    @Override
    public ServiceResult<List<SectionResource>> getByCompetitionId(final Long competitionId) {
        return find(sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(competitionId), notFoundError(Section.class, competitionId)).
                andOnSuccessReturn(r -> simpleMap(r, sectionMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId) {
        return serviceSuccess(simpleMap(sectionRepository.findByCompetitionIdAndDisplayInAssessmentApplicationSummaryTrueOrderByPriorityAsc(competitionId), sectionMapper::mapToResource));
    }

}
