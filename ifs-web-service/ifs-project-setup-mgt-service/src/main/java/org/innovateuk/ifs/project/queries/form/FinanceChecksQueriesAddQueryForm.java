package org.innovateuk.ifs.project.queries.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.EnumValidator;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

public class FinanceChecksQueriesAddQueryForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = FinanceChecksQueriesFormConstraints.MAX_QUERY_WORDS, message = "{validation.field.max.word.count}")
    private String query;

    @NotBlank(message = "{validation.field.must.not.be.blank}")
    @Size(max = FinanceChecksQueriesFormConstraints.MAX_TITLE_CHARACTERS, message = "{validation.field.too.many.characters}")
    private String queryTitle;

    @Size(max = FinanceChecksQueriesFormConstraints.MAX_SECTION_CHARACTERS, message = "{validation.field.too.many.characters}")
    @EnumValidator( enumClazz=FinanceChecksSectionType.class, message="{validation.notesandqueries.query.section.enum}")
    private String section;

    @JsonIgnore
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
