package org.innovateuk.ifs.project.queries.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesPostForm {
    public static final int MAX_QUERY_WORDS = 400;
    public static final int MAX_QUERY_CHARACTERS = 4000;

    @NotBlank(message = "{validation.notesandqueries.post.required}")
    @Size(max = MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = MAX_QUERY_WORDS, message = "{validation.notesandqueries.post.server.length.max}")
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
}
