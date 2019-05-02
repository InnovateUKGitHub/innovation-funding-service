package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class ResearchCategorySummaryViewModel extends AbstractQuestionSummaryViewModel implements NewQuestionSummaryViewModel {

    private final String researchCategory;

    public ResearchCategorySummaryViewModel(ApplicationSummaryData data, QuestionResource question) {
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
