package org.innovateuk.ifs.competition.publiccontent.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * The resource for competition public content event.
 */
public class ContentEventResource {

    private Long id;

    private Long publicContent;

    private ZonedDateTime date;

    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublicContent() {
        return publicContent;
    }

    public void setPublicContent(Long publicContent) {
        this.publicContent = publicContent;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ContentEventResource resource = (ContentEventResource) o;

        return new EqualsBuilder()
                .append(id, resource.id)
                .append(publicContent, resource.publicContent)
                .append(date, resource.date)
                .append(content, resource.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(publicContent)
                .append(date)
                .append(content)
                .toHashCode();
    }
}
