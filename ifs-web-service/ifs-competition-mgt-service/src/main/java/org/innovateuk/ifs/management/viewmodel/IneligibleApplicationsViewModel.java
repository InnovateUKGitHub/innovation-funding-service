package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * View model for the Ineligible Competition Management Applications page
 */
public class IneligibleApplicationsViewModel extends BaseApplicationsViewModel<IneligibleApplicationsRowViewModel> {

    private boolean readOnly;

    public IneligibleApplicationsViewModel(long competitionId,
                                           String competitionName,
                                           String sorting,
                                           String filter,
                                           List<IneligibleApplicationsRowViewModel> applications,
                                           PaginationViewModel pagination,
                                           boolean readOnly) {
        super(competitionId, competitionName, applications, pagination, sorting, filter);
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
