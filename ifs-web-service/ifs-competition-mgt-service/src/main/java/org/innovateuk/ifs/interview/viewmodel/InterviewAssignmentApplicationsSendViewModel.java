package org.innovateuk.ifs.interview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Holder of model attributes for the invite applications for Assessment Interview Panel 'Send' applications view.
 */
public class InterviewAssignmentApplicationsSendViewModel extends InterviewAssignmentApplicationsViewModel<InterviewAssignmentApplicationInviteSendRowViewModel> {

    private final String content;

    public InterviewAssignmentApplicationsSendViewModel(
            long competitionId,
            String competitionName,
            String innovationArea,
            String innovationSector,
            List<InterviewAssignmentApplicationInviteSendRowViewModel> applications,
            InterviewAssignmentKeyStatisticsResource keyStatisticsResource,
            Pagination pagination,
            String originQuery,
            String content) {
        super(competitionId, competitionName, innovationArea, innovationSector, applications, keyStatisticsResource, pagination, originQuery);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationsSendViewModel that = (InterviewAssignmentApplicationsSendViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(content)
                .toHashCode();
    }
}