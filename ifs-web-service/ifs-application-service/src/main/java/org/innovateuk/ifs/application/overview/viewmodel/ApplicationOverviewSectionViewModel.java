package org.innovateuk.ifs.application.overview.viewmodel;

import java.util.Set;

/**
 * View model for the application overview - section
 */
public class ApplicationOverviewSectionViewModel {

    private final long id;
    private final String title;
    private final Set<ApplicationOverviewRowViewModel> rows;

    public ApplicationOverviewSectionViewModel(long id, String title, Set<ApplicationOverviewRowViewModel> rows) {
        this.id = id;
        this.title = title;
        this.rows = rows;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<ApplicationOverviewRowViewModel> getRows() {
        return rows;
    }
}
