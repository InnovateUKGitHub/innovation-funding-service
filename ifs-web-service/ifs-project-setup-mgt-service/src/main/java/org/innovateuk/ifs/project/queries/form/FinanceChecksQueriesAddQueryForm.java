package org.innovateuk.ifs.project.queries.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.EnumValidator;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesAddQueryForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.notesandqueries.query.required}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS, message = "{validation.notesandqueries.query.character.length.max}")
    @WordCount(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS, message = "{validation.notesandqueries.query.word.length.max}")
    private String query;

    @NotBlank(message = "{validation.notesandqueries.query.title.required}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_TITLE_CHARACTERS, message = "{validation.notesandqueries.query.title.length.max}")
    private String queryTitle;

    @Size(max = FinanceChecksQueriesFormConstraints.MAX_SECTION_CHARACTERS, message = "{validation.notesandqueries.query.section.length.max}")
    @EnumValidator( enumClazz=FinanceChecksSectionType.class, message="{validation.notesandqueries.query.section.enum}")
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

    public String getQueryTitle() {
        return queryTitle;
    }

    public void setQueryTitle(String title) {
        this.queryTitle = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

}
