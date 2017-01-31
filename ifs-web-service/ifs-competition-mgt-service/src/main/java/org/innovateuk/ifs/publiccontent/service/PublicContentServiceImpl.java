package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.form.ContentGroupForm;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private PublicContentRestService publicContentRestService;

    @Autowired
    private ContentGroupRestService contentGroupRestService;

    @Override
    public PublicContentResource getCompetitionById(Long id) {
        return publicContentRestService.getByCompetitionId(id).getSuccessObjectOrThrowException();
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
    public ServiceResult<Void> removeFile(PublicContentResource resource, PublicContentSectionType type, List<ContentGroupForm> contentGroups, Integer removeFile) {
        Optional<PublicContentSectionResource> optionalSection = resource.getContentSections().stream().filter(filterSection -> type.equals(filterSection.getType())).findAny();
        if (optionalSection.isPresent()) {
            Long groupToRemoveFrom = optionalSection.get().getContentGroups().get(removeFile).getId();
            return contentGroupRestService.removeFile(groupToRemoveFrom).toServiceResult();
        } else {
            return serviceFailure(PUBLIC_CONTENT_NOT_INITIALISED);
        }
    }

    @Override
    public ServiceResult<Void> uploadFile(PublicContentResource resource, PublicContentSectionType type, List<ContentGroupForm> contentGroups, Integer uploadFile, MultipartFile attachment) {
        Optional<PublicContentSectionResource> optionalSection = resource.getContentSections().stream().filter(filterSection -> type.equals(filterSection.getType())).findAny();
        if (optionalSection.isPresent()) {
            Long groupToRemoveFrom = optionalSection.get().getContentGroups().get(uploadFile).getId();
            try {
                return contentGroupRestService.uploadFile(groupToRemoveFrom, attachment.getContentType(), attachment.getSize(), attachment.getOriginalFilename(), attachment.getBytes()).toServiceResult();
            } catch (IOException e) {
                return serviceFailure(FILES_UNABLE_TO_CREATE_FILE);
            }
        } else {
            return serviceFailure(PUBLIC_CONTENT_NOT_INITIALISED);
        }
    }
}
