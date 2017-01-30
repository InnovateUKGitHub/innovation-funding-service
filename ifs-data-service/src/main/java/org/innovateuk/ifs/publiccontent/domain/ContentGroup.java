package org.innovateuk.ifs.publiccontent.domain;

import org.innovateuk.ifs.file.domain.FileEntry;

import javax.persistence.*;

/**
 * A customisable content group of fields.
 */
@Entity
public class ContentGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_section_id", referencedColumnName = "id")
    private ContentSection contentSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_entry_id", referencedColumnName = "id")
    private FileEntry fileEntry;

    private String heading;

    @Column(length=5000)
    private String content;

    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContentSection getContentSection() {
        return contentSection;
    }

    public void setContentSection(ContentSection contentSection) {
        this.contentSection = contentSection;
    }

    public FileEntry getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry;
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
}
