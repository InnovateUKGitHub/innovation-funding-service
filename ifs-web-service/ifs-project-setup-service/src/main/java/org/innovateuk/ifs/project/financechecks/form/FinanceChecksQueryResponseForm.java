package org.innovateuk.ifs.project.financechecks.form;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueryResponseForm extends BaseBindingResultTarget {
    @NotBlank(message = "{validation.notesandqueries.response.required}")
    @Size(max = FinanceChecksQueryConstraints.MAX_QUERY_CHARACTERS, message = "{validation.notesandqueries.response.character.length.max}")
    @WordCount(max = FinanceChecksQueryConstraints.MAX_QUERY_WORDS, message = "{validation.notesandqueries.response.word.length.max}")
    private String body;

    private MultipartFile attachment;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinanceChecksQueryResponseForm that = (FinanceChecksQueryResponseForm) o;

        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        return attachment != null ? attachment.equals(that.attachment) : that.attachment == null;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(body)
                .append(attachment)
                .toHashCode();
    }
}