package org.innovateuk.ifs.application.forms.questions.generic.form;

import org.springframework.web.multipart.MultipartFile;

public class GenericQuestionApplicationForm {

    private String answer;

    private MultipartFile appendix;

    private MultipartFile templateDocument;

    private boolean textAreaActive;

    private boolean multipleChoiceOptionsActive;

    private Long multipleChoiceOptionId;

    public String getAnswer() {
        if (multipleChoiceOptionsActive && answer == null) {
            answer = "";
        }
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public MultipartFile getAppendix() {
        return appendix;
    }

    public void setAppendix(MultipartFile appendix) {
        this.appendix = appendix;
    }

    public MultipartFile getTemplateDocument() {
        return templateDocument;
    }

    public void setTemplateDocument(MultipartFile templateDocument) {
        this.templateDocument = templateDocument;
    }

    public boolean isTextAreaActive() {
        return textAreaActive;
    }

    public void setTextAreaActive(boolean textAreaActive) {
        this.textAreaActive = textAreaActive;
    }

    public boolean isMultipleChoiceOptionsActive() {
        return multipleChoiceOptionsActive;
    }

    public void setMultipleChoiceOptionsActive(boolean multipleChoiceOptionsActive) {
        this.multipleChoiceOptionsActive = multipleChoiceOptionsActive;
    }

    public Long getMultipleChoiceOptionId() {
        return multipleChoiceOptionId;
    }

    public void setMultipleChoiceOptionId(Long multipleChoiceOptionId) {
        this.multipleChoiceOptionId = multipleChoiceOptionId;
    }
}
