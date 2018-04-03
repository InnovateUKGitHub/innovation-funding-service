package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.commons.ZeroDowntime;

/**
 * TODO: this class needs to be removed
 * Bean to contain the question id that will be deleted in Competition Setup.
 */
@ZeroDowntime(reference = "IFS-2832", description = "Changed the endpoint. This class is not used anymore")
public class DeleteQuestionForm {
    private Long deleteQuestion;

    public Long getDeleteQuestion() {
        return deleteQuestion;
    }

    public void setDeleteQuestion(Long deleteQuestion) {
        this.deleteQuestion = deleteQuestion;
    }
}
