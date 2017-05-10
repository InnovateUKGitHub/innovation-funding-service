package org.innovateuk.ifs.project.financechecks.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueryResponseForm extends BaseBindingResultTarget {
    @NotBlank(message = "{validation.field.must.not.be.blank}")
    @Size(max = FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = FinanceChecksQueryConstraints.MAX_QUERY_WORDS, message = "{validation.field.max.word.count}")
    private String response;

    @JsonIgnore
    private MultipartFile attachment;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }
}
