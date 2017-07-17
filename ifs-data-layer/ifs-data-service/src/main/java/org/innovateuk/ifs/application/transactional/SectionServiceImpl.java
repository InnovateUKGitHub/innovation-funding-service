package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.mapper.SectionMapper;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

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
        List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
        List<Long> organisations = simpleMap(applicantTypeProcessRoles, ProcessRole::getOrganisationId);

        Map<Long, Set<Long>> organisationMap = new HashMap<>();

        for (Long organisationId : organisations) {
            Set<Long> completedSections = new LinkedHashSet<>();
            for (Section section : sections) {
                if (this.isSectionComplete(section, application.getId(), organisationId).getSuccessObject()) {
                    completedSections.add(section.getId());
                }
            }
            organisationMap.put(organisationId, completedSections);
        }
        return organisationMap;
    }

    @Override
    public ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId) {
        return find(application(applicationId)).
                andOnSuccess(application -> {

                    List<Section> sections = application.getCompetition().getSections();

                    final List<ServiceResult<Pair<Long, Boolean>>> unaggregatedSectionsAndStatus = simpleMap(sections, section ->
                            isSectionComplete(section, applicationId, organisationId).andOnSuccessReturn(isComplete -> of(section.getId(), isComplete)));

                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedSectionsAndStatus = aggregate(unaggregatedSectionsAndStatus);
                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedCompleteSectionsAndStatus = aggregatedSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> simpleFilter(sectionsWithStatus, Pair::getValue));
                    return aggregatedCompleteSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> new HashSet<>(simpleMap(sectionsWithStatus, Pair::getKey)));
                });
    }

    @Override
    public ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(final Long sectionId) {
        return find(section(sectionId)).andOnSuccessReturn(section ->
                new HashSet<>(simpleMap(section.fetchAllQuestionsAndChildQuestions(), Question::getId)));
    }

    @Override
    @Transactional
    public ServiceResult<List<ValidationMessages>> markSectionAsComplete(final Long sectionId,
                                                                         final Long applicationId,
                                                                         final Long markedAsCompleteById) {

        return find(section(sectionId), application(applicationId)).andOnSuccess((section, application) -> {

            List<ValidationMessages> sectionIsValid = validationUtil.isSectionValid(markedAsCompleteById, section, application);

            if (sectionIsValid.isEmpty()) {
                markSectionAsComplete(section, application, markedAsCompleteById);
            }

            return serviceSuccess(sectionIsValid);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> markSectionAsNotRequired(Long sectionId, Long applicationId, Long markedAsCompleteById) {
        return find(section(sectionId), application(applicationId)).andOnSuccess((section, application) -> {
            markSectionAsComplete(section, application, markedAsCompleteById);
            return serviceSuccess();
        });
    }

    private void markSectionAsComplete(Section section, Application application, Long markedAsCompleteById) {
        getQuestionsForSectionAndSubsections(section.getId()).andOnSuccessReturnVoid(questions -> questions.forEach(q -> {
            questionService.markAsComplete(new QuestionApplicationCompositeId(q, application.getId()), markedAsCompleteById);
            // Assign back to lead applicant.
            questionService.assign(new QuestionApplicationCompositeId(q, application.getId()), application.getLeadApplicantProcessRole().getId(), markedAsCompleteById);
        }));
    }


    @Override
    @Transactional
    public ServiceResult<Void> markSectionAsInComplete(final Long sectionId,
                                                       final Long applicationId,
                                                       final Long markedAsInCompleteById) {

        return getQuestionsForSectionAndSubsections(sectionId).andOnSuccessReturnVoid(questions -> questions.forEach(q ->
                questionService.markAsInComplete(new QuestionApplicationCompositeId(q, applicationId), markedAsInCompleteById)
        ));
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

            List<Question> questions = section.fetchAllQuestionsAndChildQuestions();
            for (Question question : questions) {
                List<FormInput> formInputs = simpleFilter(question.getFormInputs(), input -> input.getActive() && FormInputScope.APPLICATION.equals(input.getScope()));
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
        return isMainSectionComplete(section, applicationId, organisationId).andOnSuccess(
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

    private ServiceResult<Boolean> isMainSectionComplete(Section section, Long applicationId, Long organisationId) {

        for (Question question : section.getQuestions()) {

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
                if (!this.isMainSectionComplete(section, application.getId(), applicationFinance.getOrganisation().getId()).getSuccessObject()) {
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
