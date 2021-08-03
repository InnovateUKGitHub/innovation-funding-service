package org.innovateuk.ifs.application.readonly.populator;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationSectionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.SupporterAssignmentReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toCollection;

@Component
public class ApplicationReadOnlyViewModelPopulator extends AsyncAdaptor {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private FormInputResponseRestService formInputResponseRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private FinanceReadOnlyViewModelPopulator financeSummaryViewModelPopulator;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProcessRoleRestService processRoleRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private SupporterAssignmentRestService supporterAssignmentRestService;

    private Map<QuestionSetupType, QuestionReadOnlyViewModelPopulator<?>> populatorMap;

    @Autowired
    public void setPopulatorMap(List<QuestionReadOnlyViewModelPopulator<?>> populators) {
        this.populatorMap = new HashMap<>();
        populators.forEach(populator ->
                populator.questionTypes().forEach(type -> populatorMap.put(type, populator)));
    }

    public ApplicationReadOnlyViewModel populate(long applicationId, UserResource user, ApplicationReadOnlySettings settings) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        return populate(application, competition, user, settings);
    }

    public ApplicationReadOnlyViewModel populate(ApplicationResource application, CompetitionResource competition, UserResource user, ApplicationReadOnlySettings settings) {
        Future<List<QuestionResource>> questionsFuture = async(() -> questionRestService.findByCompetition(application.getCompetition()).getSuccess());
        Future<List<FormInputResource>> formInputsFuture = async(() -> formInputRestService.getByCompetitionId(competition.getId()).getSuccess());
        Future<List<FormInputResponseResource>> formInputResponsesFuture = async(() -> formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess());
        Future<List<QuestionStatusResource>> questionStatusesFuture = async(() -> getQuestionStatuses(application, user, settings));
        Future<List<SectionResource>> sectionsFuture = async(() -> sectionRestService.getByCompetition(application.getCompetition()).getSuccess());
        Future<List<ProcessRoleResource>> processRolesFuture = async(() -> getProcessRoles(application));
        Future<List<ApplicationAssessmentResource>> assessorResponseFuture = async(() -> getAssessmentResponses(application, settings));
        Future<List<SupporterAssignmentResource>> supporterResponseFuture = async(() -> getSupporterFeedbackResponses(application, settings));

        List<ProcessRoleResource> processRoles = resolve(processRolesFuture);

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, processRoles,
                resolve(questionsFuture), resolve(formInputsFuture), resolve(formInputResponsesFuture), resolve(questionStatusesFuture),
                resolve(assessorResponseFuture), resolve(supporterResponseFuture));

        if (settings.isIncludeAllAssessorFeedback()) {
            settings.setIncludeAllAssessorFeedback(data.getAssessmentToApplicationAssessment().size() > 0);
        }

        if (settings.isIncludeAllSupporterFeedback()) {
            settings.setIncludeAllSupporterFeedback(data.getFeedbackToApplicationSupport().size() > 0);
        }

        Set<ApplicationSectionReadOnlyViewModel> sectionViews = resolve(sectionsFuture)
                .stream()
                .filter(section -> section.getParentSection() == null)
                .filter(section -> settings.isIncludeAllAssessorFeedback() || section.getType() != SectionType.KTP_ASSESSMENT)
                .map(section -> async(() -> sectionView(section, settings, data)))
                .map(this::resolve)
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationReadOnlyViewModel(settings,
                sectionViews,
                settings.isIncludeAllAssessorFeedback() ? data.getApplicationScore() : BigDecimal.ZERO,
                settings.isIncludeAllAssessorFeedback() ? data.getAssessmentToApplicationAssessment().values().stream()
                        .map(ApplicationAssessmentResource::getOverallFeedback).collect(Collectors.toList()) : emptyList(),
                settings.isIncludeAllSupporterFeedback() ? data.getFeedbackToApplicationSupport().values().stream()
                        .map(assignment -> new SupporterAssignmentReadOnlyViewModel(
                                assignment.getState().getStateName().toLowerCase(),
                                assignment.getComments(),
                                assignment.getUserSimpleOrganisation()))
                        .collect(Collectors.groupingBy(SupporterAssignmentReadOnlyViewModel::getState)) : emptyMap(),
                shouldDisplayKtpApplicationFeedback(competition, user, processRoles),
                competition.isKtp(),
                competition.getTermsAndConditions().isThirdPartyProcurement(),
                competition.getCompetitionThirdPartyConfigResource()
        );
    }

    private boolean shouldDisplayKtpApplicationFeedback(CompetitionResource competition, UserResource user, List<ProcessRoleResource> processRoles) {
        boolean isKta = processRoles.stream()
                .anyMatch(pr -> pr.getUser().equals(user.getId()) && pr.getRole().isKta());
        return competition.isKtp() && isKta;
    }

    private ApplicationSectionReadOnlyViewModel sectionView(SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        if (!section.getChildSections().isEmpty()) {
            return sectionWithChildren(section, settings, data);
        }
        Set<ApplicationQuestionReadOnlyViewModel> questionViews = section.getQuestions()
                .stream()
                .map(questionId -> data.getQuestionIdToQuestion().get(questionId))
                .map(question ->  populateQuestionViewModel(question, data, settings))
                .collect(toCollection(LinkedHashSet::new));
        return new ApplicationSectionReadOnlyViewModel(section.getName(), false, section.isTermsAndConditions(), questionViews);
    }

    //Currently only theA finance section has child sections.
    private ApplicationSectionReadOnlyViewModel sectionWithChildren(SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        ApplicationQuestionReadOnlyViewModel finance = financeSummaryViewModelPopulator.populate(data);
        return new ApplicationSectionReadOnlyViewModel(section.getName(), true, section.isTermsAndConditions(), ImmutableSet.of(finance));
    }

    private ApplicationQuestionReadOnlyViewModel populateQuestionViewModel(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        if (populatorMap.containsKey(question.getQuestionSetupType())) {
            return populatorMap.get(question.getQuestionSetupType()).populate(question, data, settings);
        } else {
            throw new IFSRuntimeException("Populator not found for question type: " + question.getQuestionSetupType().name());
        }
    }

    private List<ProcessRoleResource> getProcessRoles(ApplicationResource application) {
        return processRoleRestService.findProcessRole(application.getId()).getSuccess();
    }

    private List<QuestionStatusResource> getQuestionStatuses(ApplicationResource application, UserResource user, ApplicationReadOnlySettings settings) {
        if (!settings.isIncludeStatuses()) {
            return emptyList();
        }
        long organisationId;
        if (user.hasRole(Role.APPLICANT)) {
            organisationId = organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess().getId();
        } else {
            organisationId = application.getLeadOrganisationId();
        }
        return questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisationId).getSuccess();
    }

    private List<ApplicationAssessmentResource> getAssessmentResponses(ApplicationResource application, ApplicationReadOnlySettings settings) {
        if (!settings.isIncludeAssessment()) {
            return emptyList();
        }
        if (settings.getAssessmentId() != null) {
            return singletonList(
                    assessorFormInputResponseRestService.getApplicationAssessment(application.getId(), settings.getAssessmentId()).getSuccess()
            );
        }
        if (settings.isIncludeAllAssessorFeedback()) {
            return assessorFormInputResponseRestService.getApplicationAssessments(application.getId()).getSuccess().getAssessments();
        }
        return emptyList();
    }

    private List<SupporterAssignmentResource> getSupporterFeedbackResponses(ApplicationResource application, ApplicationReadOnlySettings settings) {
        if (settings.isIncludeAllSupporterFeedback()) {
            return supporterAssignmentRestService.getAssignmentsByApplicationId(application.getId()).getSuccess();
        }

        return emptyList();
    }
}
