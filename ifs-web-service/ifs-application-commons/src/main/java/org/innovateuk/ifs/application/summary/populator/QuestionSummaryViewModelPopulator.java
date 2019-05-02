package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.util.Set;

public interface QuestionSummaryViewModelPopulator<M extends NewQuestionSummaryViewModel> {

    M populate(QuestionResource question, ApplicationSummaryData data);

    Set<QuestionSetupType> questionTypes();
}
