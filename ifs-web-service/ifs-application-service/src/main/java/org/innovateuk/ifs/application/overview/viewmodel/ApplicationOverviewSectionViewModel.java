package org.innovateuk.ifs.application.overview.viewmodel;

import java.util.Set;

/**
 * View model for the application overview - section
 */
public class ApplicationOverviewSectionViewModel {

    private final long id;
    private final String title;
    private final String subTitle;
    private final Set<ApplicationOverviewRowViewModel> rows;
    private final boolean termsAndConditions;

    public ApplicationOverviewSectionViewModel(long id, String title, String subTitle, Set<ApplicationOverviewRowViewModel> rows, boolean isTersmsAndConditions) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.rows = rows;
        this.termsAndConditions = isTersmsAndConditions;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public Set<ApplicationOverviewRowViewModel> getRows() {
        return rows;
    }

    public boolean isTermsAndConditions() { return termsAndConditions; }
}
