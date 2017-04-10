package org.innovateuk.ifs.threads.attachments.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * This class serves as a Wrapper around a {@link FileEntry}, adding attributes such as the User who
 * has uploaded this file and the date it was uploaded, while also giving a richer contextual naming.
 */
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    private User uploader;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private FileEntry fileEntry;

    @CreatedDate
    private ZonedDateTime createdOn;

    public Attachment() {}

    public Attachment(User uploader, FileEntry fileEntry) {
        this(null, uploader, fileEntry);
    }

    public Attachment(Long id, User uploader, FileEntry fileEntry) {
        this.id = id;
        this.uploader = uploader;
        this.fileEntry = fileEntry;
    }

    public Long id() {
        return id;
    }

    public boolean wasUploadedBy(Long userId) {
        return uploader.hasId(userId);
    }

    public String fileName() {
        return fileEntry.getName();
    }

    public String mediaType() {
        return fileEntry.getMediaType();
    }

    public long sizeInBytes() {
        return fileEntry.getFilesizeBytes();
    }

    public Long fileId() {
        return fileEntry.getId();
    }
}
