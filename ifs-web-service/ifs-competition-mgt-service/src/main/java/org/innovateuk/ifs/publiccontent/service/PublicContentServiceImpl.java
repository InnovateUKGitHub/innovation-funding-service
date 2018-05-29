package org.innovateuk.ifs.publiccontent.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.publiccontent.form.AbstractContentGroupForm;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_CREATE_FILE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_NOT_INITIALISED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;


/**
 * Web service for public content.
 */
@Service
public class PublicContentServiceImpl implements PublicContentService {

    private static final Log LOG = LogFactory.getLog(PublicContentServiceImpl.class);

    @Autowired
    private PublicContentRestService publicContentRestService;

    @Autowired
    private ContentGroupRestService contentGroupRestService;

    @Override
    public PublicContentResource getCompetitionById(Long id) {
        return publicContentRestService.getByCompetitionId(id).getSuccess();
    }

    @Override
    public ServiceResult<Void> publishByCompetitionId(Long publicContentId) {
        return publicContentRestService.publishByCompetitionId(publicContentId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section) {
        return publicContentRestService.updateSection(resource, section).toServiceResult();
    }

    @Override
    public ServiceResult<Void> markSectionAsComplete(PublicContentResource resource, PublicContentSectionType section) {
        return publicContentRestService.markSectionAsComplete(resource, section).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeFile(AbstractContentGroupForm form) {
        return contentGroupRestService.removeFile(form.getRemoveFile()).toServiceResult();
    }

    @Override
    public ServiceResult<Void> uploadFile(Long competitionId, PublicContentSectionType type, List<ContentGroupForm> contentGroups) {
        PublicContentResource resource = getCompetitionById(competitionId);
        Optional<PublicContentSectionResource> optionalSection = CollectionFunctions.simpleFindFirst(resource.getContentSections(),
                contentSectionResource -> type.equals(contentSectionResource.getType()));
        Optional<ContentGroupForm> withAttachment = CollectionFunctions.simpleFindFirst(contentGroups,
                contentGroupForm -> contentGroupForm.getAttachment() != null && !contentGroupForm.getAttachment().isEmpty());

        if (optionalSection.isPresent() && withAttachment.isPresent()) {
            Long groupToUpload = getIdFromResourceIndex(optionalSection.get(), contentGroups, withAttachment.get());
            MultipartFile attachment = withAttachment.get().getAttachment();
            try {
                return contentGroupRestService.uploadFile(groupToUpload, attachment.getContentType(), attachment.getSize(), attachment.getOriginalFilename(), attachment.getBytes()).toServiceResult();
            } catch (IOException e) {
                LOG.error("unable to upload file", e);
                return serviceFailure(FILES_UNABLE_TO_CREATE_FILE);
            }
        } else {
            return serviceFailure(PUBLIC_CONTENT_NOT_INITIALISED);
        }
    }

    @Override
    public ByteArrayResource downloadAttachment(Long contentGroupId) {
        return contentGroupRestService.getFile(contentGroupId).getSuccess();
    }

    @Override
    public FileEntryResource getFileDetails(Long contentGroupId) {
        return contentGroupRestService.getFileDetails(contentGroupId).getSuccess();
    }


    private Long getIdFromResourceIndex(PublicContentSectionResource sectionResource, List<ContentGroupForm> contentGroups, ContentGroupForm contentGroupForm) {
        return sectionResource.getContentGroups().get(contentGroups.indexOf(contentGroupForm)).getId();
    }
}
