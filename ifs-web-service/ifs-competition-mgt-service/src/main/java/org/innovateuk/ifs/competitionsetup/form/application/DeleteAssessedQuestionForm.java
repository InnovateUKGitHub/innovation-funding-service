package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.commons.ZeroDowntime;

/**
 * Bean to contain the question id that will be deleted in Competition Setup.
 */
@ZeroDowntime(reference = "IFS-2832", description = "Changed the endpoint. TODO: This form needs to be removed")
public class DeleteAssessedQuestionForm {
    private Long deleteAssessedQuestion;

    public Long getDeleteAssessedQuestion() {
        return deleteAssessedQuestion;
    }

    public void setDeleteAssessedQuestion(Long deleteAssessedQuestion) {
        this.deleteAssessedQuestion = deleteAssessedQuestion;
    }
}
