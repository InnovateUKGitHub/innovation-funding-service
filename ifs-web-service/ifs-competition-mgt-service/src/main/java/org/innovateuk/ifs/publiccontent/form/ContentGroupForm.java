package org.innovateuk.ifs.publiccontent.form;

/**
 * Form for the Eligibility page on public content setup.
 */
public class ContentGroupForm {

    private Long id;

    private String heading;

    private String Content;

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
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
