package org.innovateuk.ifs.competition.publiccontent.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PublicContentSectionResource that = (PublicContentSectionResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(publicContent, that.publicContent)
                .append(type, that.type)
                .append(status, that.status)
                .append(contentGroups, that.contentGroups)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(publicContent)
                .append(type)
                .append(status)
                .append(contentGroups)
                .toHashCode();
    }
}