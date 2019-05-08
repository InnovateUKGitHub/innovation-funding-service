package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.ResearchCategoryReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

@Component
public class ResearchCategoryReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<ResearchCategoryReadOnlyViewModel> {

    @Override
    public ResearchCategoryReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        return new ResearchCategoryReadOnlyViewModel(data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return asSet(QuestionSetupType.RESEARCH_CATEGORY);
    }
}
