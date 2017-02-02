package org.innovateuk.ifs.competition.publiccontent.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;

/**
 * The resource for competition public content.
 */
public class ContentGroupResource {
    private Long id;

    private Long contentSectionId;

    private PublicContentSectionType sectionType;

    private String heading;

    private String content;

    private Integer priority;

    private FileEntryResource fileEntry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentSectionId() {
        return contentSectionId;
    }

    public void setContentSectionId(Long contentSectionId) {
        this.contentSectionId = contentSectionId;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public PublicContentSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(PublicContentSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public FileEntryResource getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntryResource fileEntry) {
        this.fileEntry = fileEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ContentGroupResource that = (ContentGroupResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(contentSectionId, that.contentSectionId)
                .append(sectionType, that.sectionType)
                .append(heading, that.heading)
                .append(content, that.content)
                .append(priority, that.priority)
                .append(fileEntry, that.fileEntry)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(contentSectionId)
                .append(sectionType)
                .append(heading)
                .append(content)
                .append(priority)
                .append(fileEntry)
                .toHashCode();
    }
}
