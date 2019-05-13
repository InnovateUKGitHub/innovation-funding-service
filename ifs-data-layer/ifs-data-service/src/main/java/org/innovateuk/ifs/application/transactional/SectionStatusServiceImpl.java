package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
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

    @Override
    public ServiceResult<Map<Long, Set<Long>>> getCompletedSections(final long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(this::completedSections);
    }

    @Override
    public ServiceResult<Set<Long>> getCompletedSections(final long applicationId, final long organisationId) {
        return find(application(applicationId)).
                andOnSuccess(application -> {

                    List<Section> sections = application.getCompetition().getSections();
                    List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
                    Set<Long> organisations = simpleMapSet(applicantTypeProcessRoles, ProcessRole::getOrganisationId);

                    return serviceSuccess(sections.stream()
                            .filter(section -> isSectionComplete(section, getCompletedQuestionsGroupedByOrganisationId(applicationId), application, organisationId, organisations))
                            .map(Section::getId)
                            .collect(toSet()));
                });
    }

    private boolean isFinanceOverviewComplete(Application application, Map<Long, List<Long>> completedQuestionsByOrganisations, Set<Long> applicationOrganisations) {
        List<Section> sections = application.getCompetition().getSections();

        Section financeSection = sections.stream().filter(section -> section.getType() == FINANCE).collect(toList()).get(0);

        for (long organisationId : applicationOrganisations) {
            if (!completedQuestionsByOrganisations.containsKey(organisationId)) {
                return false;
            }

            Map<Long, List<Long>> map = new HashMap<>();
            map.put(organisationId, completedQuestionsByOrganisations.get(organisationId));

            if (!isSectionComplete(
                    financeSection,
                    map,
                    application,
                    organisationId,
                    applicationOrganisations)) {
                return false;
            }
        }

        return financeService.collaborativeFundingCriteriaMet(application.getId()).getSuccess();
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
    public ServiceResult<Boolean> sectionsCompleteForAllOrganisations(long applicationId) {

        return getApplication(applicationId).andOnSuccess(application -> {

            Set<Long> sections = sectionService.getByCompetitionId(application.getCompetition().getId()).getSuccess()
                    .stream()
                    .map(SectionResource::getId)
                    .collect(toSet());

            Set<Long> completedSections = getCompletedSections(applicationId).getSuccess()
                    .values()
                    .stream()
                    .flatMap(Set::stream)
                    .collect(toSet());

            return serviceSuccess(sections.equals(completedSections));
        });
    }

    private Map<Long, Set<Long>> completedSections(Application application) {

        List<Section> sections = application.getCompetition().getSections();
        List<ProcessRole> applicantTypeProcessRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
        Set<Long> organisations = simpleMapSet(applicantTypeProcessRoles, ProcessRole::getOrganisationId);

        Map<Long, Set<Long>> organisationMap = new HashMap<>();

        for (Long organisationId : organisations) {
            Set<Long> completedSections = new LinkedHashSet<>();
            for (Section section : sections) {
                if (isSectionComplete(section, getCompletedQuestionsGroupedByOrganisationId(application.getId()), application, organisationId, organisations)) {
                    completedSections.add(section.getId());
                }
            }
            organisationMap.put(organisationId, completedSections);
        }
        return organisationMap;
    }

    private boolean isSectionComplete(Section section,
                                      Map<Long, List<Long>> completedQuestionsByOrganisations,
                                      Application application,
                                      long organisationId,
                                      Set<Long> applicationOrganisations) {

        if (section.getType() == OVERVIEW_FINANCES) {
            return isFinanceOverviewComplete(application, completedQuestionsByOrganisations, applicationOrganisations);
        }

        if (section.hasChildSections()) {
            for (Section childSection : section.getChildSections()) {
                return isSectionComplete(childSection, completedQuestionsByOrganisations, application, organisationId, applicationOrganisations);
            }
        }

        for (Question question : section.getQuestions()) {
            if (!completedQuestionsByOrganisations.containsKey(organisationId)
                    || !completedQuestionsByOrganisations.get(organisationId).contains((question.getId()))) {
                return false;
            }
        }

        return true;
    }

    private Map<Long, List<Long>> getCompletedQuestionsGroupedByOrganisationId(long applicationId) {

        Map<Long, List<QuestionStatusResource>> completedQuestionStatuses = questionStatusService.findCompletedQuestionsByApplicationId(applicationId).getSuccess()
                .stream()
                .collect(Collectors.groupingBy(qs -> qs.getCompletedByOrganisation()));

        return completedQuestionStatuses
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey,
                        questionStatuses -> questionStatuses.getValue()
                                .stream()
                                .map(QuestionStatusResource::getQuestion)
                                .collect(toList())
                ));
    }
}