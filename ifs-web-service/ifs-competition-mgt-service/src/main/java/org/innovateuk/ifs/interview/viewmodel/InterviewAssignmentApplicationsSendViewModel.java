package org.innovateuk.ifs.interview.viewmodel;

import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Send' applications view.
 */
public class InterviewAssignmentApplicationsSendViewModel extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationInviteRowViewModel> {

    private final String content;

    public InterviewAssignmentApplicationsSendViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewAssignmentApplicationInviteRowViewModel> applications,
            int applicationsInCompetition,
            int applicationsInPanel,
            PaginationViewModel pagination,
            String originQuery,
            String content) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, applicationsInCompetition,
                applicationsInPanel, pagination, originQuery);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}