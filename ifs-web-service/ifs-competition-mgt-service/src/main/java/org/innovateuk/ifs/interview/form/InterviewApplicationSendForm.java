package org.innovateuk.ifs.interview.form;

import org.innovateuk.ifs.management.form.SendInviteForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form for sending competition invites
 */
public class InterviewApplicationSendForm extends SendInviteForm {

    private MultipartFile feedback;
    private Long attachFeedbackApplicationId;
    private Long removeFeedbackApplicationId;


    public MultipartFile getFeedback() {
        return feedback;
    }

    public void setFeedback(MultipartFile feedback) {
        this.feedback = feedback;
    }

    public Long getAttachFeedbackApplicationId() {
        return attachFeedbackApplicationId;
    }

    public void setAttachFeedbackApplicationId(Long attachFeedbackApplicationId) {
        this.attachFeedbackApplicationId = attachFeedbackApplicationId;
    }

    public Long getRemoveFeedbackApplicationId() {
        return removeFeedbackApplicationId;
    }

    public void setRemoveFeedbackApplicationId(Long removeFeedbackApplicationId) {
        this.removeFeedbackApplicationId = removeFeedbackApplicationId;
    }

}
