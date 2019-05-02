package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class ResearchCategoryReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel implements ApplicationQuestionReadOnlyViewModel {

    private final String researchCategory;

    public ResearchCategoryReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        super(data, question);
        this.researchCategory = data.getApplication().getResearchCategory().getName();
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    @Override
    public String getFragment() {
        return "research-category";
    }

}
