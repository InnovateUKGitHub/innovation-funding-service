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
    private final AssignButtonsViewModel assignButtonsViewModel;
    private final boolean showStatus;

    public ApplicationOverviewRowViewModel(String title, String url, boolean complete, AssignButtonsViewModel assignButtonsViewModel, boolean showStatus) {
        this.title = title;
        this.url = url;
        this.complete = complete;
        this.assignButtonsViewModel = assignButtonsViewModel;
        this.showStatus = showStatus;
    }

    public ApplicationOverviewRowViewModel(String title, String url, boolean complete, boolean showStatus) {
        this.title = title;
        this.url = url;
        this.complete = complete;
        this.assignButtonsViewModel = null;
        this.showStatus = showStatus;
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
        return Optional.ofNullable(assignButtonsViewModel);
    }

    public boolean isAssignable() {
        return getAssignButtonsViewModel().isPresent();
    }

    public boolean isShowStatus() {
        return showStatus;
    }
}