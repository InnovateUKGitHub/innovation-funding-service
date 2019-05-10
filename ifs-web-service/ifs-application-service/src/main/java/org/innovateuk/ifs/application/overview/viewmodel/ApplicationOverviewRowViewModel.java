package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;

import java.util.Optional;

/**
 * View model for each row with a link in the application overview page.
 */
public class ApplicationOverviewRowViewModel {

    private final String title;
    private final String url;
    private final boolean complete;
    private final Optional<AssignButtonsViewModel> assignButtonsViewModel;

    public ApplicationOverviewRowViewModel(String title, String url, boolean complete, Optional<AssignButtonsViewModel> assignButtonsViewModel) {
        this.title = title;
        this.url = url;
        this.complete = complete;
        this.assignButtonsViewModel = assignButtonsViewModel;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isComplete() {
        return complete;
    }

    public Optional<AssignButtonsViewModel> getAssignButtonsViewModel() {
        return assignButtonsViewModel;
    }

    public boolean isAssignable() {
        return assignButtonsViewModel.isPresent();
    }
}
