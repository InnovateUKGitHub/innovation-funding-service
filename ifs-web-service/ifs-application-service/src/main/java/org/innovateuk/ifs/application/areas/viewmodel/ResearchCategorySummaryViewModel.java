package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;

public class ResearchCategorySummaryViewModel extends AbstractLeadOnlyViewModel {

    private String researchCategory;

    public ResearchCategorySummaryViewModel(Long applicationId,
                                            Long questionId,
                                            String researchCategory,
                                            boolean closed,
                                            boolean complete,
                                            boolean canMarkAsComplete) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete);
        this.researchCategory = researchCategory;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    @Override
    public boolean isSummary() {
        return true;
    }
}
