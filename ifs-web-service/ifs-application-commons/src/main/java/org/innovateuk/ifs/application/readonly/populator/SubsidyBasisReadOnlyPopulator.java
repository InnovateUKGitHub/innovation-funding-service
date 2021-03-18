package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.SubsidyBasisReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;

@Component
public class SubsidyBasisReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<SubsidyBasisReadOnlyViewModel> {

    @Override
    public SubsidyBasisReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new SubsidyBasisReadOnlyViewModel(
                data,
                question
        );
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(SUBSIDY_BASIS);
    }
}