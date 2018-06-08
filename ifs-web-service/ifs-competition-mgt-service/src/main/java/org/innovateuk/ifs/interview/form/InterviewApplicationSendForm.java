package org.innovateuk.ifs.interview.form;

import org.innovateuk.ifs.management.notification.form.SendInviteForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Form for sending invites to applicants to join the interview. Includes feedback file upload.
 */
public class InterviewApplicationSendForm extends SendInviteForm {

    private List<MultipartFile> feedback = new ArrayList<>();
    private Long attachFeedbackApplicationId;
    private Long removeFeedbackApplicationId;
    private int page = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MultipartFile> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<MultipartFile> feedback) {
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

    public MultipartFile getNotEmptyFile() {
        return feedback.stream().filter(Objects::nonNull).filter(((Predicate<MultipartFile>) MultipartFile::isEmpty).negate())
                .findAny().get();
    }
}
