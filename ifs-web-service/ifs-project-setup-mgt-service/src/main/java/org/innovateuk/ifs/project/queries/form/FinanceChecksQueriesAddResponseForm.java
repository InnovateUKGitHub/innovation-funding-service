package org.innovateuk.ifs.project.queries.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesAddResponseForm extends BaseBindingResultTarget {
    @NotBlank(message = "{validation.field.must.not.be.blank}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS, message = "{validation.field.max.word.count}")
    private String response;

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
