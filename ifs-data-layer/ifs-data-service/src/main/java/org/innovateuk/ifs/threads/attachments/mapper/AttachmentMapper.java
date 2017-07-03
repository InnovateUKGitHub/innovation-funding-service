package org.innovateuk.ifs.threads.attachments.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class
)
public abstract class AttachmentMapper extends BaseMapper<Attachment, AttachmentResource, Long> {

    @Override
    public AttachmentResource mapToResource(Attachment attachment) {
        return new AttachmentResource(attachment.id(), attachment.fileName(),
                attachment.mediaType(), attachment.sizeInBytes());
    }

    @Override
    public Attachment mapToDomain(AttachmentResource attachmentResource) {
        return super.mapIdToDomain(attachmentResource.id);
    }
}
