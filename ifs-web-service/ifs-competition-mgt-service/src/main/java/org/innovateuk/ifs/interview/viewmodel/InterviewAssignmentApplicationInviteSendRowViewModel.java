package org.innovateuk.ifs.interview.viewmodel;

import java.util.Objects;

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
        if (!super.equals(o)) return false;
        InterviewAssignmentApplicationInviteSendRowViewModel that = (InterviewAssignmentApplicationInviteSendRowViewModel) o;
        return Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), filename);
    }
}