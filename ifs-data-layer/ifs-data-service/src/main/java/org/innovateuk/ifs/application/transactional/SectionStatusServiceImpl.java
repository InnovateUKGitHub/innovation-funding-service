package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.apache.commons.lang3.tuple.Pair.of;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Implements {@link SectionStatusService}
 */
@Service
public class SectionStatusServiceImpl extends BaseTransactionalService implements SectionStatusService {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionStatusService questionStatusService;

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
                            isSectionComplete(section, applicationId, organisationId).andOnSuccessReturn(isComplete -> of(section.getId(), isComplete)));

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

    private ServiceResult<Boolean> isSectionComplete(Section section, long applicationId, long organisationId) {
        return isMainSectionComplete(section, applicationId, organisationId).andOnSuccess(
                mainSectionComplete -> {
                    // If there are child sections are they complete?
                    if (mainSectionComplete && section.hasChildSections()) {
                        for (final Section childSection : section.getChildSections()) {
                            final ServiceResult<Boolean> sectionComplete = isSectionComplete(childSection, applicationId, organisationId);
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

    private ServiceResult<Boolean> isMainSectionComplete(Section section, long applicationId, long organisationId) {

        for (Question question : section.getQuestions()) {

            if (question.isMarkAsCompletedEnabled()) {
                final ServiceResult<Boolean> markedAsComplete = questionStatusService.isMarkedAsComplete(question, applicationId, organisationId);
                // if one of the questions is incomplete then the whole section is incomplete
                if (markedAsComplete.isFailure()) {
                    return markedAsComplete;
                } else if (!markedAsComplete.getSuccess()) {
                    return serviceSuccess(false);
                }
            }
        }
        return serviceSuccess(true);
    }


    @Override
    public ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, long applicationId, Section excludedSection) {
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
                if (!this.isMainSectionComplete(section, application.getId(), applicationFinance.getOrganisation().getId()).getSuccess()) {
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

    private Map<Long, Set<Long>> completedSections(Application application) {

        List<Section> sections = application.getCompetition().getSections();
        List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
        List<Long> organisations = simpleMap(applicantTypeProcessRoles, ProcessRole::getOrganisationId);

        Map<Long, Set<Long>> organisationMap = new HashMap<>();

        for (Long organisationId : organisations) {
            Set<Long> completedSections = new LinkedHashSet<>();
            for (Section section : sections) {
                if (this.isSectionComplete(section, application.getId(), organisationId).getSuccess()) {
                    completedSections.add(section.getId());
                }
            }
            organisationMap.put(organisationId, completedSections);
        }
        return organisationMap;
    }



}