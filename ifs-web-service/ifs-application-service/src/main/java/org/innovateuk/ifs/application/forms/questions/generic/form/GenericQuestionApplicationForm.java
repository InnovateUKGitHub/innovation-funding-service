package org.innovateuk.ifs.application.forms.questions.generic.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class GenericQuestionApplicationForm {

    @NotBlank(message = "{validation.field.please.enter.some.text}")
    private String answer;

    private MultipartFile appendix;

    private MultipartFile templateDocument;

    public String getAnswer() {
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
}
