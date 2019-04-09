package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.summary.viewmodel.NewApplicationSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewSectionSummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class NewApplicationSummaryViewModelPopulator {

    private ApplicationRestService applicationRestService;

    private SectionRestService sectionRestService;

    private QuestionRestService questionRestService;

    private FinanceSummaryViewModelPopulator financeSummaryViewModelPopulator;

    private Map<QuestionSetupType, QuestionSummaryViewModelPopulator> populatorMap;

    public NewApplicationSummaryViewModelPopulator(ApplicationRestService applicationRestService, SectionRestService sectionRestService, QuestionRestService questionRestService, FinanceSummaryViewModelPopulator financeSummaryViewModelPopulator, List<QuestionSummaryViewModelPopulator> populators) {
        this.applicationRestService = applicationRestService;
        this.sectionRestService = sectionRestService;
        this.questionRestService = questionRestService;
        this.financeSummaryViewModelPopulator = financeSummaryViewModelPopulator;
        this.populatorMap = new HashMap<>();
        populators.forEach(populator ->
            populator.questionTypes().forEach(type -> populatorMap.put(type, populator)));
    }

    public NewApplicationSummaryViewModel populate(long applicationId, Settings settings, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<QuestionResource> questions = questionRestService.findByCompetition(application.getCompetition()).getSuccess();
        Set<NewSectionSummaryViewModel> sectionViews = sectionRestService.getByCompetition(application.getCompetition()).getSuccess()
                .stream()
                .filter(section -> section.getParentSection() == null)
                .map(section -> sectionView(section, settings, new Data(questions, application, user)))
                .collect(toCollection(LinkedHashSet::new));

        return new NewApplicationSummaryViewModel(sectionViews);
    }

    private NewSectionSummaryViewModel sectionView(SectionResource section, Settings settings, Data data) {
        if (!section.getChildSections().isEmpty()) {
            return sectionsWithChildren(section, settings, data);
        }
        Set<NewQuestionSummaryViewModel> questionViews = section.getQuestions()
                .stream()
                .map(questionId -> data.getQuestions().get(questionId))
                .map(question ->  populateQuestionViewModel(question, data.getApplication()))
                .collect(toCollection(LinkedHashSet::new));
        return new NewSectionSummaryViewModel(section.getName(), questionViews);
    }

    //Currently only the finance section has child sections.
    private NewSectionSummaryViewModel sectionsWithChildren(SectionResource section, Settings settings, Data data) {
        NewQuestionSummaryViewModel finance = financeSummaryViewModelPopulator.populate(data.getApplication(), data.getUser());
        return new NewSectionSummaryViewModel(section.getName(), asSet(finance));
    }

    static class Settings {
        boolean includeStatuses;
        boolean includeQuestionLinks;
    }

    public static Settings settings() {
        return new Settings();
    }

    class Data {
        private Map<Long, QuestionResource> questions;
        private ApplicationResource application;
        private UserResource user;

        public Data(List<QuestionResource> questions, ApplicationResource application, UserResource user) {
            this.questions = questions.stream()
                .collect(toMap(QuestionResource::getId, Function.identity()));
            this.application = application;
            this.user = user;
        }

        public Map<Long, QuestionResource> getQuestions() {
            return questions;
        }

        public ApplicationResource getApplication() {
            return application;
        }

        public UserResource getUser() {
            return user;
        }
    }

    public NewQuestionSummaryViewModel populateQuestionViewModel(QuestionResource question, ApplicationResource application) {
        if (populatorMap.containsKey(question.getQuestionSetupType())) {
            return populatorMap.get(question.getQuestionSetupType()).populate(question, application);
        } else {
            return new NewQuestionSummaryViewModel() {
                @Override
                public String getName() {
                    return question.getShortName();
                }

                @Override
                public String getFragment() {
                    return "empty";
                }
            };
        }
    }


}
