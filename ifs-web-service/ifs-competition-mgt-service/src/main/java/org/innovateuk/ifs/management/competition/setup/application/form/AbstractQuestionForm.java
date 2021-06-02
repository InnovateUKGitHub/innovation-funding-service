package org.innovateuk.ifs.management.competition.setup.application.form;

import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Abstract class for common functionality in application question and application project forms.
 */
public abstract class AbstractQuestionForm extends CompetitionSetupForm {

    public enum TypeOfQuestion {
        FREE_TEXT,
        MULTIPLE_CHOICE;
    }

    @Valid
    @NotNull
    private CompetitionSetupQuestionResource question;

    private MultipartFile templateDocumentFile;

    private TypeOfQuestion typeOfQuestion;

    private Integer numberOfUploads;

    public CompetitionSetupQuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(CompetitionSetupQuestionResource question) {
        this.question = question;
    }

    public TypeOfQuestion getTypeOfQuestion() {
        return typeOfQuestion;
    }

    public void setTypeOfQuestion(TypeOfQuestion typeOfQuestion) {
        this.typeOfQuestion = typeOfQuestion;
    }

    public boolean isRemovable() { return false; }

    public Integer getNumberOfUploads() {
        return numberOfUploads;
    }

    public void setNumberOfUploads(Integer numberOfUploads) {
        this.numberOfUploads = numberOfUploads;
    }


    public MultipartFile getTemplateDocumentFile() {
        return templateDocumentFile;
    }

    public void setTemplateDocumentFile(MultipartFile templateDocumentFile) {
        this.templateDocumentFile = templateDocumentFile;
    }

}
