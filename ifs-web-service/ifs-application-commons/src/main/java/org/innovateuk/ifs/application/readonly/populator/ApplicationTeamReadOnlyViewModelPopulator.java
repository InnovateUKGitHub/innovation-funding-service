package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationTeamReadOnlyViewModel;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class ApplicationTeamReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<ApplicationTeamReadOnlyViewModel> {

    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationTeamReadOnlyViewModelPopulator(ApplicationSummaryRestService applicationSummaryRestService) {
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    @Override
    public ApplicationTeamReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new ApplicationTeamReadOnlyViewModel(applicationSummaryRestService.getApplicationTeam(data.getApplication().getId()).getSuccess(), data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.APPLICATION_TEAM);
    }
}
