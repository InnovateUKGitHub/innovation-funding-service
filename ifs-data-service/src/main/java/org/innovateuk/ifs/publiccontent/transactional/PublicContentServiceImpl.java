package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentStatus;
import org.innovateuk.ifs.publiccontent.domain.ContentSection;
import org.innovateuk.ifs.publiccontent.domain.Keyword;
import org.innovateuk.ifs.publiccontent.domain.PublicContent;
import org.innovateuk.ifs.publiccontent.mapper.PublicContentMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentSectionRepository;
import org.innovateuk.ifs.publiccontent.repository.KeywordRepository;
import org.innovateuk.ifs.publiccontent.repository.PublicContentRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_ALREADY_INITIALISED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for operations around the usage and processing of public content.
 */
@Service
public class PublicContentServiceImpl extends BaseTransactionalService implements PublicContentService {

    @Autowired
    private PublicContentRepository publicContentRepository;
    @Autowired
    private ContentSectionRepository contentSectionRepository;
    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private PublicContentMapper publicContentMapper;

    @Override
    public ServiceResult<PublicContentResource> findByCompetitionId(Long id) {
        return find(publicContentRepository.findByCompetitionId(id), notFoundError(PublicContent.class, id))
                .andOnSuccessReturn(publicContent -> publicContentMapper.mapToResource(publicContent));
    }

    @Override
    public ServiceResult<Void> initialiseByCompetitionId(Long competitionId) {
        if (publicContentRepository.findByCompetitionId(competitionId) != null) {
            return serviceFailure(PUBLIC_CONTENT_ALREADY_INITIALISED);
        }

        PublicContent publicContent = new PublicContent();
        publicContent.setCompetitionId(competitionId);
        publicContentRepository.save(publicContent);

        stream(PublicContentSectionType.values()).forEach(type -> {
            ContentSection contentSection = new ContentSection();
            contentSection.setPublicContent(publicContent);
            contentSection.setType(type);
            contentSection.setStatus(PublicContentStatus.IN_PROGRESS);
            contentSectionRepository.save(contentSection);
        });

        return ServiceResult.serviceSuccess();
    }

    @Override
    public ServiceResult<Void> publishByCompetitionId(Long competitionId) {
        return find(publicContentRepository.findByCompetitionId(competitionId), notFoundError(PublicContent.class, competitionId))
                .andOnSuccess(this::publish);

    }

    private ServiceResult<Void> publish(PublicContent publicContent) {
        if (allSectionsComplete(publicContent)) {
            publicContent.setPublishDate(LocalDateTime.now());
            return serviceSuccess();
        } else {
            return serviceFailure(PUBLIC_CONTENT_NOT_COMPLETE_TO_PUBLISH);
        }
    }

    private boolean allSectionsComplete(PublicContent publicContent) {
        Optional<ContentSection> incompleteSection = publicContent.getContentSections().stream()
                .filter(section -> PublicContentStatus.IN_PROGRESS.equals(section.getStatus()))
                .findAny();

        return !incompleteSection.isPresent();
    }

    @Override
    public ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSectionType section) {
        PublicContent publicContent = publicContentRepository.save(publicContentMapper.mapToDomain(resource));

        switch (section) {
            case SEARCH:
                saveKeywords(resource.getKeywords(), publicContent);
                break;
            case DATES:
                saveContentEvent();
                break;
            default:
                saveContentGroup();
                break;
        }

        markSectionAsComplete(publicContent, section);

        //If the public content has already been published then publish again.
        if (allSectionsComplete(publicContent) && publicContent.getPublishDate() != null) {
            publish(publicContent);
        }

        return serviceSuccess();
    }


    private void saveKeywords(List<String> keywords, PublicContent publicContent) {
        keywordRepository.deleteByPublicContentId(publicContent.getId());
        keywords.forEach(keyword -> {
            Keyword entity = new Keyword();
            entity.setKeyword(keyword);
            entity.setPublicContent(publicContent);
            keywordRepository.save(entity);
        });
    }

    private void saveContentGroup() {

    }

    private void saveContentEvent() {
    }

    private void markSectionAsComplete(PublicContent publicContent, PublicContentSectionType sectionType) {
        publicContent.getContentSections().stream()
                .filter(section -> sectionType.equals(section.getType()))
                .findAny()
                .ifPresent(section -> section.setStatus(PublicContentStatus.COMPLETE));
    }

}
