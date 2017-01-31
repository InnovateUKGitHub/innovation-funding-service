package org.innovateuk.ifs.publiccontent.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form for the Eligibility page on public content setup.
 */
public class ContentGroupForm {

    private Long id;

    @NotEmpty
    private String heading;

    @NotEmpty
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
