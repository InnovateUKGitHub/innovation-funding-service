package org.innovateuk.ifs.threads.attachments.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class AttachmentLookupStrategy {
    @Autowired
    private AttachmentMapper mapper;

    @PermissionEntityLookupStrategy
    public AttachmentResource findById(final Long attachmentId) {
        return mapper.mapToResource(mapper.mapIdToDomain(attachmentId));
    }
}
