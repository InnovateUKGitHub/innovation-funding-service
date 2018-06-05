package org.innovateuk.ifs.publiccontent.form;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form for the Eligibility page on public content setup.
 */
public class ContentGroupForm {

    private Long id;

    @NotBlank(message = "{validation.publiccontent.contentgroup.heading.required}")
    private String heading;

    @NotBlank(message = "{validation.publiccontent.contentgroup.content.required}")
    private String content;

    private MultipartFile attachment;

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

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

}
