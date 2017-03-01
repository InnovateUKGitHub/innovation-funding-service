package org.innovateuk.ifs.threads.attachments.repository;

import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AttachmentRepository extends PagingAndSortingRepository<Attachment, Long> {
}
