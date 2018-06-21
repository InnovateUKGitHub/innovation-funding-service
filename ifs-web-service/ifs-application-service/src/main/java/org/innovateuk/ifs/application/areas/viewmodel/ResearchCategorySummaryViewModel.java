package org.innovateuk.ifs.application.areas.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;

public class ResearchCategorySummaryViewModel extends AbstractLeadOnlyViewModel {

    private long applicationId;
    private String researchCategory;

    public ResearchCategorySummaryViewModel(long applicationId,
                                            String researchCategory,
                                            boolean closed,
                                            boolean complete,
                                            boolean canMarkAsComplete) {
        super(closed, complete, canMarkAsComplete);
        this.applicationId = applicationId;
        this.researchCategory = researchCategory;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    @Override
    public boolean isSummary() {
        return true;
    }
}
