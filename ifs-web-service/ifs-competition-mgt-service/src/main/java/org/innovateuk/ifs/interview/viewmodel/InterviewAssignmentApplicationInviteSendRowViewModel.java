package org.innovateuk.ifs.interview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Holder of model attributes for the available applications shown in the 'Find' tab of the Assessment Interview Panel invite applications view.
 */
public class InterviewAssignmentApplicationInviteSendRowViewModel extends InterviewAssignmentApplicationInviteRowViewModel {

    private final String filename;

    public InterviewAssignmentApplicationInviteSendRowViewModel(long id, long applicationId, String applicationName, String leadOrganisation, String filename) {
        super(id, applicationId, applicationName, leadOrganisation);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public boolean hasAttachment() {
        return filename != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignmentApplicationInviteSendRowViewModel that = (InterviewAssignmentApplicationInviteSendRowViewModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(filename, that.filename)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(filename)
                .toHashCode();
    }
}