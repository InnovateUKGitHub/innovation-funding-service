package org.innovateuk.ifs.threads.attachments.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.attachments.repository.PostAttachmentRepository;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class AttachmentLookupStrategy {
    //Todo Nuno: add mapper
    @Autowired
    private PostAttachmentRepository repository;

    @PermissionEntityLookupStrategy
    public AttachmentResource findById(final Long attachmentId) {
        return mapper.mapToResource(repository.findOne(attachmentId));
    }
}
