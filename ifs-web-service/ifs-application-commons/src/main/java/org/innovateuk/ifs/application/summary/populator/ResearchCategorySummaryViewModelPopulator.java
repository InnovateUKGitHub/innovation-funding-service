package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.application.summary.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ResearchCategorySummaryViewModelPopulator implements QuestionSummaryViewModelPopulator {

    @Override
    public NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationSummaryData data) {
        return new ResearchCategorySummaryViewModel(data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.RESEARCH_CATEGORY);
    }
}
