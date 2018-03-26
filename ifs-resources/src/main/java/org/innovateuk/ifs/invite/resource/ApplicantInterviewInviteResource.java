package org.innovateuk.ifs.invite.resource;

/**
 * A DTO which enables to the applicant interview invite to be sent.
 */
public class ApplicantInterviewInviteResource extends InviteResource {

    private String content;

    public ApplicantInterviewInviteResource() {
    }

    public ApplicantInterviewInviteResource(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}