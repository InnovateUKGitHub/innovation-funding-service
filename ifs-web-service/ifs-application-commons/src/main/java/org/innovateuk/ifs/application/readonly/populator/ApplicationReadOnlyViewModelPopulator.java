package org.innovateuk.ifs.application.readonly.populator;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationSectionReadOnlyViewModel;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toCollection;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ApplicationReadOnlyViewModelPopulator extends AsyncAdaptor {

    private ApplicationRestService applicationRestService;

    private CompetitionRestService competitionRestService;

    private FormInputRestService formInputRestService;

    private FormInputResponseRestService formInputResponseRestService;

    private SectionRestService sectionRestService;

    private QuestionRestService questionRestService;

    private FinanceReadOnlyViewModelPopulator financeSummaryViewModelPopulator;

    private QuestionStatusRestService questionStatusRestService;

    private OrganisationRestService organisationRestService;

    private Map<QuestionSetupType, QuestionReadOnlyViewModelPopulator<?>> populatorMap;

    public ApplicationReadOnlyViewModelPopulator(ApplicationRestService applicationRestService, CompetitionRestService competitionRestService, FormInputRestService formInputRestService, FormInputResponseRestService formInputResponseRestService, SectionRestService sectionRestService, QuestionRestService questionRestService, FinanceReadOnlyViewModelPopulator financeSummaryViewModelPopulator, QuestionStatusRestService questionStatusRestService, OrganisationRestService organisationRestService, List<QuestionReadOnlyViewModelPopulator<?>> populators) {
        this.applicationRestService = applicationRestService;
        this.competitionRestService = competitionRestService;
        this.formInputRestService = formInputRestService;
        this.formInputResponseRestService = formInputResponseRestService;
        this.sectionRestService = sectionRestService;
        this.questionRestService = questionRestService;
        this.financeSummaryViewModelPopulator = financeSummaryViewModelPopulator;
        this.questionStatusRestService = questionStatusRestService;
        this.organisationRestService = organisationRestService;
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
        Future<List<QuestionStatusResource>> questionStatusesFuture = getQuestionStatuses(application, user, settings);
        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, user, resolve(questionsFuture), resolve(formInputsFuture), resolve(formInputResponsesFuture), resolve(questionStatusesFuture));

        Set<ApplicationSectionReadOnlyViewModel> sectionViews = sectionRestService.getByCompetition(application.getCompetition()).getSuccess()
                .stream()
                .filter(section -> section.getParentSection() == null)
                .map(section -> async(() -> sectionView(section, settings, data)))
                .map(this::resolve)
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationReadOnlyViewModel(settings, sectionViews);
    }

    private ApplicationSectionReadOnlyViewModel sectionView(SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        if (!section.getChildSections().isEmpty()) {
            return sectionsWithChildren(section, settings, data);
        }
        Set<ApplicationQuestionReadOnlyViewModel> questionViews = section.getQuestions()
                .stream()
                .map(questionId -> data.getQuestionIdToQuestion().get(questionId))
                .map(question ->  populateQuestionViewModel(question, data, settings))
                .collect(toCollection(LinkedHashSet::new));
        return new ApplicationSectionReadOnlyViewModel(section.getName(), questionViews);
    }

    //Currently only the finance section has child sections.
    private ApplicationSectionReadOnlyViewModel sectionsWithChildren(SectionResource section, ApplicationReadOnlySettings settings, ApplicationReadOnlyData data) {
        ApplicationQuestionReadOnlyViewModel finance = financeSummaryViewModelPopulator.populate(data);
        return new ApplicationSectionReadOnlyViewModel(section.getName(), asSet(finance));
    }

    public ApplicationQuestionReadOnlyViewModel populateQuestionViewModel(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        if (populatorMap.containsKey(question.getQuestionSetupType())) {
            return populatorMap.get(question.getQuestionSetupType()).populate(question, data);
        } else {
            throw new IFSRuntimeException("Populator not found for question type: " + question.getQuestionSetupType().name());
        }
    }

    private Future<List<QuestionStatusResource>> getQuestionStatuses(ApplicationResource application, UserResource user, ApplicationReadOnlySettings settings) {
        if (!settings.isIncludeStatuses()) {
            return ConcurrentUtils.constantFuture(Collections.emptyList());
        }
        return async(() ->{
            OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess();
            return questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId()).getSuccess();
        });
    }


}
