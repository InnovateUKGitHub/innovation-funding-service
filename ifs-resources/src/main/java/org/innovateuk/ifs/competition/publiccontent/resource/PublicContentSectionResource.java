package org.innovateuk.ifs.competition.publiccontent.resource;

import java.util.List;

/**
 * The resource for a public content section.
 */
public class PublicContentSectionResource {
    private Long id;
    private Long publicContent;
    private PublicContentSectionType type;
    private PublicContentStatus status;
    private List<ContentGroupResource> contentGroups;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublicContentSectionType getType() {
        return type;
    }

    public void setType(PublicContentSectionType type) {
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

    public List<ContentGroupResource> getContentGroups() {
        return contentGroups;
    }

    public void setContentGroups(List<ContentGroupResource> contentGroups) {
        this.contentGroups = contentGroups;
    }
}