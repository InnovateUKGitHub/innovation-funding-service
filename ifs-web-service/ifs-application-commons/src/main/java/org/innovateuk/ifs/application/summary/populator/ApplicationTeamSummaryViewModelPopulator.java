package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationTeamSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ApplicationTeamSummaryViewModelPopulator implements QuestionSummaryViewModelPopulator {

    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationTeamSummaryViewModelPopulator(ApplicationSummaryRestService applicationSummaryRestService) {
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    @Override
    public NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationSummaryData data) {
        return new ApplicationTeamSummaryViewModel(applicationSummaryRestService.getApplicationTeam(data.getApplication().getId()).getSuccess(), data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.APPLICATION_TEAM);
    }
}
