package org.innovateuk.ifs.project.queries.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.EnumValidator;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.notesandqueries.resource.thread.SectionTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesForm {

    public static final int MAX_QUERY_WORDS = 400;
    public static final int MAX_QUERY_CHARACTERS = 4000;
    public static final int MAX_TITLE_CHARACTERS = 255;
    public static final int MAX_SECTION_CHARACTERS = 255;

    @NotBlank(message = "{validation.notesandqueries.post.required}")
    @Size(max = MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = MAX_QUERY_WORDS, message = "{validation.notesandqueries.post.server.length.max}")
    private String query;

    @NotBlank(message = "{validation.notesandqueries.thread.title.required}")
    @Size(max = MAX_TITLE_CHARACTERS, message = "{validation.notesandqueries.thread.title.server.length.max}")
    private String title;

    @Size(max = MAX_SECTION_CHARACTERS, message = "{validation.notesandqueries.thread.section.server.length.max}")
    @EnumValidator( enumClazz=SectionTypeEnum.class, message="{validation.notesandqueries.thread.section.enum}")
    private String section;

    private MultipartFile attachment;

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FinanceChecksQueriesForm that = (FinanceChecksQueriesForm) o;

        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        return attachment != null ? attachment.equals(that.attachment) : that.attachment == null;

    }

    @Override
    public int hashCode() {
        int result = query != null ? query.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (attachment != null ? attachment.hashCode() : 0);
        return result;
    }
}
