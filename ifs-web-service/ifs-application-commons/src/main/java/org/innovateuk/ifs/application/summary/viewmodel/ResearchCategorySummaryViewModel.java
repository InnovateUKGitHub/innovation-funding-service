package org.innovateuk.ifs.application.summary.viewmodel;

public class ResearchCategorySummaryViewModel implements NewQuestionSummaryViewModel {

    private final String researchCategory;
    private final String name;

    public ResearchCategorySummaryViewModel(String researchCategory, String name) {
        this.researchCategory = researchCategory;
        this.name = name;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFragment() {
        return "research-category";
    }
}
