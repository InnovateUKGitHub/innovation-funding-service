package org.innovateuk.ifs.application.viewmodel.researchCategory;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;

public class ResearchCategorySummaryViewModel extends AbstractLeadOnlyViewModel {

    private String researchCategory;
    private boolean useSelectState;

    public ResearchCategorySummaryViewModel(Long applicationId,
                                            Long questionId,
                                            String researchCategory,
                                            boolean useSelectState,
                                            boolean closed,
                                            boolean complete,
                                            boolean canMarkAsComplete,
                                            boolean allReadonly) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete, allReadonly);
        this.researchCategory = researchCategory;
        this.useSelectState = useSelectState;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    public boolean getUseSelectState() {
        return useSelectState;
    }

    @Override
    public boolean isSummary() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ResearchCategorySummaryViewModel that = (ResearchCategorySummaryViewModel) o;

        return new EqualsBuilder()
                .append(researchCategory, that.researchCategory)
                .append(useSelectState, that.useSelectState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(researchCategory)
                .append(useSelectState)
                .toHashCode();
    }
}
