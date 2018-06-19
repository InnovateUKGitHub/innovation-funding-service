package org.innovateuk.ifs.interview.form;

import org.innovateuk.ifs.management.invite.form.SendInviteForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form for resending invites to applicants to join the interview. Includes feedback file upload.
 */
public class InterviewApplicationResendForm extends SendInviteForm {

    private MultipartFile feedback;

    private boolean removeFile;

    public MultipartFile getFeedback() {
        return feedback;
    }

    public void setFeedback(MultipartFile feedback) {
        this.feedback = feedback;
    }

    public boolean isRemoveFile() {
        return removeFile;
    }

    public void setRemoveFile(boolean removeFile) {
        this.removeFile = removeFile;
    }
}
