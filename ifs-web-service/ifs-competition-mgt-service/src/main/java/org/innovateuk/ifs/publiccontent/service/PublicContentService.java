package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;

import java.util.List;

public interface PublicContentService {
    PublicContentResource getCompetitionById(final Long competitionId);
    ServiceResult<Void> publishByCompetitionId(Long publicContentId);
    ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section);
    ServiceResult<Void> markSectionAsComplete(PublicContentResource resource, PublicContentSectionType section);
    ServiceResult<Void> removeFile(AbstractContentGroupForm form);
    ServiceResult<Void> uploadFile(PublicContentResource resource, PublicContentSectionType type, List<ContentGroupForm> contentGroups);
}
