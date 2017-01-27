package org.innovateuk.ifs.project.queries.form;

import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

/**
 * Created by danielsmith on 26/01/2017.
 */

public class FinanceChecksQueriesForm extends BaseBindingResultTarget {

    public static final int MAX_QUERY_WORDS = 400;
    public static final int MAX_QUERY_CHARACTERS = 4000;
    public static final int MAX_TITLE_CHARACTERS = 255;
    public static final int MAX_SECTION_CHARACTERS = 255;

    @Size(max = MAX_QUERY_CHARACTERS, message = "{validation.field.too.many.characters}")
    @WordCount(max = MAX_QUERY_WORDS, message = "{validation.field.max.word.count}")
    @NotBlank
    private String query;

    @NotBlank
    @Size(max = MAX_TITLE_CHARACTERS, message = "{validation.field.too.many.characters}")
    private String title;

    @Size(max = MAX_SECTION_CHARACTERS, message = "{validation.field.too.many.characters}")
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
}
