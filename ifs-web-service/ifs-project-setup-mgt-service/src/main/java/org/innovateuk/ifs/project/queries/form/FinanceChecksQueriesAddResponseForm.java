package org.innovateuk.ifs.project.queries.form;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesAddResponseForm {
    @NotBlank(message = "{validation.notesandqueries.response.required}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS, message = "{validation.notesandqueries.response.server.length.max}")
    private String query;

    private MultipartFile attachment;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

        FinanceChecksQueriesAddResponseForm that = (FinanceChecksQueriesAddResponseForm) o;

        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        return attachment != null ? attachment.equals(that.attachment) : that.attachment == null;

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(query)
                .append(attachment)
                .toHashCode();
    }
}
