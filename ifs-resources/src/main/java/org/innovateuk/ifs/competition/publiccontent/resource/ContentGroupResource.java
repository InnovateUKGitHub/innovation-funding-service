package org.innovateuk.ifs.competition.publiccontent.resource;


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

    private FileEntryResource fileEntryResource;

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

    public FileEntryResource getFileEntryResource() {
        return fileEntryResource;
    }

    public void setFileEntryResource(FileEntryResource fileEntryResource) {
        this.fileEntryResource = fileEntryResource;
    }

    public PublicContentSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(PublicContentSectionType sectionType) {
        this.sectionType = sectionType;
    }
}
