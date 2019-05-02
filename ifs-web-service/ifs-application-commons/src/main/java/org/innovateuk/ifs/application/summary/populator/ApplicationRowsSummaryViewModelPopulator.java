package org.innovateuk.ifs.application.summary.populator;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.ApplicationSummarySettings;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowsSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationRowGroupSummaryViewModel;
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
public class ApplicationRowsSummaryViewModelPopulator extends AsyncAdaptor {

    private ApplicationRestService applicationRestService;

    private CompetitionRestService competitionRestService;

    private FormInputRestService formInputRestService;

    private FormInputResponseRestService formInputResponseRestService;

    private SectionRestService sectionRestService;

    private QuestionRestService questionRestService;

    private FinanceSummaryViewModelPopulator financeSummaryViewModelPopulator;

    private QuestionStatusRestService questionStatusRestService;

    private OrganisationRestService organisationRestService;

    private Map<QuestionSetupType, QuestionSummaryViewModelPopulator<?>> populatorMap;

    public ApplicationRowsSummaryViewModelPopulator(ApplicationRestService applicationRestService, CompetitionRestService competitionRestService, FormInputRestService formInputRestService, FormInputResponseRestService formInputResponseRestService, SectionRestService sectionRestService, QuestionRestService questionRestService, FinanceSummaryViewModelPopulator financeSummaryViewModelPopulator, QuestionStatusRestService questionStatusRestService, OrganisationRestService organisationRestService, List<QuestionSummaryViewModelPopulator<?>> populators) {
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

    public ApplicationRowsSummaryViewModel populate(long applicationId, UserResource user, ApplicationSummarySettings settings) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        return populate(application, competition, user, settings);
    }

    public ApplicationRowsSummaryViewModel populate(ApplicationResource application, CompetitionResource competition, UserResource user, ApplicationSummarySettings settings) {
        Future<List<QuestionResource>> questionsFuture = async(() -> questionRestService.findByCompetition(application.getCompetition()).getSuccess());
        Future<List<FormInputResource>> formInputsFuture = async(() -> formInputRestService.getByCompetitionId(competition.getId()).getSuccess());
        Future<List<FormInputResponseResource>> formInputResponsesFuture = async(() -> formInputResponseRestService.getResponsesByApplicationId(application.getId()).getSuccess());
        Future<List<QuestionStatusResource>> questionStatusesFuture = getQuestionStatuses(application, user, settings);
        ApplicationSummaryData data = new ApplicationSummaryData(application, competition, user, resolve(questionsFuture), resolve(formInputsFuture), resolve(formInputResponsesFuture), resolve(questionStatusesFuture));

        Set<ApplicationRowGroupSummaryViewModel> sectionViews = sectionRestService.getByCompetition(application.getCompetition()).getSuccess()
                .stream()
                .filter(section -> section.getParentSection() == null)
                .map(section -> async(() -> sectionView(section, settings, data)))
                .map(this::resolve)
                .collect(toCollection(LinkedHashSet::new));

        return new ApplicationRowsSummaryViewModel(settings, sectionViews);
    }

    private ApplicationRowGroupSummaryViewModel sectionView(SectionResource section, ApplicationSummarySettings settings, ApplicationSummaryData data) {
        if (!section.getChildSections().isEmpty()) {
            return sectionsWithChildren(section, settings, data);
        }
        Set<ApplicationRowSummaryViewModel> questionViews = section.getQuestions()
                .stream()
                .map(questionId -> data.getQuestionIdToQuestion().get(questionId))
                .map(question ->  populateQuestionViewModel(question, data, settings))
                .collect(toCollection(LinkedHashSet::new));
        return new ApplicationRowGroupSummaryViewModel(section.getName(), questionViews);
    }

    //Currently only the finance section has child sections.
    private ApplicationRowGroupSummaryViewModel sectionsWithChildren(SectionResource section, ApplicationSummarySettings settings, ApplicationSummaryData data) {
        ApplicationRowSummaryViewModel finance = financeSummaryViewModelPopulator.populate(data);
        return new ApplicationRowGroupSummaryViewModel(section.getName(), asSet(finance));
    }

    public ApplicationRowSummaryViewModel populateQuestionViewModel(QuestionResource question, ApplicationSummaryData data, ApplicationSummarySettings settings) {
        if (populatorMap.containsKey(question.getQuestionSetupType())) {
            return populatorMap.get(question.getQuestionSetupType()).populate(question, data);
        } else {
            throw new IFSRuntimeException("Populator not found for question type: " + question.getQuestionSetupType().name());
        }
    }

    private Future<List<QuestionStatusResource>> getQuestionStatuses(ApplicationResource application, UserResource user, ApplicationSummarySettings settings) {
        if (!settings.isIncludeStatuses()) {
            return ConcurrentUtils.constantFuture(Collections.emptyList());
        }
        return async(() ->{
            OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess();
            return questionStatusRestService.findByApplicationAndOrganisation(application.getId(), organisation.getId()).getSuccess();
        });
    }


}
