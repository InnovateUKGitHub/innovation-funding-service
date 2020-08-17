package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationSectionReadOnlyViewModel;
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
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

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
    private UserRestService userRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

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
        Future<Optional<ProcessRoleResource>> processRoleFuture = async(() -> getProcessRole(application, user, settings));
        Future<List<ApplicationAssessmentResource>> assessorResponseFuture = async(() -> getAssessmentResponses(application, settings));
        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, resolve(processRoleFuture), resolve(questionsFuture), resolve(formInputsFuture), resolve(formInputResponsesFuture), resolve(questionStatusesFuture), resolve(assessorResponseFuture));

        Set<ApplicationSectionReadOnlyViewModel> sectionViews = resolve(sectionsFuture)
                .stream()
                .filter(section -> section.getParentSection() == null)
                .map(section -> async(() -> sectionView(competition, section, settings, data)))
                .map(this::resolve)
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationReadOnlyViewModel(settings, sectionViews);
    }

    private ApplicationSectionReadOnlyViewModel sectionView(CompetitionResource competition, SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        if (!section.getChildSections().isEmpty()) {
            return sectionWithChildren(section, settings, data);
        }
        Set<ApplicationQuestionReadOnlyViewModel> questionViews = section.getQuestions()
                .stream()
                .map(questionId -> data.getQuestionIdToQuestion().get(questionId))
                .map(question ->  populateQuestionViewModel(competition, question, data, settings))
                .collect(toCollection(LinkedHashSet::new));
        return new ApplicationSectionReadOnlyViewModel(section.getName(), false, questionViews);
    }

    //Currently only theA finance section has child sections.
    private ApplicationSectionReadOnlyViewModel sectionWithChildren(SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        ApplicationQuestionReadOnlyViewModel finance = financeSummaryViewModelPopulator.populate(data);
        return new ApplicationSectionReadOnlyViewModel(section.getName(), true, asSet(finance));
    }

    private ApplicationQuestionReadOnlyViewModel populateQuestionViewModel(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        if (populatorMap.containsKey(question.getQuestionSetupType())) {
            return populatorMap.get(question.getQuestionSetupType()).populate(competition, question, data, settings);
        } else {
            throw new IFSRuntimeException("Populator not found for question type: " + question.getQuestionSetupType().name());
        }
    }

    private Optional<ProcessRoleResource> getProcessRole(ApplicationResource application, UserResource user, ApplicationReadOnlySettings settings) {
        return userRestService.findProcessRole(user.getId(), application.getId()).getOptionalSuccessObject();
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
}
