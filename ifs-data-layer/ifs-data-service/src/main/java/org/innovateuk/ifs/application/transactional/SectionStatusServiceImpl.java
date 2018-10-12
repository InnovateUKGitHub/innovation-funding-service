package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Implements {@link SectionStatusService}
 */
@Service
public class SectionStatusServiceImpl extends BaseTransactionalService implements SectionStatusService {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionStatusService questionStatusService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ApplicationValidationUtil validationUtil;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Override
    public ServiceResult<Map<Long, Set<Long>>> getCompletedSections(final long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(this::completedSections);
    }

    @Override
    public ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId) {
        return find(application(applicationId)).
                andOnSuccess(application -> {

                    List<Section> sections = application.getCompetition().getSections();

                    final List<ServiceResult<Pair<Long, Boolean>>> unaggregatedSectionsAndStatus = simpleMap(sections, section ->
                            isSectionComplete(section, application, organisationId).andOnSuccessReturn(isComplete -> Pair.of(section.getId(), isComplete)));

                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedSectionsAndStatus = aggregate(unaggregatedSectionsAndStatus);
                    final ServiceResult<List<Pair<Long, Boolean>>> aggregatedCompleteSectionsAndStatus = aggregatedSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> simpleFilter(sectionsWithStatus, Pair::getValue));
                    return aggregatedCompleteSectionsAndStatus.andOnSuccessReturn(sectionsWithStatus -> new HashSet<>(simpleMap(sectionsWithStatus, Pair::getKey)));
                });
    }

    @Override
    @Transactional
    public ServiceResult<List<ValidationMessages>> markSectionAsComplete(final long sectionId,
                                                                         final long applicationId,
                                                                         final long markedAsCompleteById) {

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
    public ServiceResult<Void> markSectionAsNotRequired(long sectionId, long applicationId, long markedAsCompleteById) {
        return find(section(sectionId), application(applicationId)).andOnSuccess((section, application) -> {
            markSectionAsComplete(section, application, markedAsCompleteById);
            return serviceSuccess();
        });
    }

    private void markSectionAsComplete(Section section, Application application, long markedAsCompleteById) {
        sectionService.getQuestionsForSectionAndSubsections(section.getId()).andOnSuccessReturnVoid(questions -> questions.forEach(q -> {
            questionStatusService.markAsComplete(new QuestionApplicationCompositeId(q, application.getId()), markedAsCompleteById);
            // Assign back to lead applicant.
            questionStatusService.assign(new QuestionApplicationCompositeId(q, application.getId()), application.getLeadApplicantProcessRole().getId(), markedAsCompleteById);
        }));
    }


    @Override
    @Transactional
    public ServiceResult<Void> markSectionAsInComplete(final long sectionId,
                                                       final long applicationId,
                                                       final long markedAsInCompleteById) {

        return sectionService.getQuestionsForSectionAndSubsections(sectionId).andOnSuccessReturnVoid(questions -> questions.forEach(q ->
                questionStatusService.markAsInComplete(new QuestionApplicationCompositeId(q, applicationId), markedAsInCompleteById)
        ));
    }

    @Override
    public ServiceResult<List<Long>> getIncompleteSections(final long applicationId) {
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

    private ServiceResult<Boolean> isSectionComplete(Section section, Application application, long organisationId) {
        if (section.getType() == OVERVIEW_FINANCES) {
            return isFinanceOverviewSectionComplete(application);
        }

        return isMainSectionComplete(section, application, organisationId).andOnSuccess(
                mainSectionComplete -> {
                    // If there are child sections are they complete?
                    if (mainSectionComplete && section.hasChildSections()) {
                        for (final Section childSection : section.getChildSections()) {
                            final ServiceResult<Boolean> sectionComplete = isSectionComplete(childSection, application, organisationId);
                            if (sectionComplete.isFailure()) {
                                return sectionComplete;
                            } else if (!sectionComplete.getSuccess()) {
                                return serviceSuccess(false);
                            }

                        }
                    }
                    return serviceSuccess(mainSectionComplete);
                });
    }

    private ServiceResult<Boolean> isMainSectionComplete(Section section, Application application,
                                                         long organisationId) {
        for (Question question : section.getQuestions()) {
            if (question.isMarkAsCompletedEnabled()) {
                final ServiceResult<Boolean> markedAsComplete = questionStatusService.isMarkedAsComplete(question,
                        application.getId(), organisationId);
                if (markedAsComplete.isFailure()) {
                    return markedAsComplete;
                } else if (!markedAsComplete.getSuccess()) {
                    return serviceSuccess(false);
                }
            }
        }
        return serviceSuccess(true);
    }

    private ServiceResult<Boolean> isFinanceOverviewSectionComplete(Application application) {
        Section finances = application.getCompetition().getSections().stream().filter(section ->
                section.isType(SectionType.FINANCE)).findFirst().get();
        return sectionCompleteForAllOrganisations(finances, application).andOnSuccess(complete ->
        {
            if (complete) {
                return financeService.collaborativeFundingCriteriaMet(application.getId());
            }
            return serviceSuccess(false);
        });
    }

    @Override
    public ServiceResult<Boolean> sectionsCompleteForAllOrganisations(long applicationId) {
        return getApplication(applicationId).andOnSuccess(application -> {
            List <Section> sections = sectionRepository.findByCompetitionIdOrderByParentSectionIdAscPriorityAsc(application.getCompetition().getId());
            for (Section section : sections) {
                ServiceResult<Boolean> sectionComplete = sectionCompleteForAllOrganisations(section, application);
                if (sectionComplete.isFailure()) {
                    return sectionComplete;
                }
                if (!sectionComplete.getSuccess()) {
                    return serviceSuccess(false);
                }
            }
            return serviceSuccess(true);
        });
    }

    private ServiceResult<Boolean> sectionCompleteForAllOrganisations(Section section, Application application) {
        List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
        Set<Long> organisations = simpleMapSet(applicantTypeProcessRoles, ProcessRole::getOrganisationId);
        for (Long organisationId : organisations) {
            ServiceResult<Boolean> sectionComplete = isSectionComplete(section, application, organisationId);
            if (sectionComplete.isFailure()) {
                return sectionComplete;
            }
            if (!sectionComplete.getSuccess()) {
                return serviceSuccess(false);
            }
        }
        return serviceSuccess(true);
    }

    private Map<Long, Set<Long>> completedSections(Application application) {
        List<Section> sections = application.getCompetition().getSections();
        List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
        Set<Long> organisations = simpleMapSet(applicantTypeProcessRoles, ProcessRole::getOrganisationId);

        Map<Long, Set<Long>> organisationMap = new HashMap<>();

        for (Long organisationId : organisations) {
            Set<Long> completedSections = new LinkedHashSet<>();
            for (Section section : sections) {
                if (this.isSectionComplete(section, application, organisationId).getSuccess()) {
                    completedSections.add(section.getId());
                }
            }
            organisationMap.put(organisationId, completedSections);
        }
        return organisationMap;
    }
}