package org.innovateuk.ifs.application.summary.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.summary.viewmodel.NewQuestionSummaryViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.util.Set;

public interface QuestionSummaryViewModelPopulator {

    NewQuestionSummaryViewModel populate(QuestionResource question, ApplicationResource application);

    Set<QuestionSetupType> questionTypes();
}
