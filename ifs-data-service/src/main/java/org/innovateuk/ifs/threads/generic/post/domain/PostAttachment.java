package org.innovateuk.ifs.threads.generic.post.domain;

import org.innovateuk.ifs.file.domain.FileEntry;

import javax.persistence.Entity;

@Entity
public final class PostAttachment {
    private Post post;
    private FileEntry file;
    
}
