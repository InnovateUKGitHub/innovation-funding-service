package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.ApplicationDetailsSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ApplicationDetailsSummaryViewModelPopulator implements QuestionSummaryViewModelPopulator {
    @Autowired
    private CompetitionRestService competitionRestService;

    @Override
    public NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationResource application) {
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        return new ApplicationDetailsSummaryViewModel(question, application, competition);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.APPLICATION_DETAILS);
    }
}
