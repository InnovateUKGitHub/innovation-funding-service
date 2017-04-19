package org.innovateuk.ifs.management.viewmodel;

import javafx.scene.control.Pagination;

import java.util.List;

/**
 * View model for the Ineligible Competition Management Applications page
 */
public class IneligibleApplicationsViewModel extends BaseApplicationsViewModel<IneligibleApplicationsRowViewModel> {

    public IneligibleApplicationsViewModel(long competitionId,
                                           String competitionName,
                                           String sorting,
                                           String filter,
                                           List<IneligibleApplicationsRowViewModel> applications,
                                           PaginationViewModel pagination) {
        super(competitionId, competitionName, applications, pagination, sorting, filter);
    }
}
