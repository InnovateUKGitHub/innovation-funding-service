package org.innovateuk.ifs.competition.publiccontent.resource;

public class PublicContentSectionResource {
    private Long id;
    private Long publicContent;
    private PublicContentSection type;

    private PublicContentStatus status;

    // TOOD private List<ContentGroup> contentGroup;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublicContentSection getType() {
        return type;
    }

    public void setType(PublicContentSection type) {
        this.type = type;
    }

    public PublicContentStatus getStatus() {
        return status;
    }

    public void setStatus(PublicContentStatus status) {
        this.status = status;
    }

    public Long getPublicContent() {
        return publicContent;
    }

    public void setPublicContent(Long publicContent) {
        this.publicContent = publicContent;
    }
}