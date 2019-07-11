package org.innovateuk.ifs.management.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.management.publiccontent.form.ContentGroupForm;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface PublicContentService {
    PublicContentResource getCompetitionById(final Long competitionId);
    ServiceResult<Void> publishByCompetitionId(Long publicContentId);
    ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section);
    ServiceResult<Void> markSectionAsComplete(PublicContentResource resource, PublicContentSectionType section);
    ServiceResult<Void> removeFile(AbstractContentGroupForm form);
    ServiceResult<Void> uploadFile(Long competitionId, PublicContentSectionType type, List<ContentGroupForm> contentGroups);
    ByteArrayResource downloadAttachment(Long contentGroupId);
    FileEntryResource getFileDetails(Long contentGroupId);
}
