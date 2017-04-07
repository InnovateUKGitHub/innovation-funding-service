package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.PostResource;

import java.time.ZonedDateTime;
import java.util.List;

public class ThreadPostViewModel extends PostResource {
    private String username;

    public ThreadPostViewModel(Long id, UserResource author, String body, List<AttachmentResource> attachments, ZonedDateTime createdOn) {
        super(id, author, body, attachments, createdOn);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
