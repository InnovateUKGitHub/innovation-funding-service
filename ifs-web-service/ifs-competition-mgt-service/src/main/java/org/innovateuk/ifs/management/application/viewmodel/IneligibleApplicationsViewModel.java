package org.innovateuk.ifs.management.application.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

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
                                           Pagination pagination,
                                           boolean readOnly) {
        super(competitionId, competitionName, applications, pagination, sorting, filter);
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
