package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamReadOnlyViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationQuestionReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ApplicationTeamReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator {

    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationTeamReadOnlyViewModelPopulator(ApplicationSummaryRestService applicationSummaryRestService) {
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    @Override
    public ApplicationQuestionReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        return new ApplicationTeamReadOnlyViewModel(applicationSummaryRestService.getApplicationTeam(data.getApplication().getId()).getSuccess(), data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.APPLICATION_TEAM);
    }
}
