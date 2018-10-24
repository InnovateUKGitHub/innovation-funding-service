package org.innovateuk.ifs.application.forms.researchcategory.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;

import java.util.List;

/**
 * View Model for a Research category selection overview.
 */
public class ResearchCategoryViewModel extends AbstractLeadOnlyViewModel {

    private String applicationName;
    private List<ResearchCategoryResource> availableResearchCategories;
    private boolean hasApplicationFinances;
    private String researchCategory;
    private boolean userLeadApplicant;
    private String leadApplicantName;

    public ResearchCategoryViewModel(String applicationName,
                                     Long applicationId,
                                     Long questionId,
                                     List<ResearchCategoryResource> availableResearchCategories,
                                     boolean hasApplicationFinances,
                                     String researchCategory,
                                     boolean closed,
                                     boolean complete,
                                     boolean canMarkAsComplete,
                                     boolean allReadonly,
                                     boolean userLeadApplicant,
                                     String leadApplicantName) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete, allReadonly);
        this.applicationName = applicationName;
        this.availableResearchCategories = availableResearchCategories;
        this.hasApplicationFinances = hasApplicationFinances;
        this.researchCategory = researchCategory;
        this.userLeadApplicant = userLeadApplicant;
        this.leadApplicantName = leadApplicantName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public List<ResearchCategoryResource> getAvailableResearchCategories() {
        return availableResearchCategories;
    }

    public boolean getHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    public boolean isUserLeadApplicant() {
        return userLeadApplicant;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public boolean getUseSelectState() {
        return availableResearchCategories.size() > 1;
    }

    @Override
    public boolean isSummary() {
        return false;
    }
}
